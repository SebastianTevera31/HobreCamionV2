package com.rfz.appflotal.data

sealed interface NetworkStatus {
    data object Connected : NetworkStatus
    data object Disconnected : NetworkStatus
}