package cz.bornasp.charging.ui

import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cz.bornasp.charging.R
import cz.bornasp.charging.model.ChargingViewModel
import cz.bornasp.charging.ui.components.SystemBroadcastReceiver
import cz.bornasp.charging.ui.history.HistoryScreen
import cz.bornasp.charging.ui.theme.AppIcons

/**
 * Enumeration values that represent the screens in the application.
 */
enum class ChargingAppScreen(@StringRes val title: Int) {
    BatteryStatus(title = R.string.battery_status),
    History(title = R.string.history),
    Settings(title = R.string.settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargingAppBar(
    currentScreen: ChargingAppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    actions: @Composable() (RowScope.() -> Unit),
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(currentScreen.title),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = AppIcons.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        },
        actions = actions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargingApp(
    modifier: Modifier = Modifier,
    viewModel: ChargingViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = ChargingAppScreen.valueOf(
        backStackEntry?.destination?.route ?: ChargingAppScreen.BatteryStatus.name
    )

    Scaffold(
        topBar = {
            ChargingAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                actions = {
                    if (currentScreen == ChargingAppScreen.BatteryStatus) {
                        IconButton(
                            onClick = { navController.navigate(ChargingAppScreen.History.name) }
                        ) {
                            Icon(
                                imageVector = AppIcons.History,
                                contentDescription = stringResource(R.string.history)
                            )
                        }
                        IconButton(
                            onClick = { navController.navigate(ChargingAppScreen.Settings.name) }
                        ) {
                            Icon(
                                imageVector = AppIcons.Settings,
                                contentDescription = stringResource(R.string.settings)
                            )
                        }
                    }
                }
            )
        },
        content = { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = ChargingAppScreen.BatteryStatus.name,
                modifier = modifier.padding(innerPadding)
            ) {
                composable(route = ChargingAppScreen.BatteryStatus.name) {
                    val uiState by viewModel.uiState.collectAsState()
                    SystemBroadcastReceiver(Intent.ACTION_BATTERY_CHANGED, viewModel::update)
                    BatteryStatus(
                        percentage = uiState.batteryPercentage,
                        isPluggedIn = uiState.isPluggedIn
                    )
                }
                composable(route = ChargingAppScreen.History.name) {
                    HistoryScreen()
                }
                composable(route = ChargingAppScreen.Settings.name) {

                }
            }
        }
    )
}
