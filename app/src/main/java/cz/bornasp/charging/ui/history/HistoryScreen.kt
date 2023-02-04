package cz.bornasp.charging.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
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

private const val LARGE_TOP_APP_BAR_HEIGHT = 152

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
                scrollBehavior = if (historyUiState.sessionList.isEmpty()) null
                    else scrollBehavior
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
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = sessionList.isNotEmpty()
    ) {
        items(items = sessionList, key = { it.id }) { record ->
            BatteryChargingSessionCard(record)
        }
        if (sessionList.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenHeight - LARGE_TOP_APP_BAR_HEIGHT.dp),
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
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryPreview() {
    val now = OffsetDateTime.now()
    val sessionList = listOf(
        BatteryChargingSession(0, now.minusSeconds(1), now, 90F, 90F),
        BatteryChargingSession(1, now.minusMinutes(195), now.minusMinutes(1), 25.6F, 100F),
        BatteryChargingSession(2, now.minusMinutes(1483), now.minusMinutes(1440).plusHours(2), 20F, 80F),
        BatteryChargingSession(3, now.minusMinutes(2800), now.minusMinutes(2791), 20F, 30F),
        BatteryChargingSession(4, now.minusMinutes(5500), now.minusMinutes(5000), 75F, 73F),
        BatteryChargingSession(5, now.minusMinutes(6000), now.minusMinutes(5998), 75F, 76F),
        BatteryChargingSession(6, now.minusMinutes(12_000), now.minusMinutes(10_000), 0F, 100F),
        BatteryChargingSession(7, now.minusMinutes(15_000), now.minusMinutes(14_939), 50F, 60F),
        BatteryChargingSession(8, null, now, null, 60F),
        BatteryChargingSession(9, now, null, 50F, null)
    )

    BatteryChargingSessionList(sessionList)
}

@Preview(showBackground = true)
@Composable
fun EmptyHistoryPreview() {
    BatteryChargingSessionList(sessionList = listOf())
}
