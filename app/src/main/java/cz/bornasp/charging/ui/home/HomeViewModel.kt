package cz.bornasp.charging.ui.home

import android.content.Intent
import android.os.BatteryManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bornasp.charging.data.BatteryChargingSession
import cz.bornasp.charging.data.BatteryChargingSessionRepository
import kotlinx.coroutines.flow.*

/**
 * View model with current battery status and all battery charging sessions in the database.
 */
class HomeViewModel(sessionRepository: BatteryChargingSessionRepository) : ViewModel() {
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

    /**
     * Update displayed information about battery status.
     * @param batteryStatus Intent that matches [Intent.ACTION_BATTERY_CHANGED].
     */
    fun update(batteryStatus: Intent?) {
        val chargePlug: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        val percentage: Float? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }
        val status = batteryStatus?.getIntExtra(
            BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN
        ) ?: BatteryManager.BATTERY_STATUS_UNKNOWN
        _uiState.update { currentState ->
            currentState.copy(
                batteryPercentage = percentage ?: -1F,
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
    val batteryPercentage: Float = -1F,
    val batteryStatus: Int = BatteryManager.BATTERY_STATUS_UNKNOWN,
    val isPluggedIn: Boolean = false,
)
