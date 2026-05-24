package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChargingAction
import com.example.data.repository.PookieeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PookieeViewModel(private val repository: PookieeRepository) : ViewModel() {
    val allActions: Flow<List<ChargingAction>> = repository.allActions
    val history = repository.history

    fun updateAction(action: ChargingAction) {
        viewModelScope.launch {
            repository.updateAction(action)
        }
    }
    
    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
