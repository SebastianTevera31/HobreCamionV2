package com.rfz.appflotal.domain.destination

import com.rfz.appflotal.data.model.destination.response.DestinationResponse
import com.rfz.appflotal.data.repository.destination.DestinationRepository
import javax.inject.Inject

class DestinationUseCase @Inject constructor(
    private val destinationRepository: DestinationRepository
) {
    suspend operator fun invoke(token: String): Result<List<DestinationResponse>> {
        return destinationRepository.doDestination(token)
    }
}
