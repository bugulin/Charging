package cz.bornasp.charging.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cz.bornasp.charging.ChargingApplication
import cz.bornasp.charging.ui.AppViewModelProvider.Factory
import cz.bornasp.charging.ui.chargealarm.ChargeAlarmViewModel
import cz.bornasp.charging.ui.history.HistoryViewModel
import cz.bornasp.charging.ui.home.HomeViewModel
import cz.bornasp.charging.ui.statistics.StatisticsViewModel

/**
 * Provide [Factory] to create instance of [ViewModel] for the entire application.
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(chargingApplication().container.batteryChargingSessionRepository)
        }
        initializer {
            HistoryViewModel(chargingApplication().container.batteryChargingSessionRepository)
        }
        initializer {
            StatisticsViewModel(chargingApplication().container.batteryChargingSessionRepository)
        }
        initializer {
            ChargeAlarmViewModel(chargingApplication().container.userPreferencesRepository)
        }
    }
}

/**
 * Return an instance of [ChargingApplication].
 */
fun CreationExtras.chargingApplication(): ChargingApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ChargingApplication)
