package cz.bornasp.charging.ui.chargealarm

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.bornasp.charging.R
import cz.bornasp.charging.helpers.toPercentage
import cz.bornasp.charging.ui.AppViewModelProvider
import cz.bornasp.charging.ui.navigation.NavigationDestination
import cz.bornasp.charging.ui.theme.AppIcons
import cz.bornasp.charging.ui.theme.ChargingTheme

object ChargeAlarmDestination : NavigationDestination {
    override val route = "charge-alarm"
    override val titleRes = R.string.charge_alarm
}

@Composable
fun ChargeAlarmScreen(
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChargeAlarmViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    ChargeAlarmScreenContent(
        onSave = { isEnabled: Boolean ->
            viewModel.updatePreferences(isEnabled)
            onSave()
        },
        onCancel = onCancel,
        onSliderValueChange = { viewModel.updateSlider(it) },
        uiState = viewModel.uiState,
        modifier = modifier.systemBarsPadding()
    )
}

@Composable
fun ChargeAlarmScreenContent(
    onSave: (Boolean) -> Unit,
    onCancel: () -> Unit,
    onSliderValueChange: (Float) -> Unit,
    uiState: ChargeAlarmUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 32.dp, end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = AppIcons.Alarm,
            contentDescription = null,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp),
            tint = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = stringResource(R.string.charge_alarm_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(R.string.charge_alarm_description),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
        Slider(
            value = uiState.sliderValue,
            onValueChange = onSliderValueChange,
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth(.75f)
        )
        Text(
            text = stringResource(
                R.string.charge_alarm_target,
                stringResource(R.string.percentage, uiState.sliderValue.toPercentage())
            ),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )
        Spacer(modifier = Modifier.weight(1f))

        BottomBar(
            onSave = onSave,
            onCancel = onCancel,
            isChargeAlarmEnabled = uiState.isEnabled,
            canSave = !uiState.isEnabled || uiState.targetBatteryPercentage != uiState.sliderValue
        )
    }
}

@Composable
private fun BottomBar(
    onSave: (Boolean) -> Unit,
    onCancel: () -> Unit,
    isChargeAlarmEnabled: Boolean,
    canSave: Boolean
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                onSave(true)
            }
        }
    )
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextButton(onClick = onCancel) {
            Text(text = stringResource(R.string.cancel))
        }
        Spacer(modifier = Modifier.weight(1f))
        if (isChargeAlarmEnabled) {
            OutlinedButton(onClick = { onSave(false) }) {
                Text(text = stringResource(R.string.disable_charge_alarm))
            }
        }
        Button(
            onClick = {
                // Runtime permission for sending notifications (Android API >= 33)
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    onSave(true)
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            },
            enabled = canSave
        ) {
            Text(
                text = stringResource(
                    if (isChargeAlarmEnabled) R.string.update else R.string.enable_charge_alarm
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChargeAlarmPreview() {
    ChargingTheme {
        ChargeAlarmScreenContent(
            onSave = { },
            onCancel = { },
            onSliderValueChange = { },
            uiState = ChargeAlarmUiState(
                isEnabled = true,
                sliderValue = 80f,
                targetBatteryPercentage = 100f
            )
        )
    }
}
