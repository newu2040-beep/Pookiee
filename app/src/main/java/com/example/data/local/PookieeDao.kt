package com.example.data.local

import androidx.room.*
import com.example.data.model.ChargingAction
import com.example.data.model.ChargingEventType
import com.example.data.model.ChargingHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface PookieeDao {
    @Query("SELECT * FROM charging_actions")
    fun getAllActions(): Flow<List<ChargingAction>>

    @Query("SELECT * FROM charging_actions WHERE eventType = :type")
    suspend fun getActionByType(type: ChargingEventType): ChargingAction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: ChargingAction)

    @Query("SELECT * FROM charging_history ORDER BY timestamp DESC LIMIT 100")
    fun getHistory(): Flow<List<ChargingHistory>>

    @Insert
    suspend fun insertHistory(history: ChargingHistory)

    @Query("DELETE FROM charging_history")
    suspend fun clearHistory()
}
