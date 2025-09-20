package com.rfz.appflotal.domain.defaultparameter

import com.rfz.appflotal.data.model.defaultparameter.dto.CreateDefaultParameterRequest
import com.rfz.appflotal.data.model.defaultparameter.response.CreateDefaultParameterResponse
import com.rfz.appflotal.data.repository.defaultparameter.CreateDefaultParameterRepository
import javax.inject.Inject

class CreateDefaultParameterUseCase @Inject constructor(
    private val repository: CreateDefaultParameterRepository
) {
    suspend operator fun invoke(
        token: String,
        request: CreateDefaultParameterRequest
    ): Result<List<CreateDefaultParameterResponse>> {
        return repository.createDefaultParameter(token, request)
    }
}
