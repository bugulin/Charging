package cz.bornasp.charging

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.IBinder
import android.util.Log
import cz.bornasp.charging.data.AppDataContainer
import cz.bornasp.charging.data.BatteryChargingSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime

private const val TAG = "ChargeMonitor"
private const val SERVICE_NOTIFICATION_CHANNEL = "charge-monitor"

class ChargeMonitor : Service() {
    private lateinit var powerBroadcastReceiver: PowerBroadcastReceiver

    override fun onBind(intent: Intent?): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Promote service to the foreground
        val notification: Notification = Notification.Builder(this, SERVICE_NOTIFICATION_CHANNEL)
            .build()
        startForeground(startId, notification)

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")

        createNotificationChannel()
        registerBroadcastReceiver()
    }

    override fun onDestroy() {
        unregisterReceiver(powerBroadcastReceiver)
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
    }

    /**
     * Ensure that the channel of foreground service's mandatory notification exists.
     */
    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            SERVICE_NOTIFICATION_CHANNEL,
            getString(R.string.service_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.service_channel_description)
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(serviceChannel)
    }

    /**
     * Register broadcast receiver for [Intent.ACTION_POWER_CONNECTED] and
     * [Intent.ACTION_POWER_DISCONNECTED].
     */
    private fun registerBroadcastReceiver() {
        powerBroadcastReceiver = PowerBroadcastReceiver()
        IntentFilter().let { ifilter ->
            ifilter.addAction(Intent.ACTION_POWER_CONNECTED)
            ifilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
            registerReceiver(powerBroadcastReceiver, ifilter)
        }
    }
}

private class PowerBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        val batteryPercentage: Float? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }

        runBlocking {
            launch {
                when (intent.action) {
                    Intent.ACTION_POWER_CONNECTED -> createSession(context, batteryPercentage)
                    Intent.ACTION_POWER_DISCONNECTED -> endSession(context, batteryPercentage)
                }
            }
        }
        Log.d(TAG, "Action: ${intent.action} ($batteryPercentage%}")
    }

    private suspend fun createSession(context: Context, batteryPercentage: Float?) {
        val repository = AppDataContainer(context).batteryChargingSessionRepository
        withContext(Dispatchers.IO) {
            repository.insertRecord(
                BatteryChargingSession(
                    startTime = OffsetDateTime.now(),
                    initialChargePercentage = batteryPercentage
                )
            )
        }
    }

    private suspend fun endSession(context: Context, batteryPercentage: Float?) {
        val repository = AppDataContainer(context).batteryChargingSessionRepository
        withContext(Dispatchers.IO) {
            val session = repository.getLastRecordStream()
            if (session == null || session.endTime != null) {
                // We missed current session's start
                repository.insertRecord(
                    BatteryChargingSession(
                        endTime = OffsetDateTime.now(),
                        finalChargePercentage = batteryPercentage
                    )
                )
            } else {
                repository.updateRecord(
                    session.copy(
                        endTime = OffsetDateTime.now(),
                        finalChargePercentage = batteryPercentage
                    )
                )
            }
        }
    }
}
