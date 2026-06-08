package com.rfz.appflotal.data.repository.couponbook

import com.rfz.appflotal.data.model.couponbook.*
import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.network.service.couponbook.RemoteCouponDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CouponBookRepository @Inject constructor(
    private val remoteCouponBookDataSource: RemoteCouponDataSource,
    private val getTasksUseCase: GetTasksUseCase
) {

    suspend fun validateVoucher(code: String): Result<ValidatedVoucher> {
        val token = getTasksUseCase().first().first().fld_token
        return remoteCouponBookDataSource.validateVoucher(
            token = token,
            body = ValidateCouponDto(
                code = code
            )
        ).map { response -> response.toDomain() }
    }

    suspend fun redeemVoucher(redeemDto: RedeemDto): Result<Unit> {
        val token = getTasksUseCase().first().first().fld_token
        return remoteCouponBookDataSource.redeemVoucher(
            token = token,
            body = redeemDto
        )
    }

    suspend fun getVouchers(): Result<List<Coupon>> {
        val token = getTasksUseCase().first().first().fld_token
        return remoteCouponBookDataSource.getVouchers(
            token = token,
        ).map { list -> list.map { it.toDomain() } }
    }

    suspend fun acquireVoucher(code: String): Result<GeneralResponse> {
        val token = getTasksUseCase().first().first().fld_token
        return remoteCouponBookDataSource.acquireVoucher(
            token = token,
            id = code
        )
    }
}