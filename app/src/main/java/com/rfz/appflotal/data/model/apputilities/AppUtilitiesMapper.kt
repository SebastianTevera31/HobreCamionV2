package com.rfz.appflotal.data.model.apputilities

fun UserOpinion.toDto() = UserOpinionDto(
    id = opinion,
    registerDate = registerDate.toString()
)

fun TermsAndConditions.toDto() = TermsAndConditionsDto(
    termsEs = termsEs,
    termsEn = termsEn
)