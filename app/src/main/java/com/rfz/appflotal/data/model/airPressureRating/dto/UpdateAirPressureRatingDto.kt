package com.rfz.appflotal.data.model.airPressureRating.dto

data class UpdateAirPressureRatingDto(
    val id_airPressureRating: Int,
    val fld_minimumPercentage: Int,
    val fld_maximumPercentage: Int,
    val fld_performancePercentage: Int
)
