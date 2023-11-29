package com.rfz.proyectogasmonsoft.data.model.sensor

import com.google.gson.annotations.SerializedName

data class SensorResponse(
    @SerializedName("id") var id: Int,
    @SerializedName("message") var message: String
)
