package com.rfz.appflotal.domain.depthcolor

import com.rfz.appflotal.data.model.depthcolor.dto.CreateDepthColorRequest
import com.rfz.appflotal.data.model.depthcolor.response.CreateDepthColorResponse
import com.rfz.appflotal.data.repository.depthcolor.CreateDepthColorRepository
import javax.inject.Inject

class CreateDepthColorUseCase @Inject constructor(
    private val repository: CreateDepthColorRepository
) {
    suspend operator fun invoke(
        token: String,
        request: CreateDepthColorRequest
    ): Result<List<CreateDepthColorResponse>> {
        return repository.createDepthColor(token, request)
    }
}
