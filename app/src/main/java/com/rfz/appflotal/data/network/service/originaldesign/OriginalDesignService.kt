package com.rfz.appflotal.data.network.service.originaldesign

import com.rfz.appflotal.data.model.disassembly.response.DisassemblyCauseResponse
import com.rfz.appflotal.data.model.originaldesign.response.OriginalDesignResponse
import com.rfz.appflotal.data.network.client.disassembly.DisassemblyCauseClient
import com.rfz.appflotal.data.network.client.originaldesign.OriginalDesignClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class OriginalDesignService @Inject constructor(private val originalDesignClient: OriginalDesignClient) {
    suspend fun doOriginalDesign(tok:String): Response<List<OriginalDesignResponse>> {
        return withContext(Dispatchers.IO) {
            originalDesignClient.doOriginalDesign(tok)
        }
    }
}