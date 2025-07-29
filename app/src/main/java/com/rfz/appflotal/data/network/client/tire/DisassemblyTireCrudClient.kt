package com.rfz.appflotal.data.network.client.tire

import com.rfz.appflotal.data.model.message.response.MessageResponse

import com.rfz.appflotal.data.model.tire.dto.DisassemblyTireDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DisassemblyTireCrudClient {

    @POST("api/Disassembly/DisassemblyTire")
    suspend fun doDisassemblyTire(@Body requestBody: DisassemblyTireDto, @Header("Authorization") token: String): Response<List<MessageResponse>>
}