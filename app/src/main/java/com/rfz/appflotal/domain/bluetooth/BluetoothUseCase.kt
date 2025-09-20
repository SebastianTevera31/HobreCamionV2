package com.rfz.appflotal.domain.bluetooth

import com.rfz.appflotal.data.repository.bluetooth.BluetoothData
import com.rfz.appflotal.data.repository.bluetooth.BluetoothRepository
import com.rfz.appflotal.data.repository.bluetooth.ScanItem
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class BluetoothUseCase @Inject constructor(private val bluetoothRepository: BluetoothRepository) {
    operator fun invoke(): StateFlow<BluetoothData> {
        return bluetoothRepository.sensorData
    }

    fun scannedDevices(): StateFlow<ScanItem?> {
        return bluetoothRepository.scannedDevices
    }

    fun doConnect(mac: String = "80:F5:B5:70:5C:8F") = bluetoothRepository.connect(mac)

    suspend fun doStartRssiMonitoring() = bluetoothRepository.startRSSIMonitoring()

    fun startScan() = bluetoothRepository.startScan()

    fun stopScan() = bluetoothRepository.stopScan()
}