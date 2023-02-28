package cz.bornasp.charging.ui.home

import android.content.Intent
import android.os.BatteryManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bornasp.charging.data.BatteryChargingSession
import cz.bornasp.charging.data.BatteryChargingSessionRepository
import cz.bornasp.charging.data.UserPreferencesRepository
import cz.bornasp.charging.service.TO_PERCENTAGE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/** What battery percentage to display when actual value is not available. */
private const val DEFAULT_BATTERY_PERCENTAGE = 0f

/**
 * View model with current battery status and all battery charging sessions in the database.
 */
class HomeViewModel(
    sessionRepository: BatteryChargingSessionRepository,
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val lastChargingSession: StateFlow<BatteryChargingSession?> =
        sessionRepository.getLastRecordStream()
            .filterNotNull()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = null
            )

    val isChargeAlarmEnabled: StateFlow<Boolean> = userPreferencesRepository.isChargeAlarmEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = true
        )

    /**
     * Update displayed information about battery status.
     * @param batteryStatus Intent that matches [Intent.ACTION_BATTERY_CHANGED].
     */
    fun update(batteryStatus: Intent?) {
        val chargePlug: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0
        val percentage: Float? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level == -1 || scale == -1) null else level * TO_PERCENTAGE / scale.toFloat()
        }
        val status = batteryStatus?.getIntExtra(
            BatteryManager.EXTRA_STATUS,
            BatteryManager.BATTERY_STATUS_UNKNOWN
        ) ?: BatteryManager.BATTERY_STATUS_UNKNOWN
        _uiState.update { currentState ->
            currentState.copy(
                batteryPercentage = percentage ?: DEFAULT_BATTERY_PERCENTAGE,
                batteryStatus = status,
                isPluggedIn = chargePlug != 0
            )
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * UI state for home screen.
 */
data class HomeUiState(
    val batteryPercentage: Float = DEFAULT_BATTERY_PERCENTAGE,
    val batteryStatus: Int = BatteryManager.BATTERY_STATUS_UNKNOWN,
    val isPluggedIn: Boolean = false,
)
