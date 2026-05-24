package com.example.data.repository

import com.example.data.local.PookieeDao
import com.example.data.model.ChargingAction
import com.example.data.model.ChargingEventType
import com.example.data.model.ChargingHistory
import kotlinx.coroutines.flow.Flow

class PookieeRepository(private val dao: PookieeDao) {
    val allActions: Flow<List<ChargingAction>> = dao.getAllActions()
    val history: Flow<List<ChargingHistory>> = dao.getHistory()

    suspend fun getActionByType(type: ChargingEventType) = dao.getActionByType(type)
    suspend fun updateAction(action: ChargingAction) = dao.insertAction(action)
    suspend fun addHistory(history: ChargingHistory) = dao.insertHistory(history)
    suspend fun clearHistory() = dao.clearHistory()
}
