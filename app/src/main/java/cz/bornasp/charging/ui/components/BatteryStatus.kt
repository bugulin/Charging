package cz.bornasp.charging.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.bornasp.charging.R
import cz.bornasp.charging.ui.theme.AppIcons
import cz.bornasp.charging.ui.theme.ChargingTheme

@Composable
fun BatteryStatus(
    percentage: Float,
    power: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.percentage, percentage),
            style = MaterialTheme.typography.displayLarge
        )
        Box(
            modifier = Modifier
                .height(64.dp)
                .padding(16.dp)
        ) {
            if (power) {
                Icon(
                    imageVector = AppIcons.Bolt,
                    contentDescription = stringResource(R.string.charging),
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BatteryStatusPreview() {
    ChargingTheme {
        BatteryStatus(percentage = 20F, power = true)
    }
}
