package com.rfz.appflotal.data.network.service.tire

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.provider.response.ProviderListResponse
import com.rfz.appflotal.data.model.tire.dto.TireCrudDto
import com.rfz.appflotal.data.model.tire.response.TireInspectionReportResponse
import com.rfz.appflotal.data.model.tire.response.TirexIdResponse
import com.rfz.appflotal.data.network.client.tire.TireCrudClient
import com.rfz.appflotal.data.network.client.tire.TireGetClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class TireGetService @Inject constructor(private val tireGetClient: TireGetClient) {
    suspend fun doTireGet(id_tire: Int, tok:String): Response<List<TirexIdResponse>> {
        return withContext(Dispatchers.IO) {
            tireGetClient.doTireGet(tok,id_tire)
        }
    }
}