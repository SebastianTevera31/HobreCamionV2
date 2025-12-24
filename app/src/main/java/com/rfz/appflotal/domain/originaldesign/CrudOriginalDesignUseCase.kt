package com.rfz.appflotal.domain.originaldesign

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.originaldesign.dto.CrudOriginalDesignDto
import com.rfz.appflotal.data.repository.originaldesign.CrudOriginalDesignRepository
import javax.inject.Inject


class CrudOriginalDesignUseCase @Inject constructor(
    private val crudOriginalDesignRepository: CrudOriginalDesignRepository
) {
    suspend operator fun invoke(requestBody: CrudOriginalDesignDto, token: String): Result<List<GeneralResponse>> {
        return crudOriginalDesignRepository.doCrudOriginalDesign(requestBody, token)
    }
}
