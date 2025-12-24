package com.rfz.appflotal.domain.provider

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.provider.dto.ProviderDto
import com.rfz.appflotal.data.repository.provider.ProviderCrudRepository
import javax.inject.Inject

class ProviderCrudUseCase @Inject constructor(
    private val providerCrudRepository: ProviderCrudRepository
) {
    suspend operator fun invoke(requestBody: ProviderDto, token: String): Result<List<GeneralResponse>> {
        return providerCrudRepository.doBrandCrud(requestBody, token)
    }
}
