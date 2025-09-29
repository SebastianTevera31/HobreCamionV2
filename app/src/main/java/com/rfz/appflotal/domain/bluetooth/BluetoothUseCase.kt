package com.rfz.appflotal.domain.bluetooth

import android.content.BroadcastReceiver
import com.rfz.appflotal.data.repository.bluetooth.BluetoothData
import com.rfz.appflotal.data.repository.bluetooth.BluetoothRepository
import com.rfz.appflotal.data.repository.bluetooth.ScanItem
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class BluetoothUseCase @Inject constructor(private val bluetoothRepository: BluetoothRepository) {
    operator fun invoke(): StateFlow<BluetoothData> {
        return bluetoothRepository.sensorData
    }

    fun getBtReceiver(): BroadcastReceiver = bluetoothRepository.btReceiver

    fun scannedDevices(): StateFlow<ScanItem?> {
        return bluetoothRepository.scannedDevices
    }

    suspend fun doConnect(mac: String) {
        bluetoothRepository.connect(mac)
    }

    fun startScan() = bluetoothRepository.startScan()

    fun stopScan() = bluetoothRepository.stopScan()
}