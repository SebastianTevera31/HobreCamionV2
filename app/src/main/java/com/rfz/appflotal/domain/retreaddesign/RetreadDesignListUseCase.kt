package com.rfz.appflotal.domain.retreaddesign

import com.rfz.appflotal.data.model.message.response.MessageResponse
import com.rfz.appflotal.data.repository.retreaddesign.RetreadDesignListRepository
import javax.inject.Inject

class RetreadDesignListUseCase @Inject constructor(
    private val retreadDesignListRepository: RetreadDesignListRepository
) {
    suspend operator fun invoke(): Result<List<MessageResponse>> {
        return retreadDesignListRepository.doBrandCrud()
    }
}
