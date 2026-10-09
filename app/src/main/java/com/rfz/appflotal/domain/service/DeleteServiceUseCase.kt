package com.rfz.appflotal.domain.service

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.repository.services.ServicesRepository
import javax.inject.Inject

class DeleteServiceUseCase @Inject constructor(private val servicesRepository: ServicesRepository) {
    suspend operator fun invoke(idService: Int): Result<List<GeneralResponse>> {
        return servicesRepository.deleteService(idService)
    }
}
