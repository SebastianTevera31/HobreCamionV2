package com.rfz.appflotal.data.network.service.retreaddesign

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.retreaddesing.dto.RetreadDesignCrudDto
import com.rfz.appflotal.data.network.client.retreaddesign.RetreadDesignCrudClient
import com.rfz.appflotal.data.network.client.retreaddesign.RetreadDesignListClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class RetreadDesignListService @Inject constructor(private val retreadDesignListClient: RetreadDesignListClient) {
    suspend fun doCrudRetreadDesignList(tok:String): Response<List<MessageResponse>> {
        return withContext(Dispatchers.IO) {
            retreadDesignListClient.doRetreadDesignList(tok)
        }
    }
}