package cz.bornasp.charging.ui.history

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.bornasp.charging.R
import cz.bornasp.charging.data.BatteryChargingSession
import cz.bornasp.charging.ui.AppViewModelProvider
import cz.bornasp.charging.ui.theme.AppIcons
import cz.bornasp.charging.ui.theme.Green
import cz.bornasp.charging.ui.theme.dark_Green
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val historyUiState by viewModel.historyUiState.collectAsState()
    Column(modifier = modifier.padding(8.dp)) {
        BatteryChargingSessionList(sessionList = historyUiState.sessionList)
    }
}

@Composable
fun BatteryChargingSessionList(
    sessionList: List<BatteryChargingSession>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = sessionList, key = { it.id }) { record ->
            RecordCard(record)
        }
    }

}

@Composable
fun RecordCard(record: BatteryChargingSession, modifier: Modifier = Modifier) {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)

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
                    from = if (record.initialChargePercentage != null)
                        stringResource(R.string.percentage, record.initialChargePercentage)
                    else "?",
                    to = if (record.finalChargePercentage != null)
                        stringResource(R.string.percentage, record.finalChargePercentage)
                    else "?",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = record.startTime?.format(formatter) ?: (record.endTime?.format(formatter)
                        ?: "?"),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (record.initialChargePercentage != null && record.finalChargePercentage != null) {
                    PercentageDifferenceText(
                        value = record.finalChargePercentage - record.initialChargePercentage,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                if (record.startTime != null && record.endTime != null) {
                    val timeCharging = Duration.between(record.startTime, record.endTime)
                    Text(
                        text = stringResource(R.string.duration, formatTime(timeCharging.seconds)),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
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
fun PercentageDifferenceText(value: Float, style: TextStyle, modifier: Modifier = Modifier) {
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
    val now = OffsetDateTime.now()
    BatteryChargingSessionList(
        sessionList = listOf(
            BatteryChargingSession(0, now.minusSeconds(1), now, 90F, 90F),
            BatteryChargingSession(1, now.minusMinutes(195), now.minusMinutes(1), 25.6F, 100F),
            BatteryChargingSession(2, now.minusMinutes(1483), now.minusMinutes(1440).plusHours(2), 20F, 80F),
            BatteryChargingSession(3, now.minusMinutes(2800), now.minusMinutes(2791), 20F, 30F),
            BatteryChargingSession(4, now.minusMinutes(5500), now.minusMinutes(5000), 75F, 73F),
            BatteryChargingSession(5, now.minusMinutes(6000), now.minusMinutes(5998), 75F, 76F),
            BatteryChargingSession(6, now.minusMinutes(12_000), now.minusMinutes(10_000), 0F, 100F),
            BatteryChargingSession(7, now.minusMinutes(15_000), now.minusMinutes(14_939), 50F, 60F),
            BatteryChargingSession(8, null, now, null, 60F),
            BatteryChargingSession(9, now, null, 50F, null)
        )
    )
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
