package com.rfz.appflotal.data.network.messaging

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.fcmessaging.AppUpdateMessage
import com.rfz.appflotal.data.network.service.fgservice.currentAppLocaleFromAppCompat
import com.rfz.appflotal.data.network.service.fgservice.localized
import com.rfz.appflotal.data.repository.fcmessaging.AppUpdateMessageRepositoryImpl
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.presentation.ui.inicio.ui.InicioActivity
import com.rfz.appflotal.presentation.ui.utils.FireCloudMessagingType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var appUpdateMessageRepository: AppUpdateMessageRepositoryImpl

    @Inject
    lateinit var getTasksUseCase: GetTasksUseCase

    private val fcmScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (!applicationContext.hasNotificationPermission()) {
            return
        }

        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            // Check if data needs to be processed by long running job
            if (isLongRunningJob()) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob()
            } else {
                // Handle message within 10 seconds
                handleNow(remoteMessage.data)
            }
        }
    }

    private fun isLongRunningJob() = false

    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private fun scheduleJob() {
        // [START dispatch_job]
        val work = OneTimeWorkRequest.Builder(MessagingWorker::class.java).build()
        WorkManager.getInstance(this).beginWith(work).enqueue()
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow(payload: Map<String, String>) {
        val update = AppUpdateMessage(
            tipo = payload["tipo"] ?: "",
            fecha = payload["fecha"] ?: "",
            horaInicio = payload["hora_inicio"] ?: "",
            horaFinal = payload["hora_final"] ?: "",
            version = payload["version"] ?: "",
        )

        fcmScope.launch {
            appUpdateMessageRepository.enqueueMessage(update)
        }

        Log.d(TAG, "Short lived task is done.")

        sendNotification()
    }

    private fun sendNotification() {
        val appLocale = currentAppLocaleFromAppCompat() ?: Locale.getDefault()
        val lctx = this.localized(appLocale)
        fcmScope.launch {
            val user = getTasksUseCase().first()
            val (title, messageBody) = getNotificationMessage(lctx, !user.isNullOrEmpty())
            if (title.isEmpty()) return@launch
            createNotification(title, messageBody)
        }
    }

    private suspend fun getNotificationMessage(
        lctx: Context,
        isThereUser: Boolean
    ): Pair<String, String> {
        val tipo = appUpdateMessageRepository.pendingMessagesFlow.first().firstOrNull()?.tipo
        if (tipo != null) {
            val title = when (tipo) {
                FireCloudMessagingType.MANTENIMIENTO.value, FireCloudMessagingType.ARREGLO_URGENTE.value -> lctx.getString(
                    R.string.mantenimiento_programado
                )

                FireCloudMessagingType.TERMINOS.value -> lctx.getString(R.string.actualizacion_de_terminos_y_condiciones)
                FireCloudMessagingType.CAMBIO_DE_PLAN.value -> {
                    if (isThereUser) lctx.getString(R.string.actualizacion_de_plan) else ""
                }

                FireCloudMessagingType.SERVICIO_AUTO.value -> ""
                FireCloudMessagingType.ACTUALIZACION.value -> lctx.getString(R.string.actualizacion_disponible)
                else -> tipo
            }


            val messageBody = when (tipo) {
                FireCloudMessagingType.MANTENIMIENTO.value,
                FireCloudMessagingType.ARREGLO_URGENTE.value ->
                    lctx.getString(R.string.mantenimiento_message)

                FireCloudMessagingType.TERMINOS.value ->
                    lctx.getString(R.string.terms_message)

                FireCloudMessagingType.CAMBIO_DE_PLAN.value -> {
                    if (isThereUser) lctx.getString(R.string.paymentplan_message) else ""
                }

                FireCloudMessagingType.SERVICIO_AUTO.value ->
                    lctx.getString(R.string.autoservice_message)

                FireCloudMessagingType.ACTUALIZACION.value ->
                    lctx.getString(R.string.update_message)

                else ->
                    lctx.getString(R.string.generic_message)
            }
            return Pair(title, messageBody)
        }
        return Pair("", "")
    }

    private fun createNotification(title: String, messageBody: String) {
        val intent = Intent(this, InicioActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = getString(R.string.app_fcm_channel)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel FCM Flotal",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun sendRegistrationToServer(token: String?) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    fun Context.hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
