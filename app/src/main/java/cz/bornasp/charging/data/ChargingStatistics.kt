package cz.bornasp.charging.data

/**
 * Helper data class holding charging statistics from a data source.
 */
data class ChargingStatistics(
    val sessionCount: Int = 0,
    val completeSessionCount: Int = 0,

    val totalChargePercentage: Float = 0f,
    val averageInitialChargePercentage: Float? = null,
    val averageFinalChargePercentage: Float? = null,

    val totalChargingTimeInSeconds: Float = 0f,
    val averageBatteryTimeInSeconds: Float? = null,
)
