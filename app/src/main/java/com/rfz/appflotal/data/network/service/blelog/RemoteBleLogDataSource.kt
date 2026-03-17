package com.rfz.appflotal.data.network.service.blelog

import com.rfz.appflotal.data.network.client.blelog.BleLogClient
import com.rfz.appflotal.data.network.networkRequestHelper
import javax.inject.Inject

class RemoteBleLogDataSource @Inject constructor(
    private val bleLogClient: BleLogClient
) {

    suspend fun sendDataFrame(token: String, data: String) = networkRequestHelper {
        bleLogClient.postDataFrame()
    }

    suspend fun sendBleError(token: String, message: String) = networkRequestHelper {
        bleLogClient.postBleError()
    }

}