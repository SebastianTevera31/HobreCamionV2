package com.rfz.appflotal.data.model.originaldesign.dto

data class CrudOriginalDesignDto(
    val id_originalDesign: Int,
    val fld_model: String,
    val fld_description: String,
    val c_brands_fk_1: Int,
    val c_utilization_fk_2: Int,
    val fld_notes: String,
)
