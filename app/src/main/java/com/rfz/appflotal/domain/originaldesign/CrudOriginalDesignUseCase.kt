package com.rfz.appflotal.domain.originaldesign

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.originaldesign.dto.CrudOriginalDesignDto
import com.rfz.appflotal.data.repository.originaldesign.CrudOriginalDesignRepository
import javax.inject.Inject


class CrudOriginalDesignUseCase @Inject constructor(
    private val crudOriginalDesignRepository: CrudOriginalDesignRepository
) {
    suspend operator fun invoke(requestBody: CrudOriginalDesignDto, token: String): Result<List<MessageResponse>> {
        return crudOriginalDesignRepository.doCrudOriginalDesign(requestBody, token)
    }
}
