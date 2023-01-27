package cz.bornasp.charging.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@androidx.room.Database(
    entities = [BatteryChargingSession::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(OffsetDateTimeConverter::class)
abstract class Database : RoomDatabase() {
    abstract fun batteryChargingSessionDao(): BatteryChargingSessionDao

    companion object {
        @Volatile
        private var Instance: Database? = null

        fun getDatabase(context: Context): Database {
            return Instance ?: synchronized(this) {
                Instance ?: Room.databaseBuilder(context, Database::class.java, "charging_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

/**
 * Type converter that enables saving [OffsetDateTime] as SQLite `TEXT`.
 */
object OffsetDateTimeConverter {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let {
            formatter.parse(value, OffsetDateTime::from)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(date: OffsetDateTime?): String? {
        return date?.format(formatter)
    }
}
