package com.rfz.appflotal.domain.provider

import com.rfz.appflotal.data.model.provider.response.ProviderListResponse
import com.rfz.appflotal.data.repository.provider.ProviderListRepository
import javax.inject.Inject

class ProviderListUseCase @Inject constructor(
    private val providerListRepository: ProviderListRepository
) {
    suspend operator fun invoke(token: String, typeProviderId: Int): Result<List<ProviderListResponse>> {
        return providerListRepository.doProviderList(token, typeProviderId)
    }
}
