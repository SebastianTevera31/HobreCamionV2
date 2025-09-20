package com.rfz.appflotal.presentation.ui.inicio.ui

enum class PaymentPlanType(val planName: String? = null) {
    Complete(planName = "Complete"), OnlyTPMS(planName = "Only TPMS"), Free(planName = "Free"), None(planName = "None")
}