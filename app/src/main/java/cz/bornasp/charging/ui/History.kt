package cz.bornasp.charging.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.bornasp.charging.R
import cz.bornasp.charging.model.ChargeRecord
import cz.bornasp.charging.ui.theme.AppIcons
import cz.bornasp.charging.ui.theme.Green
import cz.bornasp.charging.ui.theme.dark_Green
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun History(records: List<ChargeRecord>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(8.dp)) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(records) { record ->
                RecordCard(record)
            }
        }
    }
}

@Composable
fun RecordCard(record: ChargeRecord, modifier: Modifier = Modifier) {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
    val timeCharging = Duration.between(record.start, record.end)

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                FromToText(
                    from = stringResource(R.string.percentage, record.from),
                    to = stringResource(R.string.percentage, record.to),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = record.start.format(formatter),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                PercentageDifferenceText(
                    value = record.to - record.from,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = stringResource(R.string.duration, formatTime(timeCharging.seconds)),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

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

@Composable
fun PercentageDifferenceText(value: Int, style: TextStyle, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.percentage_difference, value),
        style = style,
        color = when {
            value > 0 -> if (isSystemInDarkTheme()) dark_Green else Green
            value < 0 -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.onSurface
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun HistoryPreview() {
    val now = LocalDateTime.now()
    History(records = listOf(
        ChargeRecord(now.minusSeconds(1), now, 90, 90),
        ChargeRecord(now.minusMinutes(195), now.minusMinutes(1), 25, 100),
        ChargeRecord(now.minusMinutes(1483), now.minusMinutes(1440).plusHours(2), 20, 80),
        ChargeRecord(now.minusMinutes(2800), now.minusMinutes(2791), 20, 30),
        ChargeRecord(now.minusMinutes(5500), now.minusMinutes(5000), 75, 73),
        ChargeRecord(now.minusMinutes(6000), now.minusMinutes(5998), 75, 76),
        ChargeRecord(now.minusMinutes(12_000), now.minusMinutes(10_000), 0, 100),
        ChargeRecord(now.minusMinutes(15_000), now.minusMinutes(14_939), 50, 60)
    ))
}

/**
 * Custom time formatting.
 * @param seconds Time in seconds.
 */
@Composable
private fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val hours = minutes / 60

    return when {
        hours != 0L -> stringResource(R.string.timeHoursMinutes, hours, minutes % 60)
        minutes != 0L -> stringResource(R.string.timeMinutes, minutes)
        else -> stringResource(R.string.timeSeconds, seconds)
    }
}
