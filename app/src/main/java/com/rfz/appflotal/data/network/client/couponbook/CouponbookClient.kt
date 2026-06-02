package com.rfz.appflotal.data.network.client.couponbook

import com.rfz.appflotal.data.model.couponbook.RedeemDto
import com.rfz.appflotal.data.model.couponbook.ValidateCouponDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CouponBookClient {
    @POST("api/vouchers/redeem")
    fun redeemVoucher(
        @Header("Authenticate") token: String,
        @Body redeemBody: RedeemDto
    ): Response<Unit>

    @POST("api/vouchers/validate")
    fun validateVoucher(
        @Header("Authenticate") token: String,
        @Body validateDto: ValidateCouponDto
    ): Response<Unit>
}