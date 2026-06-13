package com.rfz.appflotal.data.model.couponbook

fun GetVoucherByUserResponse.toDomain(): Coupon {
    val status = VoucherStatusType.entries.find { it.id == this.fldStatus }
    val discountType = if (this.fldDiscountType != 1) VoucherDiscountType.PORCENTAJE
    else VoucherDiscountType.DINERO
    return Coupon(
        fldCode = this.fldCode,
        fldTitle = this.fldTitle,
        fldDescription = this.fldDescription,
        fldDiscountType = discountType,
        fldDiscountValue = this.fldDiscountValue,
        fldStartDate = this.fldStartDate,
        fldEndDate = this.fldEndDate,
        fldStatus = status ?: VoucherStatusType.INACTIVO
    )
}

fun ValidateVoucherResponseDto.toDomain(): ValidatedVoucher {
    val discountType = if (this.fldDiscountType != 1) VoucherDiscountType.PORCENTAJE
    else VoucherDiscountType.DINERO
    return ValidatedVoucher(
        idVoucher = this.idVoucher,
        idCoupon = this.idCoupon,
        title = this.fldTitle,
        discountType = discountType,
        discountValue = this.fldDiscountValue,
        used = this.fldUsed,
        status = this.fldStatus
    )
}

fun GetCouponsResponse.toDomain(): Coupon {
    val discountType = if (this.fldDiscountType != 1) VoucherDiscountType.PORCENTAJE
    else VoucherDiscountType.DINERO
    return Coupon(
        fldCode = this.idCoupon.toString(),
        fldTitle = this.fldTitle,
        fldDescription = this.fldDescription,
        fldDiscountType = discountType,
        fldDiscountValue = this.fldDiscountValue.toString(),
        fldStartDate = this.fldStartDate,
        fldEndDate = this.fldEndDate,
        fldStatus = VoucherStatusType.VALIDO
    )
}
