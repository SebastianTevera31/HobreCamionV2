package com.rfz.appflotal.data.network.client.couponbook

import com.rfz.appflotal.data.model.couponbook.GetCouponsResponse
import com.rfz.appflotal.data.model.couponbook.GetVoucherByUserResponse
import com.rfz.appflotal.data.model.couponbook.RedeemDto
import com.rfz.appflotal.data.model.couponbook.ValidateCouponDto
import com.rfz.appflotal.data.model.couponbook.ValidateVoucherResponseDto
import com.rfz.appflotal.data.model.message.response.GeneralResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface CouponBookService {
    @POST("api/vouchers/redeem")
    suspend fun redeemVoucher(
        @Header("Authorization") token: String,
        @Body redeemBody: RedeemDto
    ): Response<Unit>

    @POST("api/vouchers/validate")
    suspend fun validateVoucher(
        @Header("Authorization") token: String,
        @Body validateDto: ValidateCouponDto
    ): Response<ValidateVoucherResponseDto>

    @GET("api/vouchers/UserVouchers")
    suspend fun getVouchersByUser(
        @Header("Authorization") token: String
    ): Response<List<GetVoucherByUserResponse>>

    @PUT("api/vouchers/AdquireVoucher")
    suspend fun acquireVoucher(
        @Header("Authorization") token: String,
        @Query("idCoupon") voucherId: Int
    ): Response<GeneralResponse>

    @GET("api/coupons/GetCoupons")
    suspend fun getCoupons(
        @Header("Authorization") token: String
    ): Response<List<GetCouponsResponse>>
}