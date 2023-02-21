package cz.bornasp.charging.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cz.bornasp.charging.ui.chargealarm.ChargeAlarmDestination
import cz.bornasp.charging.ui.chargealarm.ChargeAlarmScreen
import cz.bornasp.charging.ui.history.HistoryDestination
import cz.bornasp.charging.ui.history.HistoryScreen
import cz.bornasp.charging.ui.home.HomeDestination
import cz.bornasp.charging.ui.home.HomeScreen
import cz.bornasp.charging.ui.statistics.StatisticsDestination
import cz.bornasp.charging.ui.statistics.StatisticsScreen

@Composable
fun ChargingNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToChargeAlarm = { navController.navigate(ChargeAlarmDestination.route) },
                navigateToHistory = { navController.navigate(HistoryDestination.route) },
                navigateToStatistics = { navController.navigate(StatisticsDestination.route) }
            )
        }
        composable(route = HistoryDestination.route) {
            HistoryScreen(navigateUp = { navController.navigateUp() })
        }
        composable(route = StatisticsDestination.route) {
            StatisticsScreen(navigateUp = { navController.navigateUp() })
        }
        composable(route = ChargeAlarmDestination.route) {
            ChargeAlarmScreen(
                onCancel = { navController.navigateUp() },
                onSave = { navController.navigate(HomeDestination.route) }
            )
        }
    }
}
