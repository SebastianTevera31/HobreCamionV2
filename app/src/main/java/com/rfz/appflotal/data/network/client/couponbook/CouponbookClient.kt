package com.rfz.appflotal.data.network.client.couponbook

import com.rfz.appflotal.data.model.couponbook.GetVoucherByUserResponse
import com.rfz.appflotal.data.model.couponbook.RedeemDto
import com.rfz.appflotal.data.model.couponbook.ValidateCouponDto
import com.rfz.appflotal.data.model.message.response.GeneralResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

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

    @GET("api/vouchers/UserVouchersParameters")
    fun getVouchersByUser(
        @Header("Authenticate") token: String
    ): Response<List<GetVoucherByUserResponse>>

    @PUT("api/vouchers/AdquireVoucher")
    fun acquireVoucher(
        @Header("Authenticate") token: String,
        @Query("idCoupon") voucherId: Int
    ): Response<GeneralResponse>
}