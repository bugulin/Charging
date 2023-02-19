package cz.bornasp.charging.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

    @Query("SELECT COUNT(id) FROM battery_charging_sessions")
    suspend fun getSessionCount(): Int

    @Query(
        """SELECT COUNT(id) FROM battery_charging_sessions
        WHERE start_time IS NOT NULL AND end_time IS NOT NULL"""
    )
    suspend fun getCompleteSessionCount(): Int

    @Query(
        """SELECT SUM(final_charge - initial_charge) FROM battery_charging_sessions
        WHERE final_charge > initial_charge"""
    )
    suspend fun getTotalCharge(): Float?

    @Query("SELECT AVG(initial_charge) FROM battery_charging_sessions")
    suspend fun getAverageInitialCharge(): Float?

    @Query("SELECT AVG(final_charge) FROM battery_charging_sessions")
    suspend fun getAverageFinalCharge(): Float?

    @Query("SELECT SUM(julianday(end_time) - julianday(start_time)) FROM battery_charging_sessions")
    suspend fun getTotalChargingTimeInDays(): Float?

    @Query(
        """SELECT AVG(julianday(S2.start_time) - julianday(S1.end_time))
        FROM battery_charging_sessions AS S2
        INNER JOIN battery_charging_sessions AS S1 ON S2.id = S1.id + 1"""
    )
    suspend fun getAverageBatteryTimeInDays(): Float?
}
