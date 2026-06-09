package com.rfz.appflotal.data.model.couponbook

fun GetVoucherByUserResponse.toDomain(): Coupon {
    val status = VoucherStatusType.entries.find { it.id == this.fldStatus }
    return Coupon(
        fldCode = this.fldCode,
        fldTitle = this.fldTitle,
        fldDescription = this.fldDescription,
        fldDiscountType = this.fldDiscountType,
        fldDiscountValue = this.fldDiscountValue,
        fldStartDate = this.fldStartDate,
        fldEndDate = this.fldEndDate,
        fldStatus = status ?: VoucherStatusType.INACTIVO
    )
}

fun ValidateVoucherResponseDto.toDomain(): ValidatedVoucher {
    return ValidatedVoucher(
        idVoucher = this.idVoucher,
        idCoupon = this.idCoupon,
        title = this.fldTitle,
        discountType = this.fldDiscountType,
        discountValue = this.fldDiscountValue,
        used = this.fldUsed,
        status = this.fldStatus
    )
}

fun GetCouponsResponse.toDomain(): Coupon {
    return Coupon(
        fldCode = this.idCoupon.toString(),
        fldTitle = this.fldTitle,
        fldDescription = this.fldDescription,
        fldDiscountType = this.fldDiscountType,
        fldDiscountValue = this.fldDiscountValue.toString(),
        fldStartDate = this.fldStartDate,
        fldEndDate = this.fldEndDate,
        fldStatus = VoucherStatusType.VALIDO
    )
}
