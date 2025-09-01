package com.rfz.appflotal.data.repository.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.rfz.appflotal.data.repository.bluetooth.BluetoothScannerImp.Companion.SERVICE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

private var scanning = false

interface BluetoothScanner {
    fun scanDevices(serviceUUID: UUID? = SERVICE, lowLatency: Boolean = true)
    fun stopScan()
}

data class ScanItem(
    val name: String? = "",
    val address: String = "",
    val rssi: Int? = null
)

class BluetoothScannerImp(private val bluetoothAdapter: BluetoothAdapter?) :
    BluetoothScanner {
    private val bluetoothScanner = bluetoothAdapter?.bluetoothLeScanner
    private var _resultScanDevices = MutableStateFlow<ScanItem?>(null)
    val resultScanDevices = _resultScanDevices.asStateFlow()

    private fun isBleReady(): Boolean = bluetoothAdapter?.isEnabled == true

    private val seen = mutableSetOf<String>()
    private val scanCallback: ScanCallback = object : ScanCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)

            val addr = result.device.address

            Log.i("BluetoothScanner", "Address detected: $addr")

            if (seen.add(addr) && matchesTarget(result)) {
                scanning = false

                val item = ScanItem(
                    name = result.device.name,
                    address = result.device.address,
                    rssi = result.rssi
                )

                _resultScanDevices.update { item }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e("BluetoothScanner", "$errorCode")
        }
    }

    private fun matchesTarget(result: ScanResult): Boolean {
        val sr = result.scanRecord ?: return false
        val target = ParcelUuid.fromString("00001000-0000-1000-8000-00805f9b34fb")

        val hasService = sr.serviceUuids?.any { it == target } == true
        val hasServiceData = sr.getServiceData(target) != null

        // Ejemplo opcional con manufacturer data:
        val mfgId = 0xFFFF // cambia por el tuyo
        val hasMfg = sr.manufacturerSpecificData.get(mfgId) != null

        return hasService || hasServiceData || hasMfg
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun scanDevices(serviceUUID: UUID?, lowLatency: Boolean) {
        if (scanning) return
        if (!isBleReady()) return

        val scanner = bluetoothScanner ?: return
        val filters = mutableListOf<ScanFilter>()

        serviceUUID?.let {
            filters += ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(UUID.fromString("00001000-0000-1000-8000-00805f9b34fb")))
                .setServiceUuid(ParcelUuid(SERVICE))
                .build()
        }

        val settings = ScanSettings.Builder().setScanMode(
            if (lowLatency) ScanSettings.SCAN_MODE_LOW_LATENCY
            else ScanSettings.SCAN_MODE_BALANCED
        ).build()

        _resultScanDevices.value = null
        seen.clear()

        scanner.startScan(null, settings, scanCallback)
        scanning = true
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun stopScan() {
        if (!scanning) return
        bluetoothScanner?.stopScan(scanCallback)
        scanning = false
        Log.i("BluetoothScanner", "Scanner close")
    }

    companion object {
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