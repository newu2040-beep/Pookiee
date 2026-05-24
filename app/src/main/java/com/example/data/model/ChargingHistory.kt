package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "charging_history")
@Serializable
data class ChargingHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventType: ChargingEventType,
    val timestamp: Long = System.currentTimeMillis(),
    val batteryLevel: Int,
    val isFastCharging: Boolean = false,
    val temperature: Float = 0f
)
