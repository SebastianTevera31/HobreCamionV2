package com.rfz.appflotal.domain.depthcolor

import com.rfz.appflotal.data.model.depthcolor.response.DepthColorResponse
import com.rfz.appflotal.data.repository.depthcolor.DepthColorRepository
import javax.inject.Inject

class DepthColorUseCase @Inject constructor(
    private val repository: DepthColorRepository
) {
    suspend operator fun invoke(token: String): Result<List<DepthColorResponse>> {
        return repository.getDepthColors(token)
    }
}
