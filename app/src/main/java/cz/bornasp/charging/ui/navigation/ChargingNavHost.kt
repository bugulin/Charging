package cz.bornasp.charging.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cz.bornasp.charging.ui.history.HistoryDestination
import cz.bornasp.charging.ui.history.HistoryScreen
import cz.bornasp.charging.ui.home.HomeDestination
import cz.bornasp.charging.ui.home.HomeScreen

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
                navigateToChargeAlarm = { },
                navigateToHistory = { navController.navigate(HistoryDestination.route) }
            )
        }
        composable(route = HistoryDestination.route) {
            HistoryScreen(navigateUp = { navController.navigateUp() })
        }
    }
}
