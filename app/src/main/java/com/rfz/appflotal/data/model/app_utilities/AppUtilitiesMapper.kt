package com.rfz.appflotal.data.model.app_utilities

fun UserOpinion.toDto(): UserOpinionDto {
    return UserOpinionDto(
        id = opinion,
        registerDate = registerDate.toString()
    )
}