package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.db.AppDatabase
import com.example.data.db.AudioRecordingEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Foreground Service that keeps the Power Button detector active 24/7
 * even when the app is closed, screen locked, or phone in pocket.
 */
class PowerButtonSosService : Service() {

    private var detector: PowerButtonEmergencyDetector? = null

    companion object {
        const val TAG = "PowerButtonSosService"
        const val CHANNEL_ID = "sentinel_power_button_guard_channel"
        const val NOTIFICATION_ID = 2024
        const val ACTION_START = "ACTION_START_POWER_GUARD"
        const val ACTION_STOP = "ACTION_STOP_POWER_GUARD"
        const val ACTION_TRIGGER_SOS = "ACTION_TRIGGER_SOS"

        fun start(context: Context) {
            try {
                val intent = Intent(context, PowerButtonSosService::class.java).apply {
                    action = ACTION_START
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start PowerButtonSosService: ${e.message}")
            }
        }

        fun stop(context: Context) {
            try {
                val intent = Intent(context, PowerButtonSosService::class.java).apply {
                    action = ACTION_STOP
                }
                context.startService(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop PowerButtonSosService: ${e.message}")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val prefs = getSharedPreferences("tn_safety_prefs", Context.MODE_PRIVATE)
        val triggerOnOff = prefs.getBoolean("power_button_trigger_on_off", true)
        detector = PowerButtonEmergencyDetector(this).apply {
            setTriggerOnPowerOff(triggerOnOff)
            setDangerListener { reason ->
                triggerDangerProtocol(reason)
            }
            startListening()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                detector?.stopListening()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_TRIGGER_SOS -> {
                triggerDangerProtocol("NOTIFICATION_ACTION")
            }
            else -> {
                try {
                    val notification = buildForegroundNotification()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        startForeground(
                            NOTIFICATION_ID,
                            notification,
                            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                        )
                    } else {
                        startForeground(NOTIFICATION_ID, notification)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception in startForeground: ${e.message}")
                }
                val prefs = getSharedPreferences("tn_safety_prefs", Context.MODE_PRIVATE)
                val triggerOnOff = prefs.getBoolean("power_button_trigger_on_off", true)
                detector?.setTriggerOnPowerOff(triggerOnOff)
                detector?.startListening()
            }
        }
        return START_STICKY
    }

    private fun triggerDangerProtocol(reason: String = "POWER_TRIGGER") {
        Log.w(TAG, "🚨 EXECUTING SPECIAL POWER BUTTON SOS PROTOCOL (reason=$reason)")

        val prefs = getSharedPreferences("tn_safety_prefs", Context.MODE_PRIVATE)
        val storedAge = prefs.getInt("user_age", -1)
        val isMinorPref = prefs.getBoolean("is_below_18", false)
        val isUnder15Pref = prefs.getBoolean("is_under_15", false)
        val isUnder15 = when {
            storedAge in 1..14 -> true
            storedAge >= 15 -> false
            isUnder15Pref -> true
            else -> isMinorPref
        }
        val targetPhone = if (isUnder15) "1098" else "1091"
        val helplineLabel = if (isUnder15) "Child Safety 1098" else "Women Safety 1091"

        val sosManager = SosManager(this)
        val sirenPlayer = SirenPlayer(this)
        val audioRecorder = AudioRecorder(this)

        // 1. Direct emergency call (1098 for <15, 1091 for 15+)
        try {
            sosManager.triggerDirectCall(targetPhone)
        } catch (e: Exception) {
            Log.e(TAG, "Error initiating direct call: ${e.message}")
        }

        // 2. Blast emergency siren alarm
        try {
            sirenPlayer.startSiren()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting siren: ${e.message}")
        }

        // 3. Continuous audio recording evidence
        try {
            val audioFile = audioRecorder.startRecording()
            if (audioFile != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = AppDatabase.getInstance(this@PowerButtonSosService)
                        val entity = AudioRecordingEntity(
                            title = "Power SOS Evidence (${if (isUnder15) "Child <15" else "Emergency"})",
                            filePath = audioFile.absolutePath,
                            durationSeconds = 60
                        )
                        db.audioRecordingDao().insertRecording(entity)
                    } catch (ex: Exception) {
                        Log.e(TAG, "Error persisting audio recording: ${ex.message}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting audio recording: ${e.message}")
        }

        // 4. Send emergency SMS to all guardians
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(this@PowerButtonSosService)
                val guardians = db.guardianDao().getGuardiansListDirect()
                if (guardians.isNotEmpty()) {
                    val alertMessage = if (isUnder15) {
                        "🚨 EMERGENCY CHILD SAFETY ALERT!\nChild under 15 in danger! Special Power Button SOS activated.\nDirect call to Childline 1098 initiated. Please check immediately!"
                    } else {
                        "🚨 EMERGENCY DANGER SOS ALERT!\nUrgent danger! Special Power Button SOS activated.\nDirect call to Women Helpline 1091 initiated. Please check immediately!"
                    }
                    sosManager.sendEmergencySmsToGuardians(guardians, customMessage = alertMessage)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending SMS to guardians: ${e.message}")
            }
        }

        // 5. Open MainActivity with danger alert
        try {
            val sosIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("EXTRA_AUTO_TRIGGER_POWER_SOS", true)
                putExtra("EXTRA_EMERGENCY_NUMBER", targetPhone)
                putExtra("EXTRA_IS_UNDER_15", isUnder15)
                putExtra("EXTRA_TRIGGER_REASON", reason)
            }
            startActivity(sosIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch MainActivity: ${e.message}")
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val prefs = getSharedPreferences("tn_safety_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("power_button_guard_enabled", true)) {
            Log.d(TAG, "App task removed; keeping PowerButtonSosService active in background")
            val restartIntent = Intent(applicationContext, PowerButtonSosService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(restartIntent)
            } else {
                startService(restartIntent)
            }
        }
        super.onTaskRemoved(rootIntent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Power Button Danger Guard",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Active protection: Power button off or 2x tap triggers direct call, SMS to guardians, siren buzzer, and audio recording."
                enableVibration(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(): Notification {
        val prefs = getSharedPreferences("tn_safety_prefs", Context.MODE_PRIVATE)
        val storedAge = prefs.getInt("user_age", -1)
        val isMinorPref = prefs.getBoolean("is_below_18", false)
        val isUnder15Pref = prefs.getBoolean("is_under_15", false)
        val isUnder15 = when {
            storedAge in 1..14 -> true
            storedAge >= 15 -> false
            isUnder15Pref -> true
            else -> isMinorPref
        }
        val helplineDesc = if (isUnder15) "Under 15: Childline 1098" else "15+: Women Helpline 1091"

        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val sosIntent = Intent(this, PowerButtonSosService::class.java).apply {
            action = ACTION_TRIGGER_SOS
        }
        val sosPendingIntent = PendingIntent.getService(
            this,
            1,
            sosIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("⚡ Special Power Button SOS Guard Active")
            .setContentText("Power Off / 2x: Call ($helplineDesc), SMS Guardians, Siren & Audio Record")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(openPendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(R.drawable.ic_launcher_foreground, "🚨 Trigger SOS Now", sosPendingIntent)
            .build()
    }

    override fun onDestroy() {
        detector?.stopListening()
        detector = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
