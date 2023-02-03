package cz.bornasp.charging

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cz.bornasp.charging.service.ChargeMonitor
import cz.bornasp.charging.ui.navigation.ChargingNavHost
import cz.bornasp.charging.ui.theme.ChargingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start charge monitoring service
        Intent(this, ChargeMonitor::class.java).also { intent ->
            applicationContext.startForegroundService(intent)
        }

        setContent {
            ChargingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChargingApp()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ChargingTheme {
        ChargingApp()
    }
}

@Composable
fun ChargingApp(navController: NavHostController = rememberNavController()) {
   ChargingNavHost(navController = navController)
}
