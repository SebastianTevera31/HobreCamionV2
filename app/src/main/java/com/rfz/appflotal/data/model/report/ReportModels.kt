package com.rfz.appflotal.data.model.report

import com.google.gson.annotations.SerializedName

data class CpkReportRequest(
    @SerializedName("id_user") val idUser: Int,
    @SerializedName("id_tire") val idTire: Int,
)

data class CpkReportResponse(
    @SerializedName("id_tire") val idTire: Int,
    @SerializedName("fld_diferenceOdometer") val differenceOdometer: Int,
    @SerializedName("fld_differenceInTreadDepth") val differenceInTreadDepth: Int,
    @SerializedName("fld_kmPermm") val kmPerMm: Double,
    @SerializedName("fld_lifeCicle") val lifeCycle: Int,
    @SerializedName("fld_unitCost") val unitCost: Double,
    @SerializedName("fld_costPerkm") val costPerKm: Double,
    @SerializedName("fld_renovatedDesign") val renovatedDesign: String,
    @SerializedName("fld_costbymm") val costByMm: Double,
    @SerializedName("fld_tireNumber") val tireNumber: String
)

data class FuelConsumptionReportResponse(
    @SerializedName("mes") val month: String,
    @SerializedName("odometroMensual") val monthlyOdometer: String,
    @SerializedName("combustibleMensual") val monthlyFuel: String,
    @SerializedName("numCargas") val loadCount: Int,
    @SerializedName("fuelTypeName") val fuelTypeName: String,
    @SerializedName("rendimientoMensual") val monthlyPerformance: String
)

data class CO2EmissionsReportResponse(
    @SerializedName("mes") val month: String,
    @SerializedName("odometroMensual") val monthlyOdometer: String,
    @SerializedName("emisionMensualCO2") val monthlyCO2Emissions: String,
    @SerializedName("fuelTypeName") val fuelTypeName: String
)


