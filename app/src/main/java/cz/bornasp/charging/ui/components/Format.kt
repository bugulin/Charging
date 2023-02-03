package cz.bornasp.charging.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
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
import cz.bornasp.charging.ui.theme.Green
import cz.bornasp.charging.ui.theme.dark_Green

/**
 * Display transition as a text with an arrow.
 * @param from Text on the left.
 * @param to Text on the right.
 */
@Composable
fun FromToText(from: String, to: String, style: TextStyle, modifier: Modifier = Modifier) {
    val arrowId = "arrow"
    val arrowSize = style.fontSize * 0.9
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
