package com.rfz.appflotal.presentation.ui.couponbook.navigation

import kotlinx.serialization.Serializable

@Serializable
object CouponGraph

@Serializable
object CouponMenu

@Serializable
object CouponInfo

@Serializable
object CouponList


@Serializable
data class RedeemCoupon(val couponId: String)