package com.rfz.appflotal.data.network.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rfz.appflotal.R
import com.rfz.appflotal.data.model.fcmessaging.AppUpdateMessage
import com.rfz.appflotal.data.repository.fcmessaging.AppUpdateMessageRepositoryImpl
import com.rfz.appflotal.presentation.ui.inicio.ui.InicioActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var appUpdateMessageRepository: AppUpdateMessageRepositoryImpl

    private val fcmScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
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


        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            it.body?.let { body -> sendNotification(it.title, it.body) }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
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
            horaFinal = payload["hora_final"] ?: ""
        )

        fcmScope.launch {
            appUpdateMessageRepository.saveMessage(update)
        }

        Log.d(TAG, "Short lived task is done.")
    }

    private fun sendNotification(title: String?, messageBody: String?) {
        val requestCode = 0
        val intent = Intent(this, InicioActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = getString(R.string.app_fcm_channel)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title ?: "Flotal")
            .setContentText(messageBody ?: getString(R.string.notificacion_nueva))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel FCM Flotal",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun sendRegistrationToServer(token: String?) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }


    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }

}
