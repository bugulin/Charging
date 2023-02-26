package cz.bornasp.charging.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import cz.bornasp.charging.R

const val ALARM_NOTIFICATION_CHANNEL = "charge-alarm"
const val SERVICE_NOTIFICATION_CHANNEL = "charge-monitor"

const val ALARM_NOTIFICATION_ID = 1
const val SERVICE_NOTIFICATION_ID = 2

/**
 * Ensure that the demanded notification channels exist.
 */
fun createNotificationChannel(context: Context) {
    // Channel for mandatory notification of foreground service
    val serviceChannel = NotificationChannel(
        SERVICE_NOTIFICATION_CHANNEL,
        context.getString(R.string.service_channel_name),
        NotificationManager.IMPORTANCE_LOW
    ).apply {
        description = context.getString(R.string.service_channel_description)
        setShowBadge(false)
    }
    // Channel for notifications from the charge alarm
    val alarmChannel = NotificationChannel(
        ALARM_NOTIFICATION_CHANNEL,
        context.getString(R.string.charge_alarm),
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = context.getString(R.string.alarm_channel_description)
    }

    val notificationManager =
        context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
    with(notificationManager) {
        createNotificationChannel(serviceChannel)
        createNotificationChannel(alarmChannel)
    }
}
