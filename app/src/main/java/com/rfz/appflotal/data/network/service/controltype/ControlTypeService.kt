package com.rfz.appflotal.data.network.service.controltype

import com.rfz.appflotal.data.model.brand.response.BranListResponse
import com.rfz.appflotal.data.model.controltype.response.ControlTypeResponse

import com.rfz.appflotal.data.network.client.controltype.ControlTypeClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class ControlTypeService @Inject constructor(private val controlTypeClient: ControlTypeClient ) {
    suspend fun doControlType(tok:String): Response<List<ControlTypeResponse>> {
        return withContext(Dispatchers.IO) {
            controlTypeClient.doControlType(tok)
        }
    }
}