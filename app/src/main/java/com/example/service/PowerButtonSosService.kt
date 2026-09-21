package com.example.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.db.AppDatabase
import com.example.data.db.AudioRecordingEntity
import com.example.data.db.IncidentEvidenceEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Foreground Service that keeps the Power Button, Scream Voice, and Shake detectors
 * continuously active 24/7 even when the app is closed, screen locked, or phone in pocket.
 */
class PowerButtonSosService : Service() {

    private var detector: PowerButtonEmergencyDetector? = null
    private var shakeDetector: ShakeMotionDetector? = null
    private var screamDetector: ScreamDetector? = null
    private var cameraCaptureManager: CameraCaptureManager? = null

    companion object {
        const val TAG = "PowerButtonSosService"
        const val CHANNEL_ID = "sentinel_power_button_guard_channel"
        const val NOTIFICATION_ID = 2024
        const val ACTION_START = "ACTION_START_POWER_GUARD"
        const val ACTION_STOP = "ACTION_STOP_POWER_GUARD"
        const val ACTION_TRIGGER_SOS = "ACTION_TRIGGER_SOS"
        const val ACTION_UPDATE_CONFIG = "ACTION_UPDATE_CONFIG"

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

        fun updateConfig(context: Context) {
            try {
                val intent = Intent(context, PowerButtonSosService::class.java).apply {
                    action = ACTION_UPDATE_CONFIG
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update PowerButtonSosService: ${e.message}")
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
        cameraCaptureManager = CameraCaptureManager(this)

        val prefs = getSharedPreferences("tn_safety_prefs", Context.MODE_PRIVATE)
        val triggerOnOff = prefs.getBoolean("power_button_trigger_on_off", true)

        detector = PowerButtonEmergencyDetector(this).apply {
            setTriggerOnPowerOff(triggerOnOff)
            setDangerListener { reason ->
                triggerDangerProtocol(reason)
            }
        }

        shakeDetector = ShakeMotionDetector(this).apply {
            setShakeListener {
                triggerDangerProtocol("SHAKE_TRIGGER")
            }
        }

        screamDetector = ScreamDetector(this).apply {
            setScreamListener {
                triggerDangerProtocol("SCREAM_TRIGGER")
            }
        }

        syncDetectors()
    }

    private fun syncDetectors() {
        val prefs = getSharedPreferences("tn_safety_prefs", Context.MODE_PRIVATE)
        val powerGuardEnabled = prefs.getBoolean("power_button_guard_enabled", true)
        val triggerOnOff = prefs.getBoolean("power_button_trigger_on_off", true)
        val shakeGuardEnabled = prefs.getBoolean("shake_guard_enabled", true)
        val screamGuardEnabled = prefs.getBoolean("scream_guard_enabled", true)

        detector?.setTriggerOnPowerOff(triggerOnOff)
        if (powerGuardEnabled) {
            detector?.startListening()
        } else {
            detector?.stopListening()
        }

        if (shakeGuardEnabled) {
            shakeDetector?.startListening()
        } else {
            shakeDetector?.stopListening()
        }

        val hasAudioPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (screamGuardEnabled && hasAudioPermission) {
            screamDetector?.startListening()
        } else {
            screamDetector?.stopListening()
        }

        Log.i(
            TAG,
            "Sensors synced: PowerGuard=$powerGuardEnabled, ShakeGuard=$shakeGuardEnabled, ScreamGuard=${screamGuardEnabled && hasAudioPermission}"
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                detector?.stopListening()
                shakeDetector?.stopListening()
                screamDetector?.stopListening()
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
                    val hasAudioPermission = ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        val fgsType = if (hasAudioPermission) {
                            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE or ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
                        } else {
                            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                        }
                        startForeground(NOTIFICATION_ID, notification, fgsType)
                    } else {
                        startForeground(NOTIFICATION_ID, notification)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception in startForeground: ${e.message}")
                }
                syncDetectors()
            }
        }
        return START_STICKY
    }

    private fun triggerDangerProtocol(reason: String = "POWER_TRIGGER") {
        Log.w(TAG, "🚨 EXECUTING OUT-OF-APP EMERGENCY SOS PROTOCOL (reason=$reason)")

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
                            title = "Background SOS Evidence ($reason - ${if (isUnder15) "Child <15" else "Adult"})",
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

        // 4. Capture photo evidence if triggered by scream or shake
        if (reason == "SCREAM_TRIGGER" || reason == "SHAKE_TRIGGER") {
            try {
                val photoFile = cameraCaptureManager?.captureIncidentPhoto("Background Out-Of-App Trigger: $reason")
                if (photoFile != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val db = AppDatabase.getInstance(this@PowerButtonSosService)
                            val entity = IncidentEvidenceEntity(
                                title = "Incident Photo ($reason Evidence)",
                                mediaType = "PHOTO",
                                filePath = photoFile.absolutePath
                            )
                            db.evidenceDao().insertEvidence(entity)
                        } catch (ex: Exception) {
                            Log.e(TAG, "Error saving photo evidence: ${ex.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error capturing incident photo: ${e.message}")
            }
        }

        // 5. Send emergency SMS to all guardians with specific alert trigger context
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(this@PowerButtonSosService)
                val guardians = db.guardianDao().getGuardiansListDirect()
                if (guardians.isNotEmpty()) {
                    val alertMessage = when (reason) {
                        "SCREAM_TRIGGER" -> if (isUnder15) {
                            "🚨 EMERGENCY CHILD SAFETY ALERT!\nChild under 15 in danger! Distress scream detected while app was closed/locked.\nDirect call to Childline 1098 initiated. Please check immediately!"
                        } else {
                            "🚨 EMERGENCY DANGER SOS ALERT!\nUrgent danger! Distress scream detected while app was closed/locked.\nDirect call to Women Helpline 1091 initiated. Please check immediately!"
                        }
                        "SHAKE_TRIGGER" -> if (isUnder15) {
                            "🚨 EMERGENCY CHILD SAFETY ALERT!\nChild under 15 in danger! Violent shake detected while phone locked/closed.\nDirect call to Childline 1098 initiated. Please check immediately!"
                        } else {
                            "🚨 EMERGENCY DANGER SOS ALERT!\nUrgent danger! Violent shake detected while phone locked/closed.\nDirect call to Women Helpline 1091 initiated. Please check immediately!"
                        }
                        else -> if (isUnder15) {
                            "🚨 EMERGENCY CHILD SAFETY ALERT!\nChild under 15 in danger! Special Power Button SOS activated while app closed.\nDirect call to Childline 1098 initiated. Please check immediately!"
                        } else {
                            "🚨 EMERGENCY DANGER SOS ALERT!\nUrgent danger! Special Power Button SOS activated while app closed.\nDirect call to Women Helpline 1091 initiated. Please check immediately!"
                        }
                    }
                    sosManager.sendEmergencySmsToGuardians(guardians, customMessage = alertMessage)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending SMS to guardians: ${e.message}")
            }
        }

        // 6. Open MainActivity with danger alert and trigger reason
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
        val powerGuard = prefs.getBoolean("power_button_guard_enabled", true)
        val shakeGuard = prefs.getBoolean("shake_guard_enabled", true)
        val screamGuard = prefs.getBoolean("scream_guard_enabled", true)

        if (powerGuard || shakeGuard || screamGuard) {
            Log.d(TAG, "App task removed; keeping PowerButtonSosService active in background for 24/7 out-of-app protection")
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
                "Sentinel AI 24/7 Background Safety Guard",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Active 24/7 protection: Power Button off/2x, Distress Scream Voice, and Violent Shake triggers direct call, SMS to guardians, siren buzzer, and audio recording even when app is closed."
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
            .setContentTitle("🛡️ 24/7 Out-Of-App Safety Shield Active")
            .setContentText("Power Button • Scream Voice • Shake ➔ Direct Call ($helplineDesc), SMS, Siren")
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
        shakeDetector?.stopListening()
        shakeDetector = null
        screamDetector?.stopListening()
        screamDetector = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

