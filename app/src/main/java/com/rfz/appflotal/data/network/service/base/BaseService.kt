package com.rfz.appflotal.data.network.service.base

import com.rfz.appflotal.data.model.base.BaseResponse
import com.rfz.appflotal.data.model.controltype.response.ControlTypeResponse
import com.rfz.appflotal.data.network.client.base.BaseClient
import com.rfz.appflotal.data.network.client.controltype.ControlTypeClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class BaseService @Inject constructor(private val baseClient: BaseClient) {
    suspend fun doBase(tok:String): Response<List<BaseResponse>> {
        return withContext(Dispatchers.IO) {
            baseClient.doBase(tok)
        }
    }
}