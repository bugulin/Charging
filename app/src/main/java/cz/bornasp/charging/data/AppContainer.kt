package cz.bornasp.charging.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val USER_PREFERENCES_NAME = "user_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

/**
 * App container for dependency injection.
 */
interface AppContainer {
    val batteryChargingSessionRepository: BatteryChargingSessionRepository
    val userPreferencesRepository: UserPreferencesRepository
}

/**
 * [AppContainer] implementation that provides instance of [BatteryChargingSessionRepository].
 */
class AppDataContainer(private val context: Context) : AppContainer {
    override val batteryChargingSessionRepository: BatteryChargingSessionRepository by lazy {
        BatteryChargingSessionRepository(Database.getDatabase(context).batteryChargingSessionDao())
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }
}
