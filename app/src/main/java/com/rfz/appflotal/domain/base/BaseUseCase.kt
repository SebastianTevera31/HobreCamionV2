package com.rfz.appflotal.domain.base

import com.rfz.appflotal.data.model.base.BaseResponse
import com.rfz.appflotal.data.repository.base.BaseRepository
import javax.inject.Inject


class BaseUseCase @Inject constructor(
    private val baseRepository: BaseRepository
) {
    suspend operator fun invoke(token: String): Result<List<BaseResponse>> {
        return baseRepository.doBase(token)
    }
}
