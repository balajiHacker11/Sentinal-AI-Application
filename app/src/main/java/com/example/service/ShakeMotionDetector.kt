package com.example.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.sqrt

enum class ShakeSensitivity(val thresholdG: Float, val label: String, val tamilLabel: String) {
    HIGH(18.0f, "High", "அதிகம்"),
    MAXIMUM(24.0f, "Maximum Violent Shake", "அதிகபட்ச தீவிர அதிர்வு")
}

class ShakeMotionDetector(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _currentGForce = MutableStateFlow(0f)
    val currentGForce: StateFlow<Float> = _currentGForce.asStateFlow()

    private val _currentMotionPercent = MutableStateFlow(0f)
    val currentMotionPercent: StateFlow<Float> = _currentMotionPercent.asStateFlow()

    private val _shakeDetected = MutableStateFlow(false)
    val shakeDetected: StateFlow<Boolean> = _shakeDetected.asStateFlow()

    private val _sensitivity = MutableStateFlow(ShakeSensitivity.MAXIMUM)
    val sensitivity: StateFlow<ShakeSensitivity> = _sensitivity.asStateFlow()

    private var onShakeTriggered: (() -> Unit)? = null

    // Shake debounce tracking
    private var lastShakeTimestamp: Long = 0
    private var shakeCount = 0
    private var lastEventTime: Long = 0

    private val scope = CoroutineScope(Dispatchers.Default + Job())

    fun setShakeListener(listener: () -> Unit) {
        onShakeTriggered = listener
    }

    fun setSensitivity(level: ShakeSensitivity) {
        _sensitivity.value = level
    }

    fun startListening() {
        if (_isListening.value || accelerometer == null) {
            if (accelerometer == null) {
                Log.w("ShakeMotionDetector", "Accelerometer sensor not available on this device")
            }
            return
        }

        val registered = sensorManager?.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_GAME
        ) ?: false

        if (registered) {
            _isListening.value = true
            Log.i("ShakeMotionDetector", "Shake motion sensor started")
        }
    }

    fun stopListening() {
        if (!_isListening.value) return
        try {
            sensorManager?.unregisterListener(this)
        } catch (e: Exception) {
            Log.e("ShakeMotionDetector", "Error unregistering sensor: ${e.message}")
        }
        _isListening.value = false
        _currentGForce.value = 0f
        _currentMotionPercent.value = 0f
        Log.i("ShakeMotionDetector", "Shake motion sensor stopped")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Calculate total magnitude of acceleration
        val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val netGForce = Math.abs(acceleration - SensorManager.GRAVITY_EARTH)

        val threshold = _sensitivity.value.thresholdG
        val percent = (netGForce / threshold).coerceIn(0f, 1.5f)

        _currentGForce.value = netGForce
        _currentMotionPercent.value = percent

        val currentTime = System.currentTimeMillis()

        // Check if motion exceeds threshold
        if (netGForce >= threshold) {
            if (currentTime - lastEventTime > 250) {
                shakeCount++
                lastEventTime = currentTime
            }

            // If 2 or more violent motion peaks occur within 1.5 seconds, or 1 massive shock (> 32 m/s²)
            if ((shakeCount >= 2 && currentTime - lastShakeTimestamp < 1500) || netGForce > 32f) {
                if (currentTime - lastShakeTimestamp > 3000) { // Cooldown of 3s to prevent spamming
                    lastShakeTimestamp = currentTime
                    shakeCount = 0
                    triggerEmergencyShake()
                }
            } else if (currentTime - lastShakeTimestamp > 1500) {
                lastShakeTimestamp = currentTime
                shakeCount = 1
            }
        } else if (currentTime - lastShakeTimestamp > 2000) {
            shakeCount = 0
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }

    private fun triggerEmergencyShake() {
        scope.launch(Dispatchers.Main) {
            vibrateAlert()
            _shakeDetected.value = true
            onShakeTriggered?.invoke()
            Log.w("ShakeMotionDetector", "🚨 VIOLENT SHAKE EMERGENCY TRIGGERED!")
        }
    }

    fun resetDetection() {
        _shakeDetected.value = false
    }

    private fun vibrateAlert() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 300, 150, 300), -1))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 300, 150, 300), -1)
            }
        } catch (e: Exception) {
            Log.e("ShakeMotionDetector", "Vibration failed: ${e.message}")
        }
    }
}
