package com.rfz.appflotal.data.network.service.couponbook

import com.rfz.appflotal.data.model.couponbook.RedeemDto
import com.rfz.appflotal.data.model.couponbook.ValidateCouponDto
import com.rfz.appflotal.data.network.client.couponbook.CouponBookClient
import com.rfz.appflotal.data.network.networkRequestHelper
import javax.inject.Inject


class RemoteCouponDataSource @Inject constructor(private val couponBookClient: CouponBookClient) {

    suspend fun redeemVoucher(token: String, body: RedeemDto) = networkRequestHelper {
        couponBookClient.redeemVoucher(
            token = "Bearer $token",
            redeemBody = body
        )
    }

    suspend fun validateVoucher(token: String, body: ValidateCouponDto) = networkRequestHelper {
        couponBookClient.validateVoucher(
            token = "Bearer $token",
            validateDto = body
        )
    }

    suspend fun getVoucher(id: String, token: String) {
//        networkRequestHelper {
////        couponBookClient.getVoucher(
////            token = "Bearer $token"
////        )
//        }
    }
}