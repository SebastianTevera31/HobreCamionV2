package com.rfz.appflotal.data.model.airPressureRating.response

data class AirPressureRating(
    val id_airPressureRating: Int,
    val fld_description: String,
    val fld_minimumPercentage: Int,
    val fld_maximumPercentage: Int,
    val fld_performancePercentage: Int
)
