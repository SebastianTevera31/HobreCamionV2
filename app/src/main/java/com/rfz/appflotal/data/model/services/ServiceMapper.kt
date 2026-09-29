package com.rfz.appflotal.data.model.services

import com.rfz.appflotal.core.util.Commons.formatToLongDate
import com.rfz.appflotal.data.model.services.response.ServiceResponseDto
import com.rfz.appflotal.data.model.services.response.TypeServiceDto
import com.rfz.appflotal.presentation.ui.services.model.CatalogItemUi
import com.rfz.appflotal.presentation.ui.services.model.ServiceUi

fun ServiceResponseDto.toDomain(): ServiceUi {
    return ServiceUi(
        id = idService,
        type = typeService,
        description = description,
        price = price,
        quantity = quantity,
        provider = provider,
        date = formatToLongDate(dateString = date, inputFormat = "yyyy-MM-dd'T'HH:mm:ss"),
        rawDate = date
    )
}

fun TypeServiceDto.toDomain(): CatalogItemUi {
    return CatalogItemUi(
        id = idTypeService,
        name = description
    )
}
