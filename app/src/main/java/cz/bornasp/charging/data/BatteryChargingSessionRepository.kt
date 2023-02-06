package cz.bornasp.charging.data

import kotlinx.coroutines.flow.Flow

private const val SECONDS_IN_DAY = 24 * 60 * 60f

/**
 * Repository that provides insertion, update, and retrieval of [BatteryChargingSession] in
 * an SQLite database.
 */
class BatteryChargingSessionRepository(private val sessionDao: BatteryChargingSessionDao) {
    /**
     * Retrieve most recent session from the data source.
     */
    fun getLastRecord(): BatteryChargingSession? = sessionDao.getLastSession()

    /**
     * Retrieve most recent session from the data source as a data stream.
     */
    fun getLastRecordStream(): Flow<BatteryChargingSession?> = sessionDao.getLastSessionStream()

    /**
     * Retrieve all the recorded sessions from the data source.
     */
    fun getAllRecordsStream(): Flow<List<BatteryChargingSession>> = sessionDao.getAllSessions()

    /**
     * Retrieve various statistics about stored records.
     */
    suspend fun getChargingStatistics(): ChargingStatistics {
        val avgBatteryTime = sessionDao.getAverageBatteryTimeInDays()
        return ChargingStatistics(
            sessionCount = sessionDao.getSessionCount(),
            completeSessionCount = sessionDao.getCompleteSessionCount(),
            totalChargePercentage = sessionDao.getTotalCharge() ?: 0f,
            averageInitialChargePercentage = sessionDao.getAverageInitialCharge(),
            averageFinalChargePercentage = sessionDao.getAverageFinalCharge(),
            totalChargingTimeInSeconds =
                (sessionDao.getTotalChargingTimeInDays() ?: 0f) * SECONDS_IN_DAY,
            averageBatteryTimeInSeconds =
                if (avgBatteryTime != null) avgBatteryTime * SECONDS_IN_DAY else null
        )
    }

    /**
     * Insert battery charging session in the data source.
     */
    suspend fun insertRecord(record: BatteryChargingSession) = sessionDao.insert(record)

    /**
     * Update battery charging session in the data source.
     */
    suspend fun updateRecord(record: BatteryChargingSession) = sessionDao.update(record)
}
