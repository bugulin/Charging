package cz.bornasp.charging.ui.home

import android.content.Intent
import android.os.BatteryManager
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bornasp.charging.data.BatteryChargingSession
import cz.bornasp.charging.data.BatteryChargingSessionRepository
import kotlinx.coroutines.flow.*

/**
 * View model with current battery status and all battery charging sessions in the database.
 */
class HomeViewModel(sessionRepository: BatteryChargingSessionRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState(-1F, false))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val listState = MutableStateFlow(LazyListState())

    val historyUiState: StateFlow<HistoryUiState> = sessionRepository.getAllRecordsStream()
        .map { HistoryUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HistoryUiState()
        )

    /**
     * Update displayed information about battery status.
     * @param batteryStatus Intent that matches [Intent.ACTION_BATTERY_CHANGED].
     */
    fun update(batteryStatus: Intent?) {
        val chargePlug: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        val batteryPercentage: Float? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }
        _uiState.value = HomeUiState(
            batteryPercentage = batteryPercentage ?: -1F,
            isPluggedIn = chargePlug != 0
        )
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * UI state for history of battery charging.
 */
data class HistoryUiState(
    val sessionList: List<BatteryChargingSession> = listOf()
)

/**
 * UI state for home screen.
 */
data class HomeUiState(
    val batteryPercentage: Float,
    val isPluggedIn: Boolean
)
