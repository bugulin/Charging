package cz.bornasp.charging.ui.home

import android.content.Intent
import android.os.BatteryManager
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.whenStarted
import cz.bornasp.charging.R
import cz.bornasp.charging.data.BatteryChargingSession
import cz.bornasp.charging.ui.AppViewModelProvider
import cz.bornasp.charging.ui.components.BatteryStatus
import cz.bornasp.charging.ui.components.SystemBroadcastReceiver
import cz.bornasp.charging.ui.components.formatDuration
import cz.bornasp.charging.ui.navigation.NavigationDestination
import cz.bornasp.charging.ui.theme.AppIcons
import cz.bornasp.charging.ui.theme.ChargingTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime

/** Last event message update interval in seconds. */
private const val UPDATE_INTERVAL = 20_000L

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@Composable
fun HomeScreen(
    navigateToChargeAlarm: () -> Unit,
    navigateToHistory: () -> Unit,
    navigateToStatistics: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val lastCharging by viewModel.lastChargingSession.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val title = when (uiState.batteryStatus) {
        BatteryManager.BATTERY_STATUS_CHARGING -> R.string.charging
        BatteryManager.BATTERY_STATUS_DISCHARGING -> R.string.discharging
        BatteryManager.BATTERY_STATUS_FULL -> R.string.fully_charged
        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> R.string.not_charging
        else -> HomeDestination.titleRes
    }

    SystemBroadcastReceiver(Intent.ACTION_BATTERY_CHANGED, viewModel::update)
    HomeScreenContent(
        title = title,
        message = getLastEventMessage(lastCharging),
        modifier = modifier.systemBarsPadding(),
        uiState = uiState,
        isChargeAlarmEnabled = viewModel.isChargeAlarmEnabled.collectAsState().value,
        navigateToChargeAlarm = navigateToChargeAlarm,
        navigateToHistory = navigateToHistory,
        navigateToStatistics = navigateToStatistics
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    @StringRes title: Int,
    message: String?,
    modifier: Modifier,
    uiState: HomeUiState,
    isChargeAlarmEnabled: Boolean,
    navigateToChargeAlarm: () -> Unit,
    navigateToHistory: () -> Unit,
    navigateToStatistics: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LargeTopAppBar(
            title = {
                Text(
                    text = stringResource(title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            actions = {
                IconButton(onClick = navigateToChargeAlarm) {
                    Icon(
                        imageVector =
                        if (isChargeAlarmEnabled) AppIcons.AlarmOn else AppIcons.AlarmAdd,
                        contentDescription = stringResource(R.string.charge_alarm)
                    )
                }
                IconButton(onClick = navigateToStatistics) {
                    Icon(
                        imageVector = AppIcons.Analytics,
                        contentDescription = stringResource(R.string.statistics)
                    )
                }
                IconButton(onClick = navigateToHistory) {
                    Icon(
                        imageVector = AppIcons.History,
                        contentDescription = stringResource(R.string.history)
                    )
                }
            }
        )
        BatteryStatus(
            percentage = uiState.batteryPercentage,
            power = uiState.isPluggedIn,
            modifier = Modifier
                .padding(16.dp, 8.dp, 16.dp, 64.dp)
        )
        Text(
            text = message ?: "",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ChargingTheme {
        HomeScreenContent(
            navigateToChargeAlarm = { },
            navigateToHistory = { },
            navigateToStatistics = { },
            title = R.string.app_name,
            message = stringResource(R.string.time_from_last_charging, "30 minutes"),
            uiState = HomeUiState(80f, BatteryManager.BATTERY_STATUS_CHARGING, true),
            isChargeAlarmEnabled = true,
            modifier = Modifier
        )
    }
}

/**
 * Get informative message about the last charging event (plugged in/out).
 *
 * This [Composable] returns a string that contains current time since the last event and
 * automatically updates it every [UPDATE_INTERVAL] seconds.
 */
@Composable
private fun getLastEventMessage(lastCharging: BatteryChargingSession?): String? {
    val lifecycleOwner = LocalLifecycleOwner.current
    val now by produceState<OffsetDateTime>(initialValue = OffsetDateTime.now()) {
        withContext(Dispatchers.Default) {
            lifecycleOwner.whenStarted {
                while (true) {
                    value = OffsetDateTime.now()
                    delay(UPDATE_INTERVAL)
                }
            }
        }
    }

    var message: String? = null
    if (lastCharging != null) {
        if (lastCharging.endTime != null) {
            message = stringResource(
                R.string.time_from_last_charging,
                formatDuration(
                    seconds = now.toEpochSecond() - lastCharging.endTime.toEpochSecond(),
                    precise = false
                )
            )
        } else if (lastCharging.startTime != null) {
            message = stringResource(
                R.string.current_charging_time,
                formatDuration(
                    seconds = now.toEpochSecond() - lastCharging.startTime.toEpochSecond(),
                    precise = false
                )
            )
        }
    }

    return message
}
