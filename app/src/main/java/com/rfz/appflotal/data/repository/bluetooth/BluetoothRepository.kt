package com.rfz.appflotal.data.repository.bluetooth

import android.Manifest
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

interface BluetoothRepository {
    var sensorData: StateFlow<BluetoothData>

    val scannedDevices: StateFlow<ScanItem?>
    fun connect(macAddress: String)
    fun disconnect()

    fun startScan()

    fun stopScan()
    suspend fun startRSSIMonitoring()
}

data class BluetoothData(
    val dataFrame: String? = null,
    val bluetoothSignalQuality: BluetoothSignalQuality = BluetoothSignalQuality.Desconocida,
    val rssi: Int? = null,
    val timestamp: String? = null
)

enum class BluetoothSignalQuality {
    Excelente, Aceptable, Pobre, Desconocida
}

class BluetoothRepositoryImp @Inject constructor(private val context: Context) :
    BluetoothRepository {

    private var bluetoothGatt: BluetoothGatt? = null

    private var _sensorData: MutableStateFlow<BluetoothData> = MutableStateFlow(BluetoothData())
    override var sensorData: StateFlow<BluetoothData> = _sensorData.asStateFlow()

    private val bluetoothManager: BluetoothManager? by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val bluetoothScanner by lazy {
        BluetoothScannerImp(bluetoothAdapter)
    }

    private var isConnected = false

    override val scannedDevices: StateFlow<ScanItem?> = bluetoothScanner.resultScanDevices

    private val gattCallback = object : BluetoothGattCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d("BLE", "Conectado. Descubriendo servicios...")
                    isConnected = true
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
                    isConnected = false
                    gatt.connect()
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

                val descriptor =
                    characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)
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
    override fun connect(macAddress: String) {
        val regex = Regex("^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$")
        if (regex.matches(macAddress)) {
            val device = bluetoothAdapter?.getRemoteDevice(macAddress)
            bluetoothGatt = device?.connectGatt(context, true, gattCallback)
        } else {
            Log.e("BluetoothRepository", "Bluetooth Address is not valid")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun startScan() {
        bluetoothScanner.scanDevices()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun stopScan() {
        bluetoothScanner.stopScan()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override suspend fun startRSSIMonitoring() {
        coroutineScope {
            launch {
                while (true) {
                    delay(5000)
                    bluetoothGatt?.readRemoteRssi()
                }
            }
        }
    }

    private fun verifyDataFrame(dataFrame: ByteArray): String? {
        val dataFrameToHex = dataFrame.toHexString()

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
}