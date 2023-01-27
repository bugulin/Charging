package cz.bornasp.charging.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

/**
 * Entity data class that represents a single row in the database, with information about one
 * battery charging session.
 */
@Entity(tableName = "battery_charging_sessions")
data class BatteryChargingSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "start_time")
    val startTime: OffsetDateTime? = null,
    @ColumnInfo(name = "end_time")
    val endTime: OffsetDateTime? = null,
    @ColumnInfo(name = "initial_charge")
    val initialChargePercentage: Float? = null,
    @ColumnInfo(name = "final_charge")
    val finalChargePercentage: Float? = null
)
