package com.rfz.appflotal.data.repository.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.os.Build
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.AppLog
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

interface BluetoothRepository {
    var sensorData: StateFlow<BluetoothData>

    val scannedDevices: StateFlow<ScanItem?>

    val btReceiver: BroadcastReceiver

    suspend fun connect(macAddress: String)

    fun disconnect()

    fun startScan()

    fun stopScan()
}

private val bleFrameBuffer = mutableListOf<Byte>()

private const val HEADER_1 = 0xAA
private const val HEADER_2 = 0xA1
private const val LENGTH_INDEX = 3
private const val MIN_FRAME_LENGTH = 5


data class BluetoothData(
    val dataFrame: String? = null,
    val bluetoothSignalQuality: BluetoothSignalQuality = BluetoothSignalQuality.Desconocida,
    val rssi: Int? = null,
    val timestamp: String? = null,
    val isBluetoothOn: Boolean = false
)

enum class BluetoothSignalQuality(
    @StringRes val signalText: Int? = null,
    @StringRes val alertMessage: Int? = null
) {
    Excelente(R.string.excelente), Aceptable(R.string.aceptable), Pobre(R.string.pobre),
    Desconocida(R.string.sin_conexi_n, R.string.aviso_conexion_blueotooth)
}

class BluetoothRepositoryImp @Inject constructor(private val context: Context) :
    BluetoothRepository {
    private var ready = false
    private var lastMacAddress: String? = null

    private var isConnected: Boolean = false

    private var bluetoothGatt: BluetoothGatt? = null
    private val mutex = Mutex()

    private var rssiJob: Job? = null
    private var reconnectJob: Job? = null

    private val bluetoothManager: BluetoothManager? by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val bluetoothScanner by lazy {
        BluetoothScannerImp(bluetoothAdapter)
    }

    private var _sensorData: MutableStateFlow<BluetoothData> = MutableStateFlow(BluetoothData())

    override var sensorData: StateFlow<BluetoothData> = _sensorData.asStateFlow()

    override val scannedDevices: StateFlow<ScanItem?> = bluetoothScanner.resultScanDevices

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val gattCallback = object : BluetoothGattCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    isConnected = true
                    startRSSIMonitoring()
                    gatt.requestMtu(247)
                    gatt.discoverServices()
                    AppLog.d("BLE", "Conectado. Descubriendo servicios...")
                }


                BluetoothProfile.STATE_DISCONNECTED -> {
                    isConnected = false
                    stopRSSIMonitoring()

                    try {
                        gatt.close()
                    } catch (_: Exception) {
                    }
                    if (bluetoothGatt === gatt) {
                        bluetoothGatt = null
                    }

                    _sensorData.update { currentState ->
                        currentState.copy(
                            dataFrame = null,
                            bluetoothSignalQuality = BluetoothSignalQuality.Desconocida,
                            rssi = null,
                            timestamp = null
                        )
                    }

                    // reintento controlado
                    reconnectJob?.cancel()
                    reconnectJob = lastMacAddress?.let { mac ->
                        scope.launch {
                            delay(5000.milliseconds)
                            connect(mac)
                        }
                    }

                    AppLog.d("BLE", "Conexion perdidad")
                }

                BluetoothProfile.STATE_DISCONNECTING -> {
                    AppLog.d("BLE", "Disconectando de la red.")
                }

                BluetoothProfile.STATE_CONNECTING -> {
                    AppLog.d("BLE", "Conectando de la red.")
                }
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                AppLog.e("BLE_TRAMA", "onServicesDiscovered con status de error: $status")
                return
            }

            AppLog.d(
                "BLE_TRAMA",
                "Servicios descubiertos: ${gatt.services.joinToString { it.uuid.toString() }}"
            )

            val service = gatt.getService(UUID.fromString("00001000-0000-1000-8000-00805f9b34fb"))
            val serviceBle5 =
                gatt.getService(UUID.fromString("0000A002-0000-1000-8000-00805F9B34FB"))

            try {
                when {
                    serviceBle5 != null -> {
                        // BLE 5
                        AppLog.d("BLE_TRAMA", "Servicio BLE5 (0000A002) encontrado")
                        val notifyChar =
                            serviceBle5.getCharacteristic(UUID.fromString("0000C306-0000-1000-8000-00805F9B34FB"))

                        if (notifyChar == null) {
                            AppLog.e("BLE_TRAMA", "Característica 0000C306 no encontrada en el servicio BLE5")
                            return
                        }

                        gatt.setCharacteristicNotification(notifyChar, true)

                        val descriptor = notifyChar.getDescriptor(
                            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
                        )

                        if (descriptor == null) {
                            AppLog.e("BLE_TRAMA", "Descriptor CCCD no encontrado para 0000C306")
                            return
                        }

                        descriptor.value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                        val writeStarted = gatt.writeDescriptor(descriptor)
                        AppLog.d("BLE_TRAMA", "writeDescriptor(CCCD indicación 0000C306) iniciado: $writeStarted")
                    }

                    service != null -> {
                        // BLE 4
                        AppLog.d("BLE_TRAMA", "Servicio BLE4 (00001000) encontrado")
                        val characteristic =
                            service.getCharacteristic(UUID.fromString("00001002-0000-1000-8000-00805f9b34fb")) // UUID característico

                        if (characteristic == null) {
                            AppLog.e("BLE_TRAMA", "Característica 00001002 no encontrada en el servicio BLE4")
                            return
                        }

                        gatt.setCharacteristicNotification(characteristic, true)

                        val descriptor = characteristic
                            .getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))

                        if (descriptor == null) {
                            AppLog.e("BLE_TRAMA", "Descriptor CCCD no encontrado para 00001002")
                            return
                        }

                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        val writeStarted = gatt.writeDescriptor(descriptor)
                        AppLog.d("BLE_TRAMA", "writeDescriptor(CCCD notificación 00001002) iniciado: $writeStarted")
                    }

                    else -> {
                        AppLog.e(
                            "BLE_TRAMA",
                            "Ningún servicio compatible encontrado. Servicios disponibles: ${gatt.services.joinToString { it.uuid.toString() }}"
                        )
                    }
                }
            } catch (e: SecurityException) {
                AppLog.e("BLE_TRAMA", "Sin permiso BLUETOOTH_CONNECT al suscribir características", e)
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            AppLog.d(
                "BLE_TRAMA",
                "Trama cruda recibida de ${characteristic.uuid} (${value.size} bytes): ${value.toPrettyHex()}"
            )
            val dataframes = processIncomingDataFrames(value)
            if (dataframes.isEmpty()) {
                AppLog.d("BLE_TRAMA", "No se extrajo ninguna trama válida de este paquete")
            }
            dataframes.forEach {
                AppLog.d("BLE_TRAMA", "Trama entregada a sensorData: $it")
                _sensorData.update { currentState ->
                    currentState.copy(
                        dataFrame = it,
                        timestamp = getCurrentDate()
                    )
                }
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onReadRemoteRssi(
            gatt: BluetoothGatt?,
            rssi: Int,
            status: Int
        ) {
            super.onReadRemoteRssi(gatt, rssi, status)
            AppLog.d("BLE", "Rssi: $rssi Status: $status")
            _sensorData.update { currentState ->
                currentState.copy(
                    bluetoothSignalQuality = rssiToQuality(rssi),
                    rssi = rssi
                )
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            Log.d("BLE", "MTU negociado: $mtu")
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
            val charUuid = descriptor?.characteristic?.uuid
            if (status == BluetoothGatt.GATT_SUCCESS) {
                ready = true
                AppLog.d("BLE_TRAMA", "✅ Suscripción confirmada (CCCD OK) para $charUuid")
            } else {
                val hint = when (status) {
                    5, 15 -> " (posible falta de emparejamiento/bonding con el dispositivo)"
                    else -> ""
                }
                AppLog.e("BLE_TRAMA", "❌ Error escribiendo CCCD para $charUuid: status $status$hint")
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override suspend fun connect(macAddress: String) {
        reconnectJob?.cancel()
        reconnectJob = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            AppLog.e("BLE_TRAMA", "No se puede conectar: falta el permiso BLUETOOTH_CONNECT")
            return
        }

        scope.launch {
            mutex.withLock {
                val regex = Regex("^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$")
                if (!regex.matches(macAddress)) {
                    AppLog.e("BluetoothRepository", "Bluetooth Address is not valid")
                    return@withLock
                }

                lastMacAddress = macAddress

                bluetoothGatt?.let {
                    try {
                        it.disconnect()
                        it.close()
                    } catch (_: Exception) {
                    }
                }
                bluetoothGatt = null
                stopRSSIMonitoring()

                try {
                    val device = bluetoothAdapter?.getRemoteDevice(macAddress)
                    bluetoothGatt = device?.connectGatt(context, false, gattCallback)
                    AppLog.d("BLE_TRAMA", "connectGatt solicitado para $macAddress")
                } catch (e: SecurityException) {
                    AppLog.e("BLE_TRAMA", "Sin permiso para conectar a $macAddress", e)
                }

            }
        }
    }

    @SuppressLint("MissingPermission")
    override val btReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                    BluetoothAdapter.STATE_OFF -> {
                        AppLog.d("BluetoothRepositoriy", "BT OFF -> limpiar")
                        _sensorData.update { currentState ->
                            currentState.copy(isBluetoothOn = false)
                        }
                        disconnect()
                    }

                    BluetoothAdapter.STATE_ON -> {
                        AppLog.d("BluetoothRepository", "BT ON -> intentar reconectar")
                        _sensorData.update { currentState ->
                            currentState.copy(isBluetoothOn = true)
                        }
                        reconnectJob?.cancel()
                        reconnectJob = lastMacAddress?.let { mac ->
                            scope.launch {
                                delay(2000.milliseconds)
                                connect(mac)
                            }
                        }
                    }
                }
            } else if (action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    @Suppress("NewApi")
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }
                val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE)
                AppLog.d("BluetoothRepository", "Bond state changed for ${device?.address}: $bondState")
                if (bondState == BluetoothDevice.BOND_NONE && device?.address == lastMacAddress) {
                    AppLog.d("BluetoothRepository", "Bond lost with current device, attempting to reconnect...")
                }
            } else if (action == "android.bluetooth.device.action.KEY_MISSING") { // ACTION_KEY_MISSING (API 36)
                val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    @Suppress("NewApi")
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }
                if (device?.address == lastMacAddress) {
                    AppLog.w("BluetoothRepository", "Android 16: Bond loss detected (KEY_MISSING).")
                }
            } else if (action == "android.bluetooth.device.action.ENCRYPTION_CHANGE") { // ACTION_ENCRYPTION_CHANGE (API 36)
                AppLog.d("BluetoothRepository", "Android 16: Encryption status changed.")
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun disconnect() {
        reconnectJob?.cancel()
        reconnectJob = null
        lastMacAddress = null

        scope.launch {
            mutex.withLock {
                stopRSSIMonitoring()
                bluetoothGatt?.let {
                    try {
                        it.disconnect()
                        it.close()
                    } catch (_: Exception) {
                    }
                }
                bluetoothGatt = null
                isConnected = false
            }
        }

        _sensorData.update { currentState ->
            currentState.copy(
                dataFrame = null,
                bluetoothSignalQuality = BluetoothSignalQuality.Desconocida,
                rssi = null,
                timestamp = null
            )
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun startScan() {
        bluetoothScanner.scanDevices()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun stopScan() {
        bluetoothScanner.stopScan()
    }

    @SuppressLint("MissingPermission")
    private fun startRSSIMonitoring() {
        rssiJob?.cancel()
        rssiJob = scope.launch {
            while (isActive) {
                delay(5000.milliseconds)
                bluetoothGatt?.let { gatt ->
                    try {
                        gatt.readRemoteRssi()
                    } catch (e: Exception) {
                        AppLog.w("BluetoothRepository", "readRemoteRssi error: ${e.message}")
                    }
                } ?: AppLog.d("BluetoothRepository", "No GATT al leer RSSI")
            }
        }
    }

    private fun stopRSSIMonitoring() {
        rssiJob?.cancel()
        rssiJob = null
    }

    private fun ByteArray.toHexString(): String =
        joinToString("") { "%02x".format(it) }

    private fun ByteArray.toPrettyHex(): String =
        joinToString("-") { "%02X".format(it) }

    private fun rssiToQuality(rssi: Int?): BluetoothSignalQuality {
        return if (rssi == null) BluetoothSignalQuality.Desconocida
        else when (rssi) {
            in -55..0 -> BluetoothSignalQuality.Excelente
            in -85..-56 -> BluetoothSignalQuality.Aceptable
            in -100..-86 -> BluetoothSignalQuality.Pobre
            else -> BluetoothSignalQuality.Desconocida
        }
    }

    private fun processIncomingDataFrames(dataFrame: ByteArray): List<String> {
        val validFrames = mutableListOf<String>()

        bleFrameBuffer.addAll(dataFrame.toList())

        while (bleFrameBuffer.size >= MIN_FRAME_LENGTH) {

            val headerIndex = findHeaderIndex(bleFrameBuffer)

            if (headerIndex == -1) {
                val discarded = bleFrameBuffer.toByteArray()
                AppLog.d("BLE_TRAMA", "Sin header válido, descartando: ${discarded.toPrettyHex()}")

//                scope.launch {
//                    bleLogRepository.sendDataFrame(discarded)
//                }

                bleFrameBuffer.clear()
                break
            }

            if (headerIndex > 0) {
                val discarded = bleFrameBuffer.take(headerIndex).toByteArray()
                AppLog.d("BLE_TRAMA", "Bytes basura antes del header: ${discarded.toPrettyHex()}")

//                scope.launch {
//                    bleLogRepository.sendDataFrame(discarded)
//                }

                repeat(headerIndex) {
                    bleFrameBuffer.removeAt(0)
                }
            }

            if (bleFrameBuffer.size <= LENGTH_INDEX) {
                break
            }

            val expectedLength = bleFrameBuffer[LENGTH_INDEX].toUByte().toInt()

            if (expectedLength < MIN_FRAME_LENGTH) {
                val invalidByte = bleFrameBuffer.removeAt(0)
                AppLog.d("BLE_TRAMA", "Longitud inválida, descartando byte: ${byteToHex(invalidByte)}")
                continue
            }

            if (bleFrameBuffer.size < expectedLength) {
                AppLog.d(
                    "BLE_TRAMA",
                    "Trama incompleta. Esperados: $expectedLength, recibidos: ${bleFrameBuffer.size}"
                )
                break
            }

            val frame = bleFrameBuffer.take(expectedLength).toByteArray()
            val frameHex = frame.toHexString()

            if (isValidFrame(frame)) {
                AppLog.d("BLE_TRAMA", "Trama correcta (${frame.size} bytes): ${frame.toPrettyHex()}")
                validFrames.add(frameHex)

                repeat(expectedLength) {
                    bleFrameBuffer.removeAt(0)
                }
            } else {
                AppLog.d(
                    "BLE_TRAMA",
                    "Trama incorrecta, intentando resincronizar: ${frame.toPrettyHex()}"
                )

//                scope.launch {
//                    bleLogRepository.sendDataFrame(frameHex)
//                }

                bleFrameBuffer.removeAt(0)
            }
        }

        return validFrames
    }

    private fun findHeaderIndex(buffer: List<Byte>): Int {
        for (i in 0 until buffer.size - 1) {
            val current = buffer[i].toUByte().toInt()
            val next = buffer[i + 1].toUByte().toInt()

            if (current == HEADER_1 && next == HEADER_2) {
                return i
            }
        }

        return -1
    }

    private fun isValidFrame(frame: ByteArray): Boolean {
        if (frame.size < MIN_FRAME_LENGTH) return false

        val frameHex = frame.toHexString()

        val expectedLength = frame[LENGTH_INDEX].toUByte().toInt()

        if (frame.size != expectedLength) {
            AppLog.d("BLE_TRAMA", "Longitud incorrecta. Esperada: $expectedLength, real: ${frame.size}")
            return false
        }

        val calculatedChecksum = frame
            .dropLast(1)
            .sumOf { it.toUByte().toInt() } % 256

        val receivedChecksum = frame.last().toUByte().toInt()

        val temperatureOk = verifyTemperature(frameHex)

        AppLog.d(
            "BLE_TRAMA",
            "Trama: ${frame.toPrettyHex()} | checksum calculado: $calculatedChecksum, recibido: $receivedChecksum | temperatura válida: $temperatureOk"
        )

        return calculatedChecksum == receivedChecksum && temperatureOk
    }

    private fun byteToHex(byte: Byte): String {
        return "%02x".format(byte.toUByte().toInt())
    }
}