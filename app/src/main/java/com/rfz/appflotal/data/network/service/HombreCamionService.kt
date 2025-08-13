package com.rfz.appflotal.data.network.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.PermissionChecker
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.core.util.Commons.validateBluetoothConnectivity
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.data.model.flotalSoft.SensorTpmsEntity
import com.rfz.appflotal.data.repository.bluetooth.MonitorDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeDataFrame
import com.rfz.appflotal.domain.bluetooth.BluetoothUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.database.SensorTableUseCase
import com.rfz.appflotal.domain.tpmsUseCase.ApiTpmsUseCase
import com.rfz.appflotal.domain.wifi.WifiUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HombreCamionService : Service() {
    @Inject
    lateinit var bluetoothUseCase: BluetoothUseCase

    @Inject
    lateinit var apiTpmsUseCase: ApiTpmsUseCase

    @Inject
    lateinit var wifiUseCase: WifiUseCase

    @Inject
    lateinit var sensorTableUseCase: SensorTableUseCase

    @Inject
    lateinit var getUserUseCase: GetTasksUseCase

    lateinit var notificationManager: NotificationManager

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var oldestTimestamp: String? = null

    private var isStaterd = false

    override fun onBind(p0: Intent?): IBinder? = null

    private var user: Pair<Int?, String?> = Pair(null, null)

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onDestroy() {
        super.onDestroy()
        val restartIntent = Intent("android.intent.action.SERVICE_RESTARTED")
            .setPackage(packageName)
        sendBroadcast(restartIntent)
        isStaterd = false
        coroutineScope.cancel()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        // Configurar e iniciar del servicio
        if (!isStaterd) {
            startForegroundService()
            isStaterd = true
        }

        // Reiniciando servicio
        Log.d("HombreCamionService", "Servicio iniciado")


        // Habilitar observador WiFi
        wifiUseCase.doConnect()

        // Iniciar conexión Bluetooth
        initBluetoothConnection()

        readDataFromMonitor()

        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startForegroundService() {
        // Movier a la seccion de invocacion
        // Verificar si se aceptaron permisos de Bluetooth.
        val bluetoothPermission =
            PermissionChecker.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
        if (bluetoothPermission != PermissionChecker.PERMISSION_GRANTED) {
            stopSelf()
            return
        }

        createServiceNotificationChannel()

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Servicio HombreCamion")
            .setContentText("Recibiendo datos del monitor")
            .setSmallIcon(R.drawable.truckdriver)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    private fun createServiceNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setShowBadge(false)
        }

        notificationManager.createNotificationChannel(channel)
    }

    private fun initBluetoothConnection() {
        coroutineScope.launch {
            val dataUser = getUserUseCase().first()[0]
            Log.d("HombreCamionService", "Iniciando Bluetooth...")
            bluetoothUseCase.doConnect(dataUser.monitorMac)
            bluetoothUseCase.doStartRssiMonitoring()
        }
    }

    private fun readDataFromMonitor() {
        coroutineScope.launch {
            val dataUser = getUserUseCase().first()[0]
            val currentMonitorId = dataUser.id_monitor
            bluetoothUseCase()
                .distinctUntilChangedBy { it.timestamp }
                .collect { data ->
                    val bluetoothSignalQuality = data.bluetoothSignalQuality

                    val dataFrame = data.dataFrame
                    Log.d("HombreCamionService", "Dataframe: $dataFrame, ")
                    if (validateBluetoothConnectivity(bluetoothSignalQuality) && dataFrame != null) {

                        // Cambiar: Debe almancenarse el ID del Monitor
                        val monitorId = currentMonitorId
                        Log.d("HombreCamionService", "UserId: $monitorId")
                        val timestamp = getCurrentDate()

                        val sensorId =
                            decodeDataFrame(dataFrame, MonitorDataFrame.SENSOR_ID)

                        sensorTableUseCase.doInsert(
                            SensorTpmsEntity(
                                monitorId = 3,
                                sensorId = sensorId,
                                dataFrame = dataFrame,
                                timestamp = timestamp,
                                sent = false
                            )
                        )

                        // Enviar datos a API
                        sendDataToApi(dataFrame, timestamp, monitorId)
                    }
                }
        }
    }

    private suspend fun sendDataToApi(dataFrame: String, timestamp: String, userId: Int) {
        val wifiStatus = wifiUseCase()
        Log.d("HombreCamionService", "WifiStatus: $wifiStatus")
        if (wifiStatus.value == NetworkStatus.Connected) {

            val localOldestTimestamp = oldestTimestamp
            if (localOldestTimestamp != null) {
                getUnsentRecords(userId)
                oldestTimestamp = null
            }

            apiTpmsUseCase.doPostSensorData(
                fldFrame = dataFrame,
                monitorId = 3,
                fldDateData = timestamp
            )

            sensorTableUseCase.doSetRecordStatus(
                monitorId = 3,
                timestamp = timestamp,
                sendStatus = true
            )

        } else if (wifiStatus.value == NetworkStatus.Disconnected && oldestTimestamp.isNullOrEmpty()) {
            oldestTimestamp = timestamp
        }
    }

    private suspend fun getUnsentRecords(monitorId: Int) {
        val records = sensorTableUseCase.doGetUnsentRecords(
            monitorId = monitorId,
        )

        records.forEach {
            if (it != null) {
                val result = apiTpmsUseCase.doPostSensorData(
                    it.dataFrame, it.monitorId, it.timestamp
                )

                when (result) {
                    is ResultApi.Success -> {
                        sensorTableUseCase.doSetRecordStatus(it.monitorId, it.timestamp, true)
                    }

                    is ResultApi.Error -> {
                        Log.d(
                            "HombreCamionServicio",
                            "Error al enviar datos almacenados al servidor."
                        )
                    }

                    ResultApi.Loading -> {}
                }

            }
        }
    }

    companion object {
        const val CHANNEL_ID = "1003"
        const val ONGOING_NOTIFICATION_ID = 103

        fun startService(context: Context) {
            val intent = Intent(context, HombreCamionService::class.java)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) context.startService(intent)
            else context.startForegroundService(intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, HombreCamionService::class.java)
            context.stopService(intent)
        }
    }
}