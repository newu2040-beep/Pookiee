package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.ChargingAction
import com.example.data.model.ChargingHistory

@Database(entities = [ChargingAction::class, ChargingHistory::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PookieeDatabase : RoomDatabase() {
    abstract fun dao(): PookieeDao
}

// Separate file or same file for Converters
class Converters {
    @androidx.room.TypeConverter
    fun fromEventType(value: com.example.data.model.ChargingEventType): String {
        return value.name
    }

    @androidx.room.TypeConverter
    fun toEventType(value: String): com.example.data.model.ChargingEventType {
        return com.example.data.model.ChargingEventType.valueOf(value)
    }
}
