package com.rfz.appflotal.presentation.ui.inspection.components

import androidx.annotation.StringRes
import com.rfz.appflotal.R

enum class UploadingInspectionMessage(@param:StringRes val message: Int) {
    SUCCESS(R.string.inspeccion_exitosa_mensaje),
    GENERAL_ERROR(R.string.error_registrar_inspeccion),
}