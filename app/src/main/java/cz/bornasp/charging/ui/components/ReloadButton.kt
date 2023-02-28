package cz.bornasp.charging.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import cz.bornasp.charging.R
import cz.bornasp.charging.ui.theme.AppIcons
import kotlinx.coroutines.launch

/** Initial rotation in degrees. */
private const val INITIAL_ROTATION = 0f

/** Full rotation in degrees. */
private const val FULL_ROTATION = 360f

/** Maximum number of rotations during reload. */
private const val ROTATION_COUNT = 10

/** Duration of a single rotation in milliseconds. */
private const val ROTATION_DURATION = 1000

@Composable
fun ReloadButton(
    reloading: Boolean,
    onClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val currentReloading by rememberUpdatedState(reloading)
    val reloadButtonRotation = remember { Animatable(INITIAL_ROTATION) }

    IconButton(
        onClick = {
            onClick()

            // Animate reloading
            coroutineScope.launch {
                reloadButtonRotation.animateTo(
                    targetValue = FULL_ROTATION * ROTATION_COUNT,
                    animationSpec = tween(
                        durationMillis = ROTATION_DURATION * ROTATION_COUNT,
                        easing = LinearEasing
                    ),
                    block = {
                        val animatable = this
                        // Do at least one full rotation, so that the animation is noticeable
                        if (!currentReloading && animatable.value >= FULL_ROTATION) {
                            coroutineScope.launch {
                                animatable.stop()
                                animatable.snapTo(INITIAL_ROTATION)
                            }
                        }
                    }
                )
            }
        },
        enabled = !(reloadButtonRotation.isRunning || currentReloading)
    ) {
        Icon(
            imageVector = AppIcons.Refresh,
            contentDescription = stringResource(R.string.refresh),
            modifier = Modifier
                .rotate(reloadButtonRotation.value)
        )
    }
}
