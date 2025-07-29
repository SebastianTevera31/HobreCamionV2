package com.rfz.appflotal.data.model.airPressureRating.dto

data class CreateAirPressureRatingDto(
    val fld_description: String,
    val fld_minimumPercentage: Int,
    val fld_maximumPercentage: Int,
    val fld_performancePercentage: Int
)
