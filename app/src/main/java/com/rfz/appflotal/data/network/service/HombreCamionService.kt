package com.rfz.appflotal.data.network.service

import android.Manifest
import android.app.ForegroundServiceStartNotAllowedException
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat.startForeground
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

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var oldestTimestamp: String? = null

    override fun onBind(p0: Intent?): IBinder? = null

    private var user: Pair<Int?, String?> = Pair(null, null)

    override fun onCreate() {
        super.onCreate()

        coroutineScope.launch {
            getUserUseCase().collect { data ->
                user = Pair(data.first().id_user, data.first().fld_token)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        // Configurar e iniciar del servicio
        startForegroundService()

        // Habilitar observador WiFi
        wifiUseCase.doConnect()

        // Iniciar conexión Bluetooth
        initBluetoothConnection()

        readDataFromMonitor()

        // Reiniciando servicio
        Log.d("HombreCamionService", "Servicio iniciado")

        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startForegroundService() {

        val bluetoothPermission =
            PermissionChecker.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
        if (bluetoothPermission != PermissionChecker.PERMISSION_GRANTED) {
            stopSelf()
            return
        }

        try {
            val notification = createNotification()

            if (Build.VERSION.SDK_INT >= 34) {
                startForeground(
                    /* service = */ this,
                    /* id = */ 100, // Cannot be 0
                    /* notification = */ notification,
                    /* foregroundServiceType = */
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC or ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
                )
            } else {
                startForeground(100, notification)
            }
        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                && e is ForegroundServiceStartNotAllowedException
            ) {
                Log.e("HombreCamionService", "${e.message}")
            }
        }
    }

    private fun createNotification(): Notification {
        val channelId = "bt_channel_id"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Bluetooth Service Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setShowBadge(false)
            }

            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Servicio HombreCamion")
            .setContentText("Recibiendo datos del monitor")
            .setSmallIcon(R.drawable.truckdriver)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun initBluetoothConnection() {
        coroutineScope.launch {
            Log.d("HombreCamionService", "Iniciando Bluetooth...")
            bluetoothUseCase.doConnect("80:F5:B5:70:5C:8F")
            bluetoothUseCase.doStartRssiMonitoring()
        }
    }

    private fun readDataFromMonitor() {
        coroutineScope.launch {
            val currentUserId = getUserUseCase().first()[0].id_user
            if (user.first != null) {
                bluetoothUseCase()
                    .distinctUntilChangedBy { it.timestamp }
                    .collect { data ->
                        val bluetoothSignalQuality = data.bluetoothSignalQuality

                        val dataFrame = data.dataFrame
                        Log.d("HombreCamionService", "Dataframe: $dataFrame, ")
                        if (validateBluetoothConnectivity(bluetoothSignalQuality) && dataFrame != null) {

                            // Cambiar: Debe almancenarse el ID del Monitor
                            val monitorId = currentUserId
                            Log.d("HombreCamionService", "UserId: $monitorId")
                            val timestamp = getCurrentDate()

                            val sensorId = decodeDataFrame(dataFrame, MonitorDataFrame.SENSOR_ID)

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
                token = user.second!!,
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

    private suspend fun getUnsentRecords(userId: Int) {
        val records = sensorTableUseCase.doGetUnsentRecords(
            userId = userId,
        )

        records.forEach {
            if (it != null) {
//                assemblyUseCase.doSendMonitorData()
            }
        }
    }
}