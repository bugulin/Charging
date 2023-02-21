package cz.bornasp.charging.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val TAG = "UserPreferencesRepo"

const val DEFAULT_CHARGE_ALARM_TARGET = 1f
const val DEFAULT_CHARGE_ALARM_ENABLED = false
const val DEFAULT_CHARGE_ALARM_WENT_OFF = true

/**
 * Repository that stores user preferences.
 */
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    val chargeAlarmTarget: Flow<Float> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[CHARGE_ALARM_TARGET] ?: DEFAULT_CHARGE_ALARM_TARGET
        }

    val isChargeAlarmEnabled: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[CHARGE_ALARM_ENABLED] ?: DEFAULT_CHARGE_ALARM_ENABLED
        }

    val chargeAlarmInformation: Flow<ChargeAlarmInformation> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            ChargeAlarmInformation(
                isEnabled = preferences[CHARGE_ALARM_ENABLED] ?: DEFAULT_CHARGE_ALARM_ENABLED,
                targetCharge = preferences[CHARGE_ALARM_TARGET] ?: DEFAULT_CHARGE_ALARM_TARGET,
                wentOff = preferences[CHARGE_ALARM_WENT_OFF] ?: DEFAULT_CHARGE_ALARM_WENT_OFF
            )
        }

    suspend fun updateChargeAlarm(isEnabled: Boolean, target: Float?) {
        dataStore.edit { preferences ->
            preferences[CHARGE_ALARM_ENABLED] = isEnabled
            if (target != null) preferences[CHARGE_ALARM_TARGET] = target
        }
    }

    suspend fun setChargeAlarmWentOff(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[CHARGE_ALARM_WENT_OFF] = value
        }
    }

    private companion object {
        val CHARGE_ALARM_ENABLED = booleanPreferencesKey("charge_alarm_enabled")
        val CHARGE_ALARM_TARGET = floatPreferencesKey("charge_alarm_target")
        val CHARGE_ALARM_WENT_OFF = booleanPreferencesKey("charge_alarm_rang")
    }
}
