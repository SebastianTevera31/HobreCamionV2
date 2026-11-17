package com.rfz.appflotal.domain.service

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.services.dto.ServiceDetailDto
import com.rfz.appflotal.data.repository.services.ServicesRepository
import javax.inject.Inject

class DoCrudServiceDetailUseCase @Inject constructor(private val servicesRepository: ServicesRepository) {
    /**
     * Devuelve la respuesta del backend solo si confirmó el guardado; null si hubo
     * error de red/HTTP o si respondió con un id distinto de 200.
     *
     * El endpoint responde una lista: [{"id": 200, "message": "..."}].
     */
    suspend operator fun invoke(requestBody: ServiceDetailDto): GeneralResponse? {
        return servicesRepository.doCrudServiceDetail(requestBody).getOrNull()
            ?.firstOrNull()
            ?.takeIf { it.id == 200 }
    }
}
