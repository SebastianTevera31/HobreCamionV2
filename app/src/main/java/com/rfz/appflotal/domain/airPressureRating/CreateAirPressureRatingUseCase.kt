package com.rfz.appflotal.domain.airPressureRating

import com.rfz.appflotal.data.model.airPressureRating.dto.CreateAirPressureRatingDto
import com.rfz.appflotal.data.model.airPressureRating.response.CreateAirPressureRatingResponse
import com.rfz.appflotal.data.repository.airPressureRating.CreateAirPressureRatingRepository
import javax.inject.Inject


class CreateAirPressureRatingUseCase @Inject constructor(
    private val createAirPressureRatingRepository: CreateAirPressureRatingRepository
) {
    suspend operator fun invoke(requestBody: CreateAirPressureRatingDto, token: String): Result<List<CreateAirPressureRatingResponse>> {
        return createAirPressureRatingRepository.docreateAirPressureRating(requestBody, token)
    }
}
