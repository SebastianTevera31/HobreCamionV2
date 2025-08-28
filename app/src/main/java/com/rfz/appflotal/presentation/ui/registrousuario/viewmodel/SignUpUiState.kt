package com.rfz.appflotal.presentation.ui.registrousuario.viewmodel

import androidx.annotation.StringRes
import com.rfz.appflotal.R
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType

data class SignUpUiState(
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val country: Pair<Int, String>? = null,
    val sector: Pair<Int, String>? = null,
    val vehicleType: String = "",
    val plates: String = "",
    val countries: Map<Int, String> = emptyMap(),
    val sectors: Map<Int, String> = emptyMap(),
    val paymentPlan: PaymentPlanType = PaymentPlanType.None
)

sealed class AuthFlow {
    object None : AuthFlow()
    object SignUp : AuthFlow()
    object Login : AuthFlow()
}

enum class SignUpAlerts(@StringRes val message: Int = -1) {
    NAME_ALERT(R.string.signup_name_alert),
    EMAIL_ALERT(R.string.signup_email_alert),
    PASSWORD_ALERT(R.string.signup_password_alert),
    COUNTRY_ALERT(R.string.signup_country_alert),
    INDUSTRY_ALERT(R.string.signup_industry_alert),
    VEHICLE_ALERT(R.string.signup_typevehicle_alert),
    PLATES_ALERT(R.string.signup_plates_alert),
    SIGNUP_ALERT(R.string.signup_successfull),
    UNKNOWN(R.string.no_resultados)
}
