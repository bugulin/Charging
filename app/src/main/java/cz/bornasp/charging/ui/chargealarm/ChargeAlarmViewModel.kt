package cz.bornasp.charging.ui.chargealarm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bornasp.charging.data.DEFAULT_CHARGE_ALARM_ENABLED
import cz.bornasp.charging.data.DEFAULT_CHARGE_ALARM_TARGET
import cz.bornasp.charging.data.UserPreferencesRepository
import cz.bornasp.charging.helpers.percent
import cz.bornasp.charging.helpers.toPercentage
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * View model for managing a charge alarm.
 */
class ChargeAlarmViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    var uiState by mutableStateOf(ChargeAlarmUiState())
        private set

    init {
        viewModelScope.launch {
            val isEnabled = async { userPreferencesRepository.isChargeAlarmEnabled.first() }
            val target = async { userPreferencesRepository.chargeAlarmTarget.first() }

            uiState = ChargeAlarmUiState(
                isEnabled = isEnabled.await(),
                sliderValue = target.await(),
                targetBatteryPercentage = target.await(),
            )
        }
    }

    fun updateSlider(value: Float) {
        // Save value rounded to 2 decimal places
        uiState = uiState.copy(sliderValue = value.toPercentage().percent)
    }

    fun updatePreferences(enableChargeAlarm: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateChargeAlarm(
                isEnabled = enableChargeAlarm,
                target = if (enableChargeAlarm) uiState.sliderValue else null
            )
        }
    }
}

/**
 * UI state for [ChargeAlarmViewModel].
 */
data class ChargeAlarmUiState(
    val isEnabled: Boolean = DEFAULT_CHARGE_ALARM_ENABLED,
    val sliderValue: Float = DEFAULT_CHARGE_ALARM_TARGET,
    val targetBatteryPercentage: Float = DEFAULT_CHARGE_ALARM_TARGET
)
