package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
enum class ChargingEventType {
    CONNECTED,
    DISCONNECTED,
    BATTERY_FULL,
    BATTERY_LOW,
    BATTERY_CRITICAL,
    OVERHEATING,
    BATTERY_SAVER_ON,
    CUSTOM_THRESHOLD
}

@Entity(tableName = "charging_actions")
@Serializable
data class ChargingAction(
    @PrimaryKey val eventType: ChargingEventType,
    val isEnabled: Boolean = true,
    val soundUri: String? = null, // URI of the audio file or raw resource name
    val volume: Float = 1.0f,
    val isTtsEnabled: Boolean = false,
    val ttsText: String? = null,
    val threshold: Int? = null, // For CUSTOM_THRESHOLD like 80%
    val vibrationEnabled: Boolean = false,
    val customLabel: String? = null,
    val startMs: Long = 0L,
    val durationMs: Long? = null // null means play until end
)
