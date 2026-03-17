package com.rfz.appflotal.data.network.client.blelog

import retrofit2.Response
import retrofit2.http.POST


interface BleLogClient {

    @POST()
    suspend fun postDataFrame(): Response<Unit>

    @POST()
    suspend fun postBleError(): Response<Unit>
}