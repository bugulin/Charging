package cz.bornasp.charging.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cz.bornasp.charging.ChargingApplication
import cz.bornasp.charging.ui.AppViewModelProvider.Factory
import cz.bornasp.charging.ui.history.HistoryViewModel

/**
 * Provide [Factory] to create instance of [ViewModel] for the entire application.
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HistoryViewModel(chargingApplication().container.batteryChargingSessionRepository)
        }
    }
}

/**
 * Return an instance of [ChargingApplication].
 */
fun CreationExtras.chargingApplication(): ChargingApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ChargingApplication)
