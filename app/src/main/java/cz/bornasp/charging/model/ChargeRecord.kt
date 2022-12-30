package cz.bornasp.charging.model

import java.time.LocalDateTime

/**
 * Record of one charging session.
 * @param start Time when the session started.
 * @param end Time when the session ended.
 * @param from Initial battery level percentage.
 * @param to Final battery level percentage.
 */
data class ChargeRecord(
    val start: LocalDateTime,
    val end: LocalDateTime,
    val from: Int,
    val to: Int
)
