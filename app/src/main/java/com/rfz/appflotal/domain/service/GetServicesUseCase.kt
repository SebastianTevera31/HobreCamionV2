package com.rfz.appflotal.domain.service

import com.rfz.appflotal.data.model.services.toDomain
import com.rfz.appflotal.data.repository.services.ServicesRepository
import com.rfz.appflotal.presentation.ui.services.model.ServiceUi
import javax.inject.Inject

class GetServicesUseCase @Inject constructor(private val servicesRepository: ServicesRepository) {

    suspend operator fun invoke(): List<ServiceUi>? {
        return servicesRepository.getServices().getOrNull()?.map { it.toDomain() }
    }
}
