package cz.bornasp.charging.model

import android.content.Intent
import android.os.BatteryManager
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChargingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ChargingUiState(0, false))
    val uiState: StateFlow<ChargingUiState> = _uiState.asStateFlow()

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
        _uiState.value = ChargingUiState(
            batteryPercentage = batteryPercentage?.toInt() ?: -1,
            isPluggedIn = chargePlug != 0
        )
    }
}

data class ChargingUiState(
    val batteryPercentage: Int,
    val isPluggedIn: Boolean
)
