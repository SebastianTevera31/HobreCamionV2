package com.rfz.appflotal.data.repository.couponbook

import com.rfz.appflotal.data.network.service.couponbook.RemoteCouponDataSource
import javax.inject.Inject

class CouponBookRepository @Inject constructor(
    private val remoteCouponBookDataSource: RemoteCouponDataSource
) {

}