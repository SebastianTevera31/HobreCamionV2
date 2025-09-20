package com.rfz.appflotal.domain.depthcolor

import com.rfz.appflotal.data.model.depthcolor.dto.UpdateDepthColorRequest
import com.rfz.appflotal.data.model.depthcolor.response.UpdateDepthColorResponse
import com.rfz.appflotal.data.repository.depthcolor.UpdateDepthColorRepository
import javax.inject.Inject

class UpdateDepthColorUseCase @Inject constructor(
    private val repository: UpdateDepthColorRepository
) {
    suspend operator fun invoke(
        token: String,
        request: UpdateDepthColorRequest
    ): Result<List<UpdateDepthColorResponse>> {
        return repository.updateDepthColor(token, request)
    }
}
