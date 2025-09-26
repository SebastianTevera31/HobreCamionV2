package com.rfz.appflotal.data.repository.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.annotation.StringRes
import androidx.room.concurrent.AtomicBoolean
import com.rfz.appflotal.R
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
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.UUID
import javax.inject.Inject

interface BluetoothRepository {
    var sensorData: StateFlow<BluetoothData>

    val scannedDevices: StateFlow<ScanItem?>
    suspend fun connect(macAddress: String)
    suspend fun disconnect()

    fun startScan()

    fun stopScan()
    fun startRssiPolling(intervalsMs: Long = 2000L): Job
}

data class BluetoothData(
    val dataFrame: String? = null,
    val bluetoothSignalQuality: BluetoothSignalQuality = BluetoothSignalQuality.Desconocida,
    val rssi: Int? = null,
    val timestamp: String? = null
)

enum class BluetoothSignalQuality(@StringRes val signalText: Int? = null) {
    Excelente(R.string.excelente), Aceptable(R.string.aceptable), Pobre(R.string.pobre), Desconocida(
        R.string.sin_conexi_n
    )
}

class BluetoothRepositoryImp @Inject constructor(private val context: Context) :
    BluetoothRepository {
    private var ready = false
    private var device: BluetoothDevice? = null
    private var currentMac: String? = null

    private var connecting = AtomicBoolean(false)
    private var bluetoothGatt: BluetoothGatt? = null
    private val mutex = Mutex()

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
                    Log.d("BLE", "Conectado. Descubriendo servicios...")
                    gatt.discoverServices()
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d("BLE", "Conexion perdidad")
                    _sensorData.update { currentState ->
                        currentState.copy(
                            dataFrame = null,
                            bluetoothSignalQuality = BluetoothSignalQuality.Desconocida,
                            rssi = null,
                            timestamp = null
                        )
                    }

                    ready = false
                    safeCloseGatt(gatt)
                }
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val service = gatt.getService(UUID.fromString("00001000-0000-1000-8000-00805f9b34fb"))
            val characteristic =
                service?.getCharacteristic(UUID.fromString("00001002-0000-1000-8000-00805f9b34fb")) // UUID característico

            if (characteristic != null) {
                gatt.setCharacteristicNotification(characteristic, true)

                val descriptor = characteristic
                    .getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)
                ready = (status == BluetoothGatt.GATT_SUCCESS)
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
            Log.d("BLE", "Rssi: $rssi Status: $status")
            _sensorData.update { currentState ->
                currentState.copy(
                    bluetoothSignalQuality = rssiToQuality(rssi),
                    rssi = rssi
                )
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override suspend fun connect(macAddress: String) {
        require(Regex("^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$").matches(macAddress)) {
            Log.e("BluetoothRepository", "Bluetooth Address is not valid")
        }

        if (macAddress != currentMac) {
            // Verificamos si exista una instancia previa
            scope.launch {
                awaitDisconnectAndCloseLocked(timeoutMs = 4000)
                device = bluetoothAdapter?.getRemoteDevice(macAddress)

                currentMac = macAddress
                val dev = device ?: error("Dispositivo no encontrado")

                bluetoothGatt = dev.connectGatt(context, false, gattCallback)
                ready = false
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun awaitDisconnectAndCloseLocked(timeoutMs: Long) {
        val gatt = bluetoothGatt ?: return
        Log.d("BLE", "awaitDisconnect: gatt#${System.identityHashCode(gatt)}")
        // 1) Pedir disconnect
        try {
            gatt.disconnect()
        } catch (_: Exception) {
        }
        // 2) Esperar a que el stack marque DISCONNECTED
        withTimeoutOrNull(timeoutMs) {
            while (bluetoothManager?.getConnectionState(
                    gatt.device,
                    BluetoothProfile.GATT
                ) != BluetoothProfile.STATE_DISCONNECTED
            ) {
                delay(100)
            }
        }
        // 3) Close por si acaso y nullear
        try {
            gatt.close()
        } catch (_: Exception) {
        }
        if (bluetoothGatt === gatt) bluetoothGatt = null
        ready = false
        Log.d("BLE", "awaitDisconnect: cerrado y limpio")
        // 4) Margen para liberar el cliente en el stack (importante)
        delay(250)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override suspend fun disconnect() = mutex.withLock {
        awaitDisconnectAndCloseLocked(timeoutMs = 4000)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun startScan() {
        bluetoothScanner.scanDevices()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun stopScan() {
        bluetoothScanner.stopScan()
    }

    private fun verifyDataFrame(dataFrame: ByteArray): String? {
        val dataFrameToHex = dataFrame.toHexString()
        Log.d("BLE", "Current dataframe $dataFrameToHex")
        // Verificación de longitud
        if (dataFrameToHex.length == 28) {
            val calculatedDataFrame =
                dataFrame.dropLast(1).sumOf { data -> data.toUByte().toInt() } % 256

            Log.d("BLE", "CalculatedDataFrame $calculatedDataFrame")
            Log.d("BLE", "CheckSum ${dataFrame.last().toUByte().toInt()}")

            // Verificación de CheckSum
            if (calculatedDataFrame == dataFrame.last().toUByte().toInt()
                && verifyTemperature(dataFrameToHex)
            ) {
                Log.d("BLE", "Trama correcta")
                return dataFrameToHex
            } else Log.d("BLE", "Trama incorrecta")
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

    @SuppressLint("MissingPermission")
    private fun internalDisconnectLocked() {
        bluetoothGatt?.run {
            try {
                disconnect()
                close()
            } catch (_: Exception) {
            }
        }
        bluetoothGatt = null
        device = null
        ready = false
    }

    @SuppressLint("MissingPermission")
    private fun safeCloseGatt(gatt: BluetoothGatt) {
        try {
            gatt.disconnect()
            gatt.close()
        } catch (_: Exception) {
        }
        if (bluetoothGatt == gatt) bluetoothGatt = null
    }

    @SuppressLint("MissingPermission")
    fun isConnected(): Boolean {
        val dev = device ?: return false
        return bluetoothManager?.getConnectionState(
            dev,
            BluetoothProfile.GATT
        ) == BluetoothProfile.STATE_CONNECTED && ready
    }

    private inline fun safeGattCall(block: (BluetoothGatt) -> Unit): Boolean {
        val gatt = bluetoothGatt ?: return false
        return try {
            block(gatt)
            true
        } catch (_: android.os.DeadObjectException) {
            safeCloseGatt(gatt)
            false
        } catch (_: SecurityException) {
            false
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun readRssiSafely(): Boolean {
        if (!isConnected()) return false
        return withContext(Dispatchers.IO) {
            safeGattCall { it.readRemoteRssi() }
        }
    }

    @SuppressLint("MissingPermission")
    override fun startRssiPolling(intervalsMs: Long): Job = scope.launch {
        while (isActive) {
            if (isConnected()) {
                readRssiSafely()
            }
            if (bluetoothAdapter?.isEnabled == false) {
                currentMac?.let { connect(it) }
            }
            delay(intervalsMs)
        }
    }
}