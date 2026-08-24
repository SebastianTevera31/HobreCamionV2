package com.rfz.appflotal.data.repository.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresPermission
import com.rfz.appflotal.data.repository.bluetooth.BluetoothScannerImp.Companion.SERVICE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

interface BluetoothScanner {
    fun scanDevices(serviceUUID: UUID? = SERVICE, lowLatency: Boolean = true)
    fun stopScan()
}

data class ScanItem(
    val name: String? = "",
    val address: String = "",
    val rssi: Int? = null
)

class BluetoothScannerImp(
    private val bluetoothAdapter: BluetoothAdapter?,
) :
    BluetoothScanner {
    private val bluetoothScanner = bluetoothAdapter?.bluetoothLeScanner

    private var _resultScanDevices = MutableStateFlow<ScanItem?>(null)
    private val seen = mutableSetOf<String>()

    private var scanning = false

    val resultScanDevices = _resultScanDevices.asStateFlow()

    private fun isBleReady(): Boolean = bluetoothAdapter?.isEnabled == true

    private val scanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)

            val addr = result.device.address

            if (seen.add(addr) && matchesTarget(result)) {
                Log.i("BluetoothScanner", "Valid device found: $addr")
                val item = ScanItem(
                    name = result.device.name,
                    address = result.device.address,
                    rssi = result.rssi
                )

                _resultScanDevices.update { item }
                stopScan()
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e("BluetoothScanner", "$errorCode")
        }
    }

    private fun matchesTarget(result: ScanResult): Boolean {
        val deviceName = result.device.name ?: ""
        Log.i("BluetoothScanner", "Device name: $deviceName")

        // 1. Caso específico para TPMS (Validación estricta)
        if (deviceName.startsWith("TPMS")) {
            val mac = result.device.address.replace(":", "").takeLast(6)
            val hasProduct = MONITOR_PRODUCTOS.any { deviceName.contains(it) }
            val hasMac = deviceName.contains(mac)

            if (hasProduct && hasMac) {
                Log.i("BluetoothScanner", "Target TPMS found: $deviceName")
                return true
            }
            // Si empieza con TPMS pero no cumple lo anterior, seguimos buscando por UUID
        }

        // 2. Otros prefijos que no requieren validación extra (ej. "TM")
        if (deviceName.startsWith("TM")) {
            return true
        }

        // 3. Verificación por registros de servicio (UUIDs)
        val sr = result.scanRecord ?: return false
        val hasService = sr.serviceUuids?.any { it == TARGET_BLE4 || it == TARGET_BLE5 } == true
        val hasServiceData = sr.getServiceData(TARGET_BLE4) != null
                || sr.getServiceData(TARGET_BLE5) != null

        return hasService || hasServiceData
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun scanDevices(serviceUUID: UUID?, lowLatency: Boolean) {
        if (scanning) return
        if (!isBleReady()) return
        val scanner = bluetoothScanner ?: return

        val settings = ScanSettings.Builder().setScanMode(
            if (lowLatency) ScanSettings.SCAN_MODE_LOW_LATENCY
            else ScanSettings.SCAN_MODE_BALANCED
        ).build()

        _resultScanDevices.value = null
        seen.clear()

        scanning = true

        scanner.startScan(null, settings, scanCallback)
    }

    @SuppressLint("MissingPermission")
    override fun stopScan() {
        if (!scanning) return

        bluetoothScanner?.stopScan(scanCallback)

        scanning = false

        Log.i("BluetoothScanner", "Scanner close")
    }

    companion object {
        val VALID_NAME_PREFIXES = listOf("TM", "TPMS")
        val MONITOR_PRODUCTOS = listOf("T6", "T22", "T38")
        val TARGET_BLE4: ParcelUuid = ParcelUuid.fromString("00001000-0000-1000-8000-00805f9b34fb")
        val TARGET_BLE5: ParcelUuid = ParcelUuid.fromString("0000A002-0000-1000-8000-00805F9B34FB")
        val SERVICE: UUID = UUID.fromString("f000ffd0-0451-4000-b000-000000000000")
        val required = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )

            else -> arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION // Compat <= 11
            )
        }
    }

}