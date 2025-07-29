package com.rfz.appflotal.domain.retreaddesign

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.model.retreaddesing.dto.RetreadDesignCrudDto
import com.rfz.appflotal.data.repository.retreaddesign.RetreadDesignCrudRepository
import javax.inject.Inject

class RetreadDesignCrudUseCase @Inject constructor(
    private val retreadDesignCrudRepository: RetreadDesignCrudRepository
) {
    suspend operator fun invoke(requestBody: RetreadDesignCrudDto, token: String): Result<List<MessageResponse>> {
        return retreadDesignCrudRepository.doBrandCrud(requestBody, token)
    }
}
