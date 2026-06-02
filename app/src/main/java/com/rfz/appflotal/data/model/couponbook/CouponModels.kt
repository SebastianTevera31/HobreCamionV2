package com.rfz.appflotal.data.model.couponbook

import com.google.gson.annotations.SerializedName

data class RedeemDto(
    @SerializedName("fld_code") val code: String,
    @SerializedName("id_user_customer") val idUserCustomer: Int,
    @SerializedName("id_user_cashier") val idUserCashier: Int,
    @SerializedName("id_business") val idBusiness: Int,
    @SerializedName("fld_original_amount") val originalAmount: Int,
)

data class ValidateCouponDto(
    @SerializedName("fld_code") val code: String,
    @SerializedName("id_business") val idBusiness: Int,
)

data class Redeem(
    val code: String,
    val idUserCustomer: Int,
    val idUserCashier: Int,
    val idBusiness: Int,
    val originalAmount: Int
)

data class Validate(
    val code: String,
    val idBusiness: Int
)