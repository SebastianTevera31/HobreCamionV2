package com.rfz.appflotal.data.model.flotalSoft

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppFlotalEntity (
    @PrimaryKey (autoGenerate = true)
    val id: Int = 0,
    val id_user: Int,
    val fld_name: String,
    val fld_email: String,
    val fld_token: String,
    val  fecha: String



)