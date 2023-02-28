package cz.bornasp.charging.ui.statistics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.bornasp.charging.data.BatteryChargingSessionRepository
import cz.bornasp.charging.data.ChargingStatistics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * View model for a screen with statistics.
 */
class StatisticsViewModel(
    private val sessionRepository: BatteryChargingSessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChargingStatistics())
    val uiState: StateFlow<ChargingStatistics> = _uiState.asStateFlow()

    var reloading by mutableStateOf(false)
        private set

    init {
        reload()
    }

    fun reload() {
        viewModelScope.launch {
            reloading = true
            _uiState.update {
                sessionRepository.getChargingStatistics()
            }
            reloading = false
        }
    }
}
