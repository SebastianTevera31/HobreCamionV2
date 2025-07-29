package com.rfz.appflotal.domain.acquisitiontype

import com.rfz.appflotal.data.model.acquisitiontype.response.AcquisitionTypeResponse
import com.rfz.appflotal.data.model.login.response.LoginResponse
import com.rfz.appflotal.data.repository.acquisitiontype.AcquisitionTypeRepository
import com.rfz.appflotal.data.repository.login.LoginRepository
import javax.inject.Inject


class AcquisitionTypeUseCase @Inject constructor(
    private val acquisitionTypeRepository: AcquisitionTypeRepository
) {
    suspend operator fun invoke(token: String): Result<AcquisitionTypeResponse> {
        return acquisitionTypeRepository.doAcquisitionType(token).mapCatching { list ->
            list.firstOrNull() ?: throw Throwable("Lista vacía de AcquisitionTypeResponse")
        }
    }
}
