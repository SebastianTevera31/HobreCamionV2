package com.rfz.appflotal.data.repository.promotions

import com.rfz.appflotal.data.model.database.AppHCEntity
import com.rfz.appflotal.data.repository.fcmessaging.toSafePaymentPlanType
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.presentation.ui.inicio.ui.PaymentPlanType
import kotlinx.coroutines.flow.first

/**
 * Token con el que se puede consultar GetDiscounts: solo una sesión activa de plan Complete.
 * Null sin sesión (p. ej. tras cerrar sesión) o con cualquier otro plan.
 */
fun AppHCEntity?.completeSessionToken(): String? = this
    ?.takeIf { it.paymentPlan.toSafePaymentPlanType() == PaymentPlanType.Complete }
    ?.fld_token
    ?.takeIf { it.isNotBlank() }

suspend fun GetTasksUseCase.completeSessionToken(): String? =
    invoke().first().firstOrNull().completeSessionToken()

/**
 * Una sincronización ya iniciada se vuelve a validar antes de cada llamada: si el usuario cerró
 * sesión (o entró otro) a la mitad, no se hacen más llamadas ni se escribe en la caché.
 */
suspend fun GetTasksUseCase.isSameCompleteSession(token: String): Boolean =
    completeSessionToken() == token
