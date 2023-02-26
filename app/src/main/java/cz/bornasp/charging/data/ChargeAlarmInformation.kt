package cz.bornasp.charging.data

/**
 * Information about the charge alarm.
 */
data class ChargeAlarmInformation(
    val isEnabled: Boolean,
    val targetCharge: Float,
    val wentOff: Boolean
)
