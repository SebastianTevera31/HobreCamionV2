package com.rfz.appflotal.data.network.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.PermissionChecker
import com.rfz.appflotal.R
import com.rfz.appflotal.core.util.AppLocale
import com.rfz.appflotal.core.util.Commons.getCurrentDate
import com.rfz.appflotal.core.util.Commons.validateBluetoothConnectivity
import com.rfz.appflotal.core.util.tpms.getBatteryStatus
import com.rfz.appflotal.core.util.tpms.getHighPressureStatus
import com.rfz.appflotal.core.util.tpms.getHighTemperatureStatus
import com.rfz.appflotal.core.util.tpms.getLowPressureStatus
import com.rfz.appflotal.core.util.tpms.getPressure
import com.rfz.appflotal.core.util.tpms.getPunctureStatus
import com.rfz.appflotal.core.util.tpms.getTemperature
import com.rfz.appflotal.core.util.tpms.getTire
import com.rfz.appflotal.data.NetworkStatus
import com.rfz.appflotal.data.model.database.DataframeEntity
import com.rfz.appflotal.data.model.database.SensorDataUpdate
import com.rfz.appflotal.data.network.service.fgservice.HombreCamionServiceController
import com.rfz.appflotal.data.network.service.fgservice.currentAppLocaleFromAppCompat
import com.rfz.appflotal.data.network.service.fgservice.localized
import com.rfz.appflotal.data.repository.bluetooth.BluetoothSignalQuality
import com.rfz.appflotal.data.repository.bluetooth.MonitorDataFrame
import com.rfz.appflotal.data.repository.bluetooth.decodeDataFrame
import com.rfz.appflotal.data.repository.database.SensorDataTableRepository
import com.rfz.appflotal.domain.bluetooth.BluetoothUseCase
import com.rfz.appflotal.domain.database.CoordinatesTableUseCase
import com.rfz.appflotal.domain.database.DataframeTableUseCase
import com.rfz.appflotal.domain.database.GetTasksUseCase
import com.rfz.appflotal.domain.tpmsUseCase.ApiTpmsUseCase
import com.rfz.appflotal.domain.wifi.WifiUseCase
import com.rfz.appflotal.presentation.ui.inicio.ui.InicioActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class HombreCamionService : Service() {
    @Inject
    lateinit var bluetoothUseCase: BluetoothUseCase

    @Inject
    lateinit var apiTpmsUseCase: ApiTpmsUseCase

    @Inject
    lateinit var sensorDataTableRepository: SensorDataTableRepository

    @Inject
    lateinit var wifiUseCase: WifiUseCase

    @Inject
    lateinit var dataframeTableUseCase: DataframeTableUseCase

    @Inject
    lateinit var getUserUseCase: GetTasksUseCase

    @Inject
    lateinit var hcServiceController: HombreCamionServiceController

    @Inject
    lateinit var coordinatesTableUseCase: CoordinatesTableUseCase

    lateinit var notificationManager: NotificationManager
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var jobWifi: Job? = null
    private var jobBleConn: Job? = null
    private var jobLang: Job? = null
    private var jobUpdateStatus: Job? = null
    private var oldestTimestamp: String? = null
    private var hasStarted = false
    private var readingHasStarted = false
    private var currentMac: String? = null

    override fun onBind(p0: Intent?): IBinder? = null
    private lateinit var notificationCompactBuilder: NotificationCompat.Builder

    private var btReceiver: BroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        startForegroundServiceSafelyOnce()

        hcServiceController.getDataApi()

        startWifiOnce()
        startBleConnOnce()
        startReadTpmsOnce()
        startBtStatusOnce()
        startLangOnce()
        updateSensorStatus()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (btReceiver != null) this.unregisterReceiver(btReceiver)

        val restartIntent = Intent("android.intent.action.SERVICE_RESTARTED")
            .setPackage(packageName)
        sendBroadcast(restartIntent)
        hasStarted = false

        hcServiceController.stopService()

        serviceScope.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            "ACTION_RESTART" -> {
                restartBleOnly()
            }
        }

        return START_STICKY
    }

    @SuppressLint("NewApi")
    private fun startForegroundServiceSafelyOnce() {
        if (!hasStarted) {
            startForegroundService() // tu notificación + startForeground(...)
            hasStarted = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startForegroundService() {
        val notificationIntent = Intent(this, InicioActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Movier a la seccion de invocacion
        // Verificar si se aceptaron permisos de Bluetooth.
        val bluetoothPermission =
            PermissionChecker.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
        if (bluetoothPermission != PermissionChecker.PERMISSION_GRANTED) {
            stopSelf()
            return
        }

        createServiceNotificationChannel()

        notificationCompactBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        rebuildNotificationTexts()
        startForeground(
            ONGOING_NOTIFICATION_ID,
            notificationCompactBuilder.build(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
        )
    }

    private fun createServiceNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun startWifiOnce() {
        if (jobWifi?.isActive != true) {
            jobWifi = serviceScope.launch {
                wifiUseCase.doConnect()
            }
        }
    }

    private fun startBleConnOnce() {
        if (jobBleConn?.isActive != true) {
            jobBleConn = serviceScope.launch {
                initBluetoothConnection() // debe ser segura y no duplicar GATT
            }
        }
    }

    private fun startReadTpmsOnce() {
        if (!readingHasStarted) {
            readingHasStarted = true
            serviceScope.launch {
                readDataFromMonitor()
            }
        }
    }

    private fun startBtStatusOnce() {
        btReceiver = bluetoothUseCase.getBtReceiver()
        if (btReceiver != null) {
            val filter = IntentFilter().apply {
                addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            }
            this.registerReceiver(btReceiver, filter)
        }
        serviceScope.launch {
            readBluetoothStatus()
        }
    }

    private fun startLangOnce() {
        if (jobLang?.isActive != true) {
            jobLang = serviceScope.launch {
                readLanguageUpdate()
            }
        }
    }

    private fun restartBleOnly() {
        // Reinicia SOLO la parte BLE (sin tocar WiFi/Idioma/TPMS si no es necesario)
        jobBleConn?.cancel()

        jobBleConn = null

        startBleConnOnce()
    }

    private suspend fun initBluetoothConnection() {
        val record = getUserUseCase().first()
        if (record.isEmpty()) return
        val dataUser = record.first()
        Log.d("HombreCamionService", "Iniciando Bluetooth...")
        currentMac = dataUser.monitorMac
        bluetoothUseCase.doConnect(dataUser.monitorMac)
    }

    private suspend fun readBluetoothStatus() {
        bluetoothUseCase().distinctUntilChangedBy { it.bluetoothSignalQuality }
            .collect { data ->
                val quality = data.bluetoothSignalQuality

                if (BluetoothSignalQuality.Desconocida != quality) {
                    CONNECTION_CONTEXT_MESSAGE = R.string.recibiendo_datos_monitor
                }

                when (quality) {
                    BluetoothSignalQuality.Excelente -> {
                        CONNECTION_TITLE_MESSAGE = R.string.conexion_status
                        CONNECTION_STATUS_MESSAGE = BluetoothSignalQuality.Excelente.signalText
                    }

                    BluetoothSignalQuality.Aceptable -> {
                        CONNECTION_TITLE_MESSAGE = R.string.conexion_status
                        CONNECTION_STATUS_MESSAGE = BluetoothSignalQuality.Aceptable.signalText
                    }

                    BluetoothSignalQuality.Pobre -> {
                        CONNECTION_TITLE_MESSAGE = R.string.conexion_status
                        CONNECTION_STATUS_MESSAGE = BluetoothSignalQuality.Pobre.signalText
                    }

                    BluetoothSignalQuality.Desconocida -> {
                        CONNECTION_CONTEXT_MESSAGE =
                            BluetoothSignalQuality.Desconocida.signalText
                        CONNECTION_TITLE_MESSAGE = null
                        CONNECTION_STATUS_MESSAGE = null
                    }
                }

                rebuildNotificationTexts()
            }
    }

    private suspend fun readDataFromMonitor() {
        val dataUser = getUserUseCase().first()
        if (dataUser.isNotEmpty()) {
            val currentMonitorId = dataUser.first().id_monitor
            bluetoothUseCase()
                .distinctUntilChangedBy { it.timestamp }
                .collectLatest { data ->
                    Log.i("HombreCamionService", "Leyendo datos desde el servicio")
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

                        dataframeTableUseCase.doInsert(
                            DataframeEntity(
                                monitorId = monitorId,
                                sensorId = sensorId,
                                dataFrame = dataFrame,
                                timestamp = timestamp,
                                sent = false,
                                active = true
                            )
                        )

                        val highTemperatureAlert = getHighTemperatureStatus(dataFrame)
                        val highPressureAlert = getHighPressureStatus(dataFrame)
                        val lowPressureAlert = getLowPressureStatus(dataFrame)
                        val lowBatteryAlert = getBatteryStatus(dataFrame)
                        val puncture = getPunctureStatus(dataFrame)

                        val tire = getTire(dataFrame)

                        sensorDataTableRepository.updateSensorDataExceptLastInspection(
                            update = SensorDataUpdate(
                                idMonitor = monitorId,
                                tire = tire,
                                tireNumber = "",
                                timestamp = timestamp,
                                temperature = getTemperature(dataFrame).toInt(),
                                pressure = getPressure(dataFrame).toInt(),
                                highTemperatureAlert = highTemperatureAlert,
                                highPressureAlert = highPressureAlert,
                                lowPressureAlert = lowPressureAlert,
                                lowBatteryAlert = lowBatteryAlert,
                                punctureAlert = puncture,
                                active = true
                            )
                        )

                        val inAlert = highTemperatureAlert || highPressureAlert
                                || lowPressureAlert || lowBatteryAlert

                        coordinatesTableUseCase.updateCoordinates(
                            monitorId,
                            tire,
                            isAlert = inAlert,
                            isActive = true
                        )

                        // Enviar datos a API
                        sendDataToApi(dataFrame, timestamp, monitorId)
                    }
                }
        }
    }

    private suspend fun sendDataToApi(dataFrame: String, timestamp: String, monitorId: Int) {
        val wifiStatus = wifiUseCase()
        Log.d("HombreCamionService", "WifiStatus: $wifiStatus")
        if (wifiStatus.value == NetworkStatus.Connected) {

            val localOldestTimestamp = oldestTimestamp

            if (localOldestTimestamp != null) {
                getUnsentRecords(monitorId)
                oldestTimestamp = null
            } else {
                apiTpmsUseCase.doPostSensorData(
                    fldFrame = dataFrame,
                    monitorId = monitorId,
                    fldDateData = timestamp
                )

                dataframeTableUseCase.doSetRecordStatus(
                    monitorId = monitorId,
                    timestamp = timestamp,
                    sendStatus = true,
                    active = true
                )
            }
        } else if (wifiStatus.value == NetworkStatus.Disconnected && oldestTimestamp.isNullOrEmpty()) {
            oldestTimestamp = timestamp
        }
    }

    private suspend fun getUnsentRecords(monitorId: Int) {
        val records = dataframeTableUseCase.doGetUnsentRecords(
            monitorId = monitorId,
        )

        records.forEach {
            if (it != null) {
                val result = apiTpmsUseCase.doPostSensorData(
                    it.dataFrame, it.monitorId, it.timestamp
                )

                when (result) {
                    is ApiResult.Success -> {
                        dataframeTableUseCase.doSetRecordStatus(
                            it.monitorId, it.timestamp,
                            sendStatus = true,
                            active = true
                        )
                    }

                    is ApiResult.Error -> {
                        Log.d(
                            "HombreCamionServicio",
                            "Error al enviar datos almacenados al servidor."
                        )
                    }

                    ApiResult.Loading -> {}
                }
            }
        }
    }

    // FUNCIONES DE CAMBIO DE IDIOMA
    private suspend fun readLanguageUpdate() {
        AppLocale.currentLocale.distinctUntilChangedBy { it.language }.collect {
            rebuildNotificationTexts()
        }
    }

    private fun rebuildNotificationTexts() {
        val appLocale = currentAppLocaleFromAppCompat() ?: Locale.getDefault()
        val lctx = this.localized(appLocale)

        val title = lctx.getString(R.string.hombrecamion_conexion_tpms)

        val statusContent =
            if (CONNECTION_TITLE_MESSAGE != null && CONNECTION_STATUS_MESSAGE != null) {
                lctx.getString(
                    CONNECTION_TITLE_MESSAGE!!,
                    lctx.getString(CONNECTION_STATUS_MESSAGE!!)
                )
            } else null

        val titleContent =
            if (CONNECTION_CONTEXT_MESSAGE != null) lctx.getString(CONNECTION_CONTEXT_MESSAGE!!) else null

        val content = if (statusContent != null) "$titleContent. $statusContent" else titleContent

        notificationCompactBuilder.setContentTitle(title).setContentText(content)

        // Actualiza la notificación foreground (basta con notify con el mismo ID)
        notificationManager.notify(ONGOING_NOTIFICATION_ID, notificationCompactBuilder.build())
    }

    @SuppressLint("NewApi")
    private fun updateSensorStatus() {
        if (jobUpdateStatus?.isActive == true) return
        jobUpdateStatus = serviceScope.launch(Dispatchers.IO) {
            while (isActive) {
                val monitorId = getUserUseCase().first().firstOrNull()?.id_monitor
                try {
                    if (monitorId != null) {
                        val cutoff = LocalDateTime.ofInstant(
                            Instant.now(), ZoneId.systemDefault()
                        ).minusMinutes(15) // ISO-8601 Z
                        val tires = sensorDataTableRepository.getLastData(monitorId)

                        tires.forEach {
                            val tireDate = LocalDateTime.parse(it.timestamp.removeSuffix("Z"))
                            if (tireDate.isBefore(cutoff)) {
                                sensorDataTableRepository.deactivateTireRecord(monitorId, it.tire)
                                coordinatesTableUseCase.updateCoordinates(
                                    monitorId,
                                    it.tire,
                                    isActive = false,
                                    isAlert = false
                                )
                            }
                        }
                    }
                } catch (t: Throwable) {
                    Log.e("Updater", "Error actualizando sensores", t)
                }
                delay(4 * 60_000L) // 8 minutos
            }
        }
    }

    companion object {
        const val CHANNEL_ID = "1003"
        const val ONGOING_NOTIFICATION_ID = 103
        var CONNECTION_CONTEXT_MESSAGE: Int? = null
        var CONNECTION_TITLE_MESSAGE: Int? = null
        var CONNECTION_STATUS_MESSAGE: Int? = null

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