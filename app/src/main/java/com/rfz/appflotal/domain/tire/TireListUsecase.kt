package com.rfz.appflotal.domain.tire

import com.rfz.appflotal.data.model.tire.response.TireListResponse
import com.rfz.appflotal.data.repository.tire.TireListRepository
import javax.inject.Inject



class TireListUsecase @Inject constructor(
    private val tireListRepository: TireListRepository
) {
    suspend operator fun invoke( token: String): Result<List<TireListResponse>> {
        return tireListRepository.doTireList(token)
    }
}
