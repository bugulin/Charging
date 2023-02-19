package cz.bornasp.charging.broadcasts

import android.Manifest
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import cz.bornasp.charging.R
import cz.bornasp.charging.data.AppDataContainer
import cz.bornasp.charging.helpers.ALARM_NOTIFICATION_CHANNEL
import cz.bornasp.charging.helpers.ALARM_NOTIFICATION_ID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private const val TAG = "ChargeAlarm"

class ChargeAlarm : BroadcastReceiver() {

    override fun onReceive(context: Context, batteryStatus: Intent) {
        val batteryCharge: Float = batteryStatus.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level / scale.toFloat()
        }

        runBlocking {
            checkBatteryCharge(context, batteryCharge)
        }
    }

    /**
     * Check battery charge and accordingly activate the alarm.
     */
    private suspend fun checkBatteryCharge(context: Context, batteryCharge: Float) {
        val container = AppDataContainer(context)
        val information = container.userPreferencesRepository.chargeAlarmInformation.first()
        Log.d(TAG, "Charge: $batteryCharge / ${information.targetCharge}")

        if (batteryCharge >= information.targetCharge) {
            if (!information.wentOff) {
                // Set the alarm off
                container.userPreferencesRepository.setChargeAlarmWentOff(true)
                notify(context)
            }
        } else if (information.wentOff) {
            // Reset the alarm
            container.userPreferencesRepository.setChargeAlarmWentOff(false)
        }
    }

    /**
     * Send a notification to the user.
     * @return Whether the user was notified.
     */
    private fun notify(context: Context): Boolean {
        val notification = Notification.Builder(context, ALARM_NOTIFICATION_CHANNEL)
            .setContentTitle(context.getString(R.string.battery_charged))
            .setContentText(context.getString(R.string.battery_charged_description))
            .setSmallIcon(R.drawable.notification_charge_alarm)
            .setCategory(Notification.CATEGORY_ALARM)
            .build()

        if (
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "POST_NOTIFICATIONS permission not granted.")
            return false
        }

        NotificationManagerCompat.from(context).notify(ALARM_NOTIFICATION_ID, notification)
        return true
    }
}
