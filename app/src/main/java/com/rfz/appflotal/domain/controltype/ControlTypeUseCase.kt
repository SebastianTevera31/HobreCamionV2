package com.rfz.appflotal.domain.controltype

import com.rfz.appflotal.data.model.controltype.response.ControlTypeResponse
import com.rfz.appflotal.data.repository.controltype.ControlTypeRepository
import javax.inject.Inject

class ControlTypeUseCase @Inject constructor(
    private val controlTypeRepository: ControlTypeRepository
) {
    suspend operator fun invoke(token: String): Result<List<ControlTypeResponse>> {
        return controlTypeRepository.doControlType(token)
    }
}
