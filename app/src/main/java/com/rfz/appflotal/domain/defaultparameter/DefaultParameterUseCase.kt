package com.rfz.appflotal.domain.defaultparameter

import com.rfz.appflotal.data.model.defaultparameter.response.DefaultParameterResponse
import com.rfz.appflotal.data.repository.defaultparameter.DefaultParameterRepository
import javax.inject.Inject

class DefaultParameterUseCase @Inject constructor(
    private val defaultParameterRepository: DefaultParameterRepository
) {
    suspend operator fun invoke(token: String, userId: Int): Result<List<DefaultParameterResponse>> {
        return defaultParameterRepository.doDefaultParameter(token, userId)
    }
}
