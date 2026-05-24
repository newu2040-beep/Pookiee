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
import android.content.pm.ServiceInfo
import android.os.Build
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChargingService : Service() {
    companion object {
        const val ACTION_STOP_SOUND = "com.example.ACTION_STOP_SOUND"
        private val _isSoundPlaying = MutableStateFlow(false)
        val isSoundPlaying = _isSoundPlaying.asStateFlow()
    }

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private var exoPlayer: ExoPlayer? = null
    
    private val playerListener = object : androidx.media3.common.Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isSoundPlaying.value = isPlaying
            updateNotification()
        }
    }

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

    private fun updateNotification() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(1337, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_SOUND) {
            stopSound()
        }
        return START_STICKY
    }

    private fun stopSound() {
        exoPlayer?.stop()
    }

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            addListener(playerListener)
        }
        
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }
        registerReceiver(batteryReceiver, filter)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1337, createNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(1337, createNotification())
        }
    }

    private fun handleEvent(type: ChargingEventType) {
        val repository = (application as PookieeApplication).repository
        serviceScope.launch {
            val action = repository.getActionByType(type)
            if (action?.isEnabled == true) {
                action.soundUri?.let { playSound(it, action.volume, action.startMs, action.durationMs) }
                repository.addHistory(ChargingHistory(
                    eventType = type,
                    batteryLevel = -1,
                    isFastCharging = false
                ))
            }
        }
    }

    private fun playSound(uri: String, volume: Float, startMs: Long, durationMs: Long?) {
        exoPlayer?.apply {
            stop()
            clearMediaItems()
            setMediaItem(MediaItem.fromUri(uri))
            seekTo(startMs)
            setVolume(volume)
            prepare()
            play()

            if (durationMs != null && durationMs > 0) {
                serviceScope.launch {
                    kotlinx.coroutines.delay(durationMs)
                    if (isPlaying) stop()
                }
            }
        }
    }

    private fun createNotification(): Notification {
        val channelId = "pookiee_service"
        val channelName = "Pookiee Active Service"
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW))
        
        val stopIntent = Intent(this, ChargingService::class.java).apply {
            action = ACTION_STOP_SOUND
        }
        val stopPendingIntent = android.app.PendingIntent.getService(this, 0, stopIntent, android.app.PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Pookiee Active")
            .setContentText(if (_isSoundPlaying.value) "Meme Sound Playing..." else "Monitoring Charging Events")
            .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
            .setOngoing(true)

        if (_isSoundPlaying.value) {
            builder.addAction(android.R.drawable.ic_media_pause, "Stop Meme", stopPendingIntent)
        }
        
        return builder.build()
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
