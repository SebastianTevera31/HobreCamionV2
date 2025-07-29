package com.rfz.appflotal.data.network.service.diagram

import com.rfz.appflotal.data.model.destination.response.DestinationResponse
import com.rfz.appflotal.data.model.diagram.response.DiagramResponse
import com.rfz.appflotal.data.network.client.destination.DestinationClient
import com.rfz.appflotal.data.network.client.diagram.DiagramClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject



class DiagramService @Inject constructor(private val diagramClient: DiagramClient) {
    suspend fun doDiagram(tok:String): Response<List<DiagramResponse>> {
        return withContext(Dispatchers.IO) {
            diagramClient.doDiagram(tok)
        }
    }
}