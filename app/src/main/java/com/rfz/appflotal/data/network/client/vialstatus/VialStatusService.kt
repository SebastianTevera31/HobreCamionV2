package com.rfz.appflotal.data.network.client.vialstatus

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VialStatusService {

    @GET("api/RoadConditions/GetRoadMapByState")
    suspend fun getRoadMap(@Query("id_state") state: Int): Response<List<RoadMapDto>>
}