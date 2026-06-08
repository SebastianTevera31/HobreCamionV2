package com.rfz.appflotal.data.network.service.couponbook

import com.rfz.appflotal.data.model.couponbook.GetVoucherByUserResponse
import com.rfz.appflotal.data.model.couponbook.RedeemDto
import com.rfz.appflotal.data.model.couponbook.ValidateCouponDto
import com.rfz.appflotal.data.model.couponbook.ValidateVoucherResponseDto
import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.network.client.couponbook.CouponBookService
import com.rfz.appflotal.data.network.networkRequestHelper
import javax.inject.Inject


class RemoteCouponDataSource @Inject constructor(private val couponBookClient: CouponBookService) {

    suspend fun redeemVoucher(token: String, body: RedeemDto): Result<Unit> = networkRequestHelper {
        couponBookClient.redeemVoucher(
            token = "Bearer $token",
            redeemBody = body
        )
    }

    suspend fun validateVoucher(token: String, body: ValidateCouponDto): Result<ValidateVoucherResponseDto> = networkRequestHelper {
        couponBookClient.validateVoucher(
            token = "Bearer $token",
            validateDto = body
        )
    }

    suspend fun getVouchers(token: String): Result<List<GetVoucherByUserResponse>> =
        networkRequestHelper {
            couponBookClient.getVouchersByUser("Bearer $token")
        }

    suspend fun acquireVoucher(id: String, token: String): Result<GeneralResponse> = networkRequestHelper {
        couponBookClient.acquireVoucher("Bearer $token", id.toInt())
    }
}