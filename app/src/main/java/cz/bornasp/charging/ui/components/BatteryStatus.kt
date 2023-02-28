package cz.bornasp.charging.ui.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.bornasp.charging.R
import cz.bornasp.charging.ui.theme.AppIcons
import cz.bornasp.charging.ui.theme.ChargingTheme

/** Duration of battery percentage animation. */
private const val ANIMATION_DURATION = 250

@Composable
fun BatteryStatus(
    percentage: Float,
    power: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = ANIMATION_DURATION, easing = LinearOutSlowInEasing)
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.percentage, animatedPercentage),
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
                    tint = MaterialTheme.colorScheme.primary,
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
