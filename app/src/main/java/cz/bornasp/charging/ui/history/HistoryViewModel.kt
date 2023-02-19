package cz.bornasp.charging.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bornasp.charging.data.BatteryChargingSession
import cz.bornasp.charging.data.BatteryChargingSessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * View model with all battery charging sessions in the database.
 */
class HistoryViewModel(sessionRepository: BatteryChargingSessionRepository) : ViewModel() {
    val historyUiState: StateFlow<HistoryUiState> = sessionRepository.getAllRecordsStream()
        .map { HistoryUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HistoryUiState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * UI state for history of battery charging.
 */
data class HistoryUiState(val sessionList: List<BatteryChargingSession> = listOf())
