package com.rfz.appflotal.domain.service

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.services.dto.ServiceDetailDto
import com.rfz.appflotal.data.repository.services.ServicesRepository
import javax.inject.Inject

class DoCrudServiceDetailUseCase @Inject constructor(private val servicesRepository: ServicesRepository) {
    suspend operator fun invoke(requestBody: ServiceDetailDto): GeneralResponse? {
        return servicesRepository.doCrudServiceDetail(requestBody).getOrNull()
    }
}
