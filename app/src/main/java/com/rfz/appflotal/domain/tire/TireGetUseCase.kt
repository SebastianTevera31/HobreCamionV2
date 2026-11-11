package com.rfz.appflotal.domain.tire

import com.rfz.appflotal.data.model.tire.response.TireIdResponse
import com.rfz.appflotal.data.repository.tire.TireGetRepository
import javax.inject.Inject

class TireGetUseCase @Inject constructor(
    private val tireGetRepository: TireGetRepository
) {
    suspend operator fun invoke(idTire: Int): Result<List<TireIdResponse>> {
        return tireGetRepository.doTireGet(idTire)
    }
}
