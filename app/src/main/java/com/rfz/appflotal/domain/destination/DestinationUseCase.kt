package com.rfz.appflotal.domain.destination

import com.rfz.appflotal.data.model.destination.Destination
import com.rfz.appflotal.data.model.destination.toDomain
import com.rfz.appflotal.data.repository.destination.DestinationRepository
import javax.inject.Inject

class DestinationUseCase @Inject constructor(
    private val destinationRepository: DestinationRepository
) {
    suspend operator fun invoke(): Result<List<Destination>> {
        return destinationRepository.doDestination()
    }
}
