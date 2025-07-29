package com.rfz.appflotal.data.network.service.acquisitiontype

import com.rfz.appflotal.data.model.acquisitiontype.response.AcquisitionTypeResponse
import com.rfz.appflotal.data.network.client.acquisitiontype.AcquisitionTypeClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject



class AcquisitionTypeService @Inject constructor(private val acquisitionTypeClient: AcquisitionTypeClient) {
    suspend fun doAcquisitionType(tok:String): Response<List<AcquisitionTypeResponse>> {
        return withContext(Dispatchers.IO) {
            acquisitionTypeClient.doAcquisitionType(tok)
        }
    }
}