package cz.bornasp.charging.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.BatteryManager
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import cz.bornasp.charging.R
import cz.bornasp.charging.broadcasts.ChargeAlarm
import cz.bornasp.charging.data.AppDataContainer
import cz.bornasp.charging.data.BatteryChargingSession
import cz.bornasp.charging.helpers.ALARM_NOTIFICATION_ID
import cz.bornasp.charging.helpers.SERVICE_NOTIFICATION_CHANNEL
import cz.bornasp.charging.helpers.SERVICE_NOTIFICATION_ID
import cz.bornasp.charging.helpers.createNotificationChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime

const val TO_PERCENTAGE = 100
private const val BATTERY_UNPLUGGED = 0
private const val TAG = "ChargeMonitor"

class ChargeMonitor : Service() {
    private lateinit var powerBroadcastReceiver: PowerBroadcastReceiver

    override fun onBind(intent: Intent?): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Promote service to the foreground
        val pendingIntent: PendingIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                setDataAndNormalize(Uri.parse("package:$packageName"))
            }
            .let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            }
        val notification: Notification = Notification.Builder(this, SERVICE_NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.charge_monitor_notification_title))
            .setContentText(getString(R.string.charge_monitor_notification_text))
            .setContentIntent(pendingIntent)
            .build()
        startForeground(SERVICE_NOTIFICATION_ID, notification)

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")

        createNotificationChannel(this)
        registerBroadcastReceiver()
    }

    override fun onDestroy() {
        unregisterReceiver(powerBroadcastReceiver)
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
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
        powerBroadcastReceiver.onReceive(this, null)
    }
}

private class PowerBroadcastReceiver : BroadcastReceiver() {
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob())

    override fun onReceive(context: Context, intent: Intent?) {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        val batteryPercentage: Float? = batteryStatus?.let {
            val level: Int = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * TO_PERCENTAGE / scale.toFloat()
        }

        coroutineScope.launch {
            when (intent?.action) {
                Intent.ACTION_POWER_CONNECTED -> {
                    createSession(context, batteryPercentage)
                    registerChargeAlarm(context)
                }
                Intent.ACTION_POWER_DISCONNECTED -> {
                    endSession(context, batteryPercentage)
                    // Cancel the charge alarm if triggered
                    with(NotificationManagerCompat.from(context)) {
                        cancel(ALARM_NOTIFICATION_ID)
                    }
                    // Receiving broadcasts for the charge alarm will only drain battery
                    unregisterChargeAlarm(context)
                }
                else -> {
                    // Try to register the charge alarm in a situation when the intent
                    // ACTION_POWER_CONNECTED was missed (i.e. after reboot)
                    val chargePlug: Int = batteryStatus?.getIntExtra(
                        BatteryManager.EXTRA_PLUGGED,
                        BATTERY_UNPLUGGED
                    ) ?: BATTERY_UNPLUGGED
                    Log.d(TAG, "chargePlug = $chargePlug")
                    if (chargePlug != BATTERY_UNPLUGGED) {
                        registerChargeAlarm(context)
                    }
                }
            }
        }
        Log.d(TAG, "Action: ${intent?.action} ($batteryPercentage%)")
    }

    /**
     * Register [ChargeAlarm] as a broadcast receiver.
     */
    private fun registerChargeAlarm(context: Context) {
        IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(chargeAlarm, ifilter)
        }
    }

    /**
     * Unregister [ChargeAlarm] as a broadcast receiver.
     *
     * The charge alarm doesn't have to be registered, this method catches possible exception.
     */
    private fun unregisterChargeAlarm(context: Context) {
        try {
            context.unregisterReceiver(chargeAlarm)
        } catch (e: IllegalArgumentException) {
            Log.d(TAG, "Could not unregister receiver: $e")
        }
    }

    private suspend fun createSession(context: Context, batteryPercentage: Float?) =
        withContext(Dispatchers.IO) {
            val repository = AppDataContainer(context).batteryChargingSessionRepository
            repository.insertRecord(
                BatteryChargingSession(
                    startTime = OffsetDateTime.now(),
                    initialChargePercentage = batteryPercentage
                )
            )
        }

    private suspend fun endSession(context: Context, batteryPercentage: Float?) =
        withContext(Dispatchers.IO) {
            val repository = AppDataContainer(context).batteryChargingSessionRepository
            val session = repository.getLastRecordStream().first()
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

    companion object {
        val chargeAlarm = ChargeAlarm()
    }
}
