package cz.bornasp.charging.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.bornasp.charging.R
import cz.bornasp.charging.data.BatteryChargingSession
import cz.bornasp.charging.ui.AppViewModelProvider
import cz.bornasp.charging.ui.components.BatteryChargingSessionCard
import cz.bornasp.charging.ui.navigation.NavigationDestination
import cz.bornasp.charging.ui.theme.AppIcons
import java.time.OffsetDateTime

object HistoryDestination : NavigationDestination {
    override val route = "history"
    override val titleRes = R.string.history
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val historyUiState by viewModel.historyUiState.collectAsState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(HistoryDestination.titleRes),
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
                scrollBehavior = if (historyUiState.sessionList.isEmpty()) {
                    null
                } else {
                    scrollBehavior
                }
            )
        }
    ) { innerPadding ->
        BatteryChargingSessionList(
            sessionList = historyUiState.sessionList,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@Composable
fun BatteryChargingSessionList(
    sessionList: List<BatteryChargingSession>,
    modifier: Modifier = Modifier
) {
    if (sessionList.isEmpty()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = AppIcons.BatteryChargingFull,
                contentDescription = null,
                modifier = Modifier
                    .padding(8.dp)
                    .size(64.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = stringResource(R.string.no_history),
                modifier = Modifier
                    .padding(bottom = 64.dp),
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = sessionList, key = { it.id }) { record ->
                BatteryChargingSessionCard(record)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryPreview() {
    val now = OffsetDateTime.now()
    val data = listOf(
        Pair(now.minusSeconds(1) to now, 90f to 90f),
        Pair(now.minusMinutes(195) to now.minusMinutes(1), 25.6f to 100f),
        Pair(now.minusMinutes(1483) to now.minusMinutes(1320), 20f to 80f),
        Pair(now.minusMinutes(2800) to now.minusMinutes(2791), 20f to 30f),
        Pair(now.minusMinutes(5500) to now.minusMinutes(5000), 75f to 73f),
        Pair(now.minusMinutes(6000) to now.minusMinutes(5998), 75f to 76f),
        Pair(now.minusMinutes(12_000) to now.minusMinutes(10_000), 0f to 100f),
        Pair(now.minusMinutes(15_000) to now.minusMinutes(14_939), 50f to 60f),
        Pair(null to now, null to 60f),
        Pair(now to null, 50f to null)
    )

    BatteryChargingSessionList(
        sessionList = data.mapIndexed { index, item ->
            BatteryChargingSession(
                id = index,
                startTime = item.first.first,
                endTime = item.first.second,
                initialChargePercentage = item.second.first,
                finalChargePercentage = item.second.second
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EmptyHistoryPreview() {
    BatteryChargingSessionList(
        sessionList = listOf(),
        modifier = Modifier.fillMaxSize()
    )
}
