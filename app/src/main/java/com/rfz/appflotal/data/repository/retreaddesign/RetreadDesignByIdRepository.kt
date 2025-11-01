package com.rfz.appflotal.data.repository.retreaddesign

import com.rfz.appflotal.data.model.retreaddesing.response.RetreadDesignByIdResponse
import com.rfz.appflotal.data.network.service.retreaddesign.RetreadDesignByIdService
import javax.inject.Inject

class RetreadDesignByIdRepository @Inject constructor(
    private val retreadDesignByIdService: RetreadDesignByIdService
) {
    suspend fun onRetreatedDesignById(retreadDesignId: Int): Result<RetreadDesignByIdResponse> {
        return retreadDesignByIdService.onGetRetreadDesignById(retreadDesignId)
    }
}