package com.rfz.appflotal.data.model.couponbook

fun GetVoucherByUserResponse.toDomain() =
    Coupons(
        fldCode = this.fldCode,
        fldTitle = this.fldTitle,
        fldDescription = this.fldDescription,
        fldDiscountType = this.fldDiscountType,
        fldDiscountValue = this.fldDiscountValue,
        fldStartDate = this.fldStartDate,
        fldEndDate = this.fldEndDate,
        fldStatus = this.fldStatus
    )
