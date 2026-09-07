package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Detects double-tap (or rapid consecutive presses) of the device Power Button.
 * Uses screen ON/OFF broadcast state transitions which fire each time the power button is clicked,
 * even when the device is locked in pocket or running in background.
 */
class PowerButtonEmergencyDetector(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var resetJob: Job? = null

    private var tapCount = 0
    private var lastTapTime = 0L

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _currentTapCount = MutableStateFlow(0)
    val currentTapCount: StateFlow<Int> = _currentTapCount.asStateFlow()

    private val _triggerOnPowerOff = MutableStateFlow(true)
    val triggerOnPowerOff: StateFlow<Boolean> = _triggerOnPowerOff.asStateFlow()

    private var onDangerTriggeredListener: ((reason: String) -> Unit)? = null

    companion object {
        private const val TAG = "PowerButtonDetector"
        const val DOUBLE_TAP_WINDOW_MS = 2500L // 2.5 seconds window for 2 presses
    }

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == Intent.ACTION_SCREEN_OFF || action == Intent.ACTION_SCREEN_ON) {
                val isScreenOff = (action == Intent.ACTION_SCREEN_OFF)
                handlePowerButtonPress(isScreenOff = isScreenOff)
            }
        }
    }

    fun setTriggerOnPowerOff(enabled: Boolean) {
        _triggerOnPowerOff.value = enabled
    }

    fun setDangerListener(listener: (reason: String) -> Unit) {
        this.onDangerTriggeredListener = listener
    }

    fun startListening() {
        if (_isListening.value) return

        try {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_SCREEN_ON)
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(screenReceiver, filter, Context.RECEIVER_EXPORTED)
            } else {
                context.registerReceiver(screenReceiver, filter)
            }
            _isListening.value = true
            tapCount = 0
            _currentTapCount.value = 0
            Log.d(TAG, "Power Button Emergency Guard started listening.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register screenReceiver: ${e.message}")
        }
    }

    fun stopListening() {
        if (!_isListening.value) return

        try {
            context.unregisterReceiver(screenReceiver)
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering screenReceiver: ${e.message}")
        } finally {
            _isListening.value = false
            tapCount = 0
            _currentTapCount.value = 0
            resetJob?.cancel()
            resetJob = null
            Log.d(TAG, "Power Button Emergency Guard stopped.")
        }
    }

    fun handlePowerButtonPress(isScreenOff: Boolean = false) {
        val now = System.currentTimeMillis()

        if (now - lastTapTime > DOUBLE_TAP_WINDOW_MS) {
            tapCount = 1
        } else {
            tapCount++
        }

        lastTapTime = now
        _currentTapCount.value = tapCount
        Log.d(TAG, "Power button tap registered: count=$tapCount, isScreenOff=$isScreenOff")

        // Haptic feedback to alert user the click registered
        vibrateSingleTap()

        resetJob?.cancel()
        resetJob = scope.launch {
            delay(DOUBLE_TAP_WINDOW_MS)
            tapCount = 0
            _currentTapCount.value = 0
        }

        // Trigger danger protocol if tapCount >= 2 (Power 2x) OR if power button is trying to turn off device/screen when triggerOnPowerOff is active
        if (tapCount >= 2) {
            Log.w(TAG, "🚨 POWER BUTTON 2X TAP DETECTED! Triggering Danger SOS Protocol!")
            tapCount = 0
            _currentTapCount.value = 0
            resetJob?.cancel()
            vibrateSosEmergency()
            onDangerTriggeredListener?.invoke("POWER_2X_TAP")
        } else if (isScreenOff && _triggerOnPowerOff.value) {
            Log.w(TAG, "🚨 POWER BUTTON SCREEN OFF DETECTED! Triggering Danger SOS Protocol!")
            tapCount = 0
            _currentTapCount.value = 0
            resetJob?.cancel()
            vibrateSosEmergency()
            onDangerTriggeredListener?.invoke("POWER_BUTTON_OFF")
        }
    }

    fun simulateDoubleTap() {
        Log.d(TAG, "Simulating Power Button Double Tap manually...")
        handlePowerButtonPress(isScreenOff = false)
        handlePowerButtonPress(isScreenOff = false)
    }

    fun simulatePowerOffTap() {
        Log.d(TAG, "Simulating Power Button Screen Off manually...")
        handlePowerButtonPress(isScreenOff = true)
    }

    private fun vibrateSingleTap() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(100)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateSosEmergency() {
        try {
            val timings = longArrayOf(0, 200, 100, 200, 100, 200, 200, 400, 100, 400, 100, 400, 200, 200, 100, 200)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, -1))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(timings, -1)
            }
        } catch (_: Exception) {}
    }
}
