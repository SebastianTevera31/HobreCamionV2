package com.rfz.appflotal.data.network.service.defaultparameter

import com.rfz.appflotal.data.model.defaultparameter.response.DefaultParameterResponse
import com.rfz.appflotal.data.network.client.defaultparameter.DefaultParameterClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject



class DefaultParameterService @Inject constructor(private val defaultParameterClient: DefaultParameterClient) {
    suspend fun doDefaultParameter(tok:String, id_user:Int): Response<List<DefaultParameterResponse>> {
        return withContext(Dispatchers.IO) {
            defaultParameterClient.doDefaultParameter(tok,id_user)
        }
    }
}