package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.local.PookieeDatabase
import com.example.data.repository.PookieeRepository

class PookieeApplication : Application() {
    private val database by lazy {
        Room.databaseBuilder(this, PookieeDatabase::class.java, "pookiee.db").build()
    }
    
    val repository by lazy {
        PookieeRepository(database.dao())
    }
}
