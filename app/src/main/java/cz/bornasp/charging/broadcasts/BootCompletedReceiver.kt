package cz.bornasp.charging.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cz.bornasp.charging.service.ChargeMonitor

/**
 * Broadcast receiver that initializes the application after the device completes boot.
 */
class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Intent(context, ChargeMonitor::class.java).also {
                context.startForegroundService(it)
            }
        }
    }
}
