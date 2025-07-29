package com.rfz.appflotal.domain.airPressureRating

import com.rfz.appflotal.data.model.acquisitiontype.response.AcquisitionTypeResponse
import com.rfz.appflotal.data.model.airPressureRating.response.AirPressureRating
import com.rfz.appflotal.data.repository.acquisitiontype.AcquisitionTypeRepository
import com.rfz.appflotal.data.repository.airPressureRating.AirPressureRatingRepository
import javax.inject.Inject


class AirPressureRatingUseCase @Inject constructor(
    private val airPressureRatingRepository: AirPressureRatingRepository
) {
    suspend operator fun invoke(token: String): Result<AirPressureRating> {
        return airPressureRatingRepository.doAirPressureRating(token).mapCatching { list ->
            list.firstOrNull() ?: throw Throwable("Lista vacía de AcquisitionTypeResponse")
        }
    }
}
