package com.rfz.appflotal.domain.defaultparameter

import com.rfz.appflotal.data.model.defaultparameter.dto.UpdateDefaultParameterRequest
import com.rfz.appflotal.data.model.defaultparameter.response.UpdateDefaultParameterResponse
import com.rfz.appflotal.data.repository.defaultparameter.UpdateDefaultParameterRepository
import javax.inject.Inject

class UpdateDefaultParameterUseCase @Inject constructor(
    private val repository: UpdateDefaultParameterRepository
) {
    suspend operator fun invoke(
        token: String,
        request: UpdateDefaultParameterRequest
    ): Result<List<UpdateDefaultParameterResponse>> {
        return repository.updateDefaultParameter(token, request)
    }
}
