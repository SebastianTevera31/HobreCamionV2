package com.rfz.appflotal.domain.service

import com.rfz.appflotal.data.model.services.toDomain
import com.rfz.appflotal.data.repository.services.ServicesRepository
import com.rfz.appflotal.presentation.ui.services.model.CatalogItemUi
import javax.inject.Inject

class GetTypeServiceUseCase @Inject constructor(private val servicesRepository: ServicesRepository) {
    suspend operator fun invoke(): List<CatalogItemUi>? {
        return servicesRepository.doGetServiceType().getOrNull()?.map { it.toDomain() }
    }
}
