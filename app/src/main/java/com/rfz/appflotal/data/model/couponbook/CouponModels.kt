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

data class GetVoucherByUserResponse(
    @SerializedName("fld_code") val fldCode: String,
    @SerializedName("fld_title") val fldTitle: String,
    @SerializedName("fld_description") val fldDescription: String,
    @SerializedName("fld_discount_type") val fldDiscountType: Int,
    @SerializedName("fld_discount_value") val fldDiscountValue: String,
    @SerializedName("fld_start_date") val fldStartDate: String,
    @SerializedName("fld_end_date") val fldEndDate: String,
    @SerializedName("fld_status") val fldStatus: Int
)

data class Coupons(
    val fldCode: String,
    val fldTitle: String,
    val fldDescription: String,
    val fldDiscountType: Int,
    val fldDiscountValue: String,
    val fldStartDate: String,
    val fldEndDate: String,
    val fldStatus: Int
)

enum class VoucherStatusType(val id: Int) {
    RECLAMADO(1), INACTIVO(2), NO_VALIDO(3), EXPIRADO(4), VALIDO(5)
}