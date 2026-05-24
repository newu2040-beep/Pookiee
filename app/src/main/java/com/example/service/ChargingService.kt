package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.PookieeApplication
import com.example.data.model.ChargingEventType
import com.example.data.model.ChargingHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class ChargingService : Service() {
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private var exoPlayer: ExoPlayer? = null
    
    private val batteryReceiver = object : BroadcastReceiver() {
        private var lastLevel = -1
        private var lastStatus = -1

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_POWER_CONNECTED -> handleEvent(ChargingEventType.CONNECTED)
                Intent.ACTION_POWER_DISCONNECTED -> handleEvent(ChargingEventType.DISCONNECTED)
                Intent.ACTION_BATTERY_CHANGED -> {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f
                    
                    if (status == BatteryManager.BATTERY_STATUS_FULL && lastStatus != status) {
                        handleEvent(ChargingEventType.BATTERY_FULL)
                    }
                    
                    if (level == 20 && lastLevel > 20) {
                        handleEvent(ChargingEventType.BATTERY_LOW)
                    }
                    
                    if (temperature > 45 && level > lastLevel) {
                        handleEvent(ChargingEventType.OVERHEATING)
                    }
                    
                    lastLevel = level
                    lastStatus = status
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build()
        
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }
        registerReceiver(batteryReceiver, filter)
        
        startForeground(1337, createNotification())
    }

    private fun handleEvent(type: ChargingEventType) {
        val repository = (application as PookieeApplication).repository
        serviceScope.launch {
            val action = repository.getActionByType(type)
            if (action?.isEnabled == true) {
                action.soundUri?.let { playSound(it, action.volume) }
                
                // Add to history
                repository.addHistory(ChargingHistory(
                    eventType = type,
                    batteryLevel = -1, // Getting real level would be better
                    isFastCharging = false // Simplified
                ))
            }
        }
    }

    private fun playSound(uri: String, volume: Float) {
        exoPlayer?.apply {
            stop()
            clearMediaItems()
            setMediaItem(MediaItem.fromUri(uri))
            setVolume(volume)
            prepare()
            play()
        }
    }

    private fun createNotification(): Notification {
        val channelId = "pookiee_service"
        val channelName = "Pookiee Active Service"
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW))
        
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Pookiee is Monitoring")
            .setContentText("Listening for charging events...")
            .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryReceiver)
        exoPlayer?.release()
        exoPlayer = null
        serviceJob.cancel()
    }
}
