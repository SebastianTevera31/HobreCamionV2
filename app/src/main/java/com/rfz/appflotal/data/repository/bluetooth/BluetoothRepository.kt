package com.rfz.appflotal.data.repository.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.annotation.StringRes
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
                    lastMacAddress?.let { mac ->
                        scope.launch {
                            delay(5000)
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
            val service = gatt.getService(UUID.fromString("00001000-0000-1000-8000-00805f9b34fb"))
            val serviceBle5 =
                gatt.getService(UUID.fromString("0000A002-0000-1000-8000-00805F9B34FB"))

            when {
                serviceBle5 != null -> {
                    // BLE 5
                    val notifyChar =
                        serviceBle5.getCharacteristic(UUID.fromString("0000C306-0000-1000-8000-00805F9B34FB"))

                    if (notifyChar != null) {
                        gatt.setCharacteristicNotification(notifyChar, true)

                        val descriptor = notifyChar.getDescriptor(
                            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
                        )

                        descriptor.value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                        gatt.writeDescriptor(descriptor)
                    }
                }

                service != null -> {
                    // BLE 4
                    val characteristic =
                        service.getCharacteristic(UUID.fromString("00001002-0000-1000-8000-00805f9b34fb")) // UUID característico
                    if (characteristic != null) {
                        gatt.setCharacteristicNotification(characteristic, true)

                        val descriptor = characteristic
                            .getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        gatt.writeDescriptor(descriptor)
                    }
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            _sensorData.update { currentState ->
                currentState.copy(
                    dataFrame = verifyDataFrame(value),
                    timestamp = getCurrentDate()
                )
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
            if (status == BluetoothGatt.GATT_SUCCESS) {
                ready = true
                AppLog.d("BLE", "✅ Suscripción confirmada (CCCD OK)")
            } else {
                AppLog.e("BLE", "❌ Error escribiendo CCCD: $status")
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override suspend fun connect(macAddress: String) {
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

                val device = bluetoothAdapter?.getRemoteDevice(macAddress)
                bluetoothGatt = device?.connectGatt(context, false, gattCallback)

            }
        }
    }

    @SuppressLint("MissingPermission")
    override val btReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED || intent?.action == BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED) {
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
                        lastMacAddress?.let { mac ->
                            scope.launch {
                                delay(2000)
                                connect(mac)
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun disconnect() {
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
        lastMacAddress = null
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
                delay(5000)
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

    private fun verifyDataFrame(dataFrame: ByteArray): String? {
        val dataFrameToHex = dataFrame.toHexString()
        AppLog.d("BLE", "Current dataframe $dataFrameToHex")
        // Verificación de longitud
        if (dataFrameToHex.length == 28) {
            val calculatedDataFrame =
                dataFrame.dropLast(1).sumOf { data -> data.toUByte().toInt() } % 256

            AppLog.d("BLE", "CalculatedDataFrame $calculatedDataFrame")
            AppLog.d("BLE", "CheckSum ${dataFrame.last().toUByte().toInt()}")

            // Verificación de CheckSum
            if (calculatedDataFrame == dataFrame.last().toUByte().toInt()
                && verifyTemperature(dataFrameToHex)
            ) {
                AppLog.d("BLE", "Trama correcta")
                return dataFrameToHex
            } else AppLog.d("BLE", "Trama incorrecta")
        }
        return null
    }

    private fun ByteArray.toHexString(): String =
        joinToString("") { "%02x".format(it) }

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
                val discarded = bleFrameBuffer.toByteArray().toHexString()
                AppLog.d("BLE", "Sin header válido, descartando: $discarded")

//                scope.launch {
//                    bleLogRepository.sendDataFrame(discarded)
//                }

                bleFrameBuffer.clear()
                break
            }

            if (headerIndex > 0) {
                val discarded = bleFrameBuffer.take(headerIndex).toByteArray().toHexString()
                AppLog.d("BLE", "Bytes basura antes del header: $discarded")

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
                AppLog.d("BLE", "Longitud inválida, descartando byte: ${byteToHex(invalidByte)}")
                continue
            }

            if (bleFrameBuffer.size < expectedLength) {
                AppLog.d(
                    "BLE",
                    "Trama incompleta. Esperados: $expectedLength, recibidos: ${bleFrameBuffer.size}"
                )
                break
            }

            val frame = bleFrameBuffer.take(expectedLength).toByteArray()
            val frameHex = frame.toHexString()

            if (isValidFrame(frame)) {
                AppLog.d("BLE", "Trama correcta: $frameHex")
                validFrames.add(frameHex)

                repeat(expectedLength) {
                    bleFrameBuffer.removeAt(0)
                }
            } else {
                AppLog.d("BLE", "Trama incorrecta, intentando resincronizar: $frameHex")

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
            AppLog.d("BLE", "Longitud incorrecta. Esperada: $expectedLength, real: ${frame.size}")
            return false
        }

        val calculatedChecksum = frame
            .dropLast(1)
            .sumOf { it.toUByte().toInt() } % 256

        val receivedChecksum = frame.last().toUByte().toInt()

        AppLog.d("BLE", "Current dataframe $frameHex")
        AppLog.d("BLE", "CalculatedDataFrame $calculatedChecksum")
        AppLog.d("BLE", "CheckSum $receivedChecksum")

        return calculatedChecksum == receivedChecksum &&
                verifyTemperature(frameHex)
    }

    private fun byteToHex(byte: Byte): String {
        return "%02x".format(byte.toUByte().toInt())
    }
}