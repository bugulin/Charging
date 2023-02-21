package cz.bornasp.charging.helpers

import kotlin.math.round

private const val PERCENT = 100f

/**
 * Corresponding decimal number for a number per cent.
 */
val Float.percent: Float
    get() = this / PERCENT

/**
 * Convert a decimal number to a whole number per cent.
 */
fun Float.toPercentage(): Float = round(this * PERCENT)
