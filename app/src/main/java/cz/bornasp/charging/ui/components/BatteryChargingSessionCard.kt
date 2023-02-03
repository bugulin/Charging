package cz.bornasp.charging.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.bornasp.charging.R
import cz.bornasp.charging.data.BatteryChargingSession
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun BatteryChargingSessionCard(record: BatteryChargingSession, modifier: Modifier = Modifier) {
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
                        text = stringResource(
                            R.string.duration,
                            formatDuration(timeCharging.seconds)
                        ),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }
    }
}
