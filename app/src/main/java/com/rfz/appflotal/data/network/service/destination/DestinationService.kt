package com.rfz.appflotal.data.network.service.destination

import com.rfz.appflotal.data.model.destination.response.DestinationResponse

import com.rfz.appflotal.data.network.client.destination.DestinationClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject



class DestinationService @Inject constructor(private val destinationClient: DestinationClient) {
    suspend fun doDestination(tok:String): Response<List<DestinationResponse>> {
        return withContext(Dispatchers.IO) {
            destinationClient.doDestination(tok)
        }
    }
}