package cz.bornasp.charging.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BatteryChargingSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: BatteryChargingSession)

    @Update
    suspend fun update(record: BatteryChargingSession)

    @Query("SELECT * FROM battery_charging_sessions ORDER BY id DESC LIMIT 1")
    fun getLastSession(): BatteryChargingSession?

    @Query("SELECT * FROM battery_charging_sessions ORDER BY id DESC LIMIT 1")
    fun getLastSessionStream(): Flow<BatteryChargingSession?>

    @Query("SELECT * FROM battery_charging_sessions ORDER BY id DESC")
    fun getAllSessions(): Flow<List<BatteryChargingSession>>
}
