package cz.bornasp.charging.ui.components

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import cz.bornasp.charging.R
import cz.bornasp.charging.ui.theme.AppIcons

private const val HOURS_IN_DAY = 24
private const val MINUTES_IN_HOUR = 60
private const val SECONDS_IN_MINUTE = 60
private const val TEXT_ICON_SCALE = 0.9

/**
 * Display transition as a text with an arrow.
 * @param from Text on the left.
 * @param to Text on the right.
 */
@Composable
fun FromToText(from: String, to: String, style: TextStyle, modifier: Modifier = Modifier) {
    val arrowId = "arrow"
    val arrowSize = style.fontSize * TEXT_ICON_SCALE
    val text = buildAnnotatedString {
        append(from)
        append(" ")
        appendInlineContent(arrowId, stringResource(R.string.arrow_forward_description))
        append(" ")
        append(to)
    }
    val inlineContent = mapOf(
        arrowId to InlineTextContent(
            placeholder = Placeholder(
                width = arrowSize,
                height = arrowSize,
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
            ),
            children = {
                Icon(AppIcons.ArrowForward, stringResource(R.string.arrow_forward_description))
            }
        )
    )

    Text(text = text, inlineContent = inlineContent, style = style, modifier = modifier)
}

/**
 * Format number as a colored signed percentage.
 * @param value Percentage to display.
 */
@Composable
fun PercentageDifferenceText(value: Float, style: TextStyle, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.percentage_signed, value),
        modifier = modifier,
        color = when {
            value > 0 -> MaterialTheme.colorScheme.primary
            value < 0 -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        style = style
    )
}

/**
 * Format time in [seconds] as a duration.
 * @param precise Whether to show precise number of seconds.
 */
@Composable
fun formatDuration(seconds: Long, precise: Boolean = true): String {
    val minutes = seconds / SECONDS_IN_MINUTE
    val hours = minutes / MINUTES_IN_HOUR
    val days = hours / HOURS_IN_DAY

    return when {
        days != 0L -> stringResource(R.string.time_days_hours, days, hours % HOURS_IN_DAY)
        hours != 0L -> stringResource(R.string.time_hours_minutes, hours, minutes % MINUTES_IN_HOUR)
        minutes != 0L -> stringResource(R.string.time_minutes, minutes)
        else -> if (precise) {
            stringResource(R.string.time_seconds, seconds)
        } else {
            stringResource(R.string.time_less_than_a_minute)
        }
    }
}
