package cz.bornasp.charging.data

import kotlinx.coroutines.flow.Flow

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
     * Insert battery charging session in the data source.
     */
    suspend fun insertRecord(record: BatteryChargingSession) = sessionDao.insert(record)

    /**
     * Update battery charging session in the data source.
     */
    suspend fun updateRecord(record: BatteryChargingSession) = sessionDao.update(record)
}
