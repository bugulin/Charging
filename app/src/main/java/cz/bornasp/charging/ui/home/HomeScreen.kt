package cz.bornasp.charging.ui.home

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.bornasp.charging.ui.AppViewModelProvider
import cz.bornasp.charging.ui.components.BatteryChargingSessionCard
import cz.bornasp.charging.ui.components.BatteryStatus
import cz.bornasp.charging.ui.components.SystemBroadcastReceiver

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val historyState by viewModel.historyUiState.collectAsState()
    val listState by viewModel.listState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(
            start = 8.dp,
            top = 0.dp,
            end = 8.dp,
            bottom = 24.dp
        ),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        item(key = -1) {
            SystemBroadcastReceiver(Intent.ACTION_BATTERY_CHANGED, viewModel::update)
            BatteryStatus(
                percentage = uiState.batteryPercentage,
                power = uiState.isPluggedIn,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(items = historyState.sessionList, key = { it.id }) { session ->
            BatteryChargingSessionCard(session)
        }
    }
}
