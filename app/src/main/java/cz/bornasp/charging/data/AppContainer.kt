package cz.bornasp.charging.data

import android.content.Context

/**
 * App container for dependency injection.
 */
interface AppContainer {
    val batteryChargingSessionRepository: BatteryChargingSessionRepository
}

/**
 * [AppContainer] implementation that provides instance of [BatteryChargingSessionRepository].
 */
class AppDataContainer(private val context: Context) : AppContainer {
    override val batteryChargingSessionRepository: BatteryChargingSessionRepository by lazy {
        BatteryChargingSessionRepository(Database.getDatabase(context).batteryChargingSessionDao())
    }
}
