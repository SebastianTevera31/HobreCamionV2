package com.rfz.appflotal.data.repository.wifi

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.rfz.appflotal.data.NetworkStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

interface WifiRepository {
    val wifiConnectionState: StateFlow<NetworkStatus>
    fun connect()
}

class WifiRepositoryImp @Inject constructor(private val context: Context) : WifiRepository {
    private var _wifiConnectionState: MutableStateFlow<NetworkStatus> =
        MutableStateFlow(NetworkStatus.Disconnected)

    override val wifiConnectionState: StateFlow<NetworkStatus> = _wifiConnectionState.asStateFlow()

    private val connectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val networkRequest =
        NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.d("WifiController", "Conectado a internet")
            _wifiConnectionState.update { NetworkStatus.Connected }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.d("WifiController", "Señal de internet perdida")
            _wifiConnectionState.update { NetworkStatus.Disconnected }
        }

        override fun onUnavailable() {
            super.onUnavailable()
            Log.d("WifiController", "Red Wifi no disponible")
            _wifiConnectionState.update { NetworkStatus.Disconnected }
        }

    }

    override fun connect() = connectivityManager.requestNetwork(networkRequest, networkCallback)
}