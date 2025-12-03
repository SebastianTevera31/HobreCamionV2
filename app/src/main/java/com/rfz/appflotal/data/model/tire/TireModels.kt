package com.rfz.appflotal.data.model.tire

import com.google.gson.annotations.SerializedName
import com.rfz.appflotal.data.model.CatalogItem
import com.rfz.appflotal.data.model.tire.response.TireListResponse
import java.time.OffsetDateTime

data class Tire(
    override val id: Int,
    override val description: String,
    val size: String,
    val brand: String,
    val model: String,
    val thread: Double,
    val loadingCapacity: String,
    val destination: String
) : CatalogItem

data class RepairedTireDto(
    @SerializedName("id_repairedTire")
    val idRepairedTire: Int,

    @SerializedName("p_tire_fk_1")
    val tireId: Int,

    @SerializedName("fld_cost")
    val cost: Double,

    @SerializedName("c_repair_fk_1")
    val repairId: Int,

    @SerializedName("fld_dateOperation")
    val dateOperation: String
)

data class RetreatedTireDto(
    @SerializedName("id_retreadedTire")
    val idRetreadedTire: Int,

    @SerializedName("p_tire_fk_1")
    val tireId: Int,

    @SerializedName("fld_cost")
    val cost: Double,

    @SerializedName("fld_dateOperation")
    val dateOperation: String,

    @SerializedName("c_retreadDesign_fk_2")
    val retreadDesignId: Int
)

data class ChangeDestinationDto(
    @SerializedName("id_tire")
    val tireId: Int,

    @SerializedName("id_destination")
    val destinationId: Int,

    @SerializedName("changeMotive")
    val changeMotive: String,

    @SerializedName("fld_dateOperation")
    val dateOperation: String,
)

data class RepairedTire(
    val id: Int,
    val tireId: Int,
    val cost: Double,
    val repairId: Int,
    val dateOperation: OffsetDateTime
)

data class RetreatedTire(
    val id: Int,
    val tireId: Int,
    val cost: Double,
    val dateOperation: OffsetDateTime,
    val retreadDesignId: Int
)

data class ChangeDestination(
    val tireId: Int,
    val destinationId: Int,
    val changeMotive: String,
    val dateOperation: OffsetDateTime,
)