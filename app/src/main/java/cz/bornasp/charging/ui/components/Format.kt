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
        appendInlineContent(arrowId, stringResource(R.string.to))
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
                Icon(AppIcons.ArrowForward, stringResource(R.string.to))
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
        text = stringResource(R.string.percentage_difference, value),
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
 * Custom time formatting.
 * @param seconds Duration in seconds.
 */
@Composable
fun formatDuration(seconds: Long): String {
    val minutes = seconds / SECONDS_IN_MINUTE
    val hours = minutes / MINUTES_IN_HOUR

    return when {
        hours != 0L -> stringResource(R.string.timeHoursMinutes, hours, minutes % MINUTES_IN_HOUR)
        minutes != 0L -> stringResource(R.string.timeMinutes, minutes)
        else -> stringResource(R.string.timeSeconds, seconds)
    }
}
