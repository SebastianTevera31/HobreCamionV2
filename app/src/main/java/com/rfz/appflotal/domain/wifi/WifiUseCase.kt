package com.rfz.appflotal.domain.wifi

import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.data.repository.wifi.WifiRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class WifiUseCase @Inject constructor(private val wifiRepository: WifiRepository) {
    operator fun invoke(): StateFlow<NetworkStatus> {
        return wifiRepository.wifiConnectionState
    }

    fun doConnect() = wifiRepository.connect()
}