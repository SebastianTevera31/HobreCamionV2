package com.rfz.appflotal.domain.airPressureRating

import com.rfz.appflotal.data.model.airPressureRating.dto.CreateAirPressureRatingDto
import com.rfz.appflotal.data.model.airPressureRating.response.CreateAirPressureRatingResponse
import com.rfz.appflotal.data.repository.airPressureRating.CreateAirPressureRatingRepository
import com.rfz.appflotal.data.repository.airPressureRating.UpdateAirPressureRatingRepository
import javax.inject.Inject


class UpdateAirPressureRatingUseCase @Inject constructor(
    private val updateAirPressureRatingRepository: UpdateAirPressureRatingRepository
) {
    suspend operator fun invoke(requestBody: CreateAirPressureRatingDto, token: String): Result<List<CreateAirPressureRatingResponse>> {
        return updateAirPressureRatingRepository.docupdateAirPressureRating(requestBody, token)
    }
}
