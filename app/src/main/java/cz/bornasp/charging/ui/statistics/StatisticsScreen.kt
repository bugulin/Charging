@file:OptIn(ExperimentalMaterial3Api::class)

package cz.bornasp.charging.ui.statistics

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.bornasp.charging.R
import cz.bornasp.charging.data.ChargingStatistics
import cz.bornasp.charging.ui.AppViewModelProvider
import cz.bornasp.charging.ui.components.Overline
import cz.bornasp.charging.ui.components.formatDuration
import cz.bornasp.charging.ui.navigation.NavigationDestination
import cz.bornasp.charging.ui.theme.AppIcons
import cz.bornasp.charging.ui.theme.ChargingTheme

object StatisticsDestination : NavigationDestination {
    override val route = "statistics"
    override val titleRes = R.string.statistics
}

@Composable
fun StatisticsScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val statistics by viewModel.uiState.collectAsState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(StatisticsDestination.titleRes),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = AppIcons.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = AppIcons.Refresh,
                            contentDescription = stringResource(R.string.refresh)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Statistics(
            statistics,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@Composable
fun Statistics(
    statistics: ChargingStatistics,
    modifier: Modifier = Modifier
) {
    val totalItems = listOf(
        Pair(
            R.string.charging_sessions,
            if (statistics.sessionCount == statistics.completeSessionCount)
                statistics.sessionCount.toString()
            else
                stringResource(
                    R.string.complete_incomplete_session_count,
                    statistics.completeSessionCount,
                    statistics.sessionCount
                )
        ),
        Pair(
            R.string.total_charge_percentage,
            stringResource(
                R.string.percentage,
                statistics.totalChargePercentage
            )
        ),
        Pair(
            R.string.total_charging_time,
            formatDuration(seconds = statistics.totalChargingTimeInSeconds.toLong())
        )
    )
    val averageItems = listOf(
        Pair(
            R.string.initial_charge,
            if (statistics.averageInitialChargePercentage != null)
                stringResource(
                    R.string.percentage,
                    statistics.averageInitialChargePercentage
                )
            else null
        ),
        Pair(
            R.string.final_charge,
            if (statistics.averageFinalChargePercentage != null)
                stringResource(
                    R.string.percentage,
                    statistics.averageFinalChargePercentage
                )
            else null
        ),
        Pair(
            R.string.charging_time,
            formatDuration(seconds = (statistics.totalChargingTimeInSeconds / statistics.completeSessionCount).toLong())
        ),
        Pair(
            R.string.battery_time,
            if (statistics.averageBatteryTimeInSeconds != null)
                formatDuration(seconds = statistics.averageBatteryTimeInSeconds.toLong())
            else null
        )
    )

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(totalItems) {
            Item(it.first, it.second)
        }
        item {
            Overline(text = stringResource(R.string.averages))
        }
        items(averageItems) {
            Item(it.first, it.second)
        }
    }
}

@Composable
private fun Item(
    @StringRes nameRes: Int,
    value: String?
) {
    ListItem(
        headlineText = { Text(text = stringResource(nameRes)) },
        trailingContent = {
            if (value == null) {
                Text(
                    text = stringResource(R.string.unknown),
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Normal
                )
            } else {
                Text(
                    text = value
                )
            }
        }
    )
}

@Preview
@Composable
fun StatisticsPreview() {
    ChargingTheme {
        Statistics(
            statistics = ChargingStatistics(
                sessionCount = 3,
                completeSessionCount = 1,
                totalChargePercentage = 50.9f,
                averageInitialChargePercentage = 20.1f,
                averageFinalChargePercentage = 71.0f,
                totalChargingTimeInSeconds = 3600f,
                averageBatteryTimeInSeconds = 1760f
            )
        )
    }
}
