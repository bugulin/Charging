package cz.bornasp.charging.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import cz.bornasp.charging.R
import cz.bornasp.charging.ui.theme.Green
import cz.bornasp.charging.ui.theme.dark_Green

/**
 * Format number as a colored signed percentage.
 * @param value Percentage to display.
 */
@Composable
fun PercentageDifferenceText(
    value: Float, style: TextStyle, modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.percentage_difference, value), style = style, color = when {
            value > 0 -> if (isSystemInDarkTheme()) dark_Green else Green
            value < 0 -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.onSurface
        }, modifier = modifier
    )
}

/**
 * Custom time formatting.
 * @param seconds Duration in seconds.
 */
@Composable
fun formatDuration(seconds: Long): String {
    val minutes = seconds / 60
    val hours = minutes / 60

    return when {
        hours != 0L -> stringResource(R.string.timeHoursMinutes, hours, minutes % 60)
        minutes != 0L -> stringResource(R.string.timeMinutes, minutes)
        else -> stringResource(R.string.timeSeconds, seconds)
    }
}
