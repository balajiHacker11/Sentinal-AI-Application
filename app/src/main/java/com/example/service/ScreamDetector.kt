package com.example.service

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
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
import kotlin.math.abs

class ScreamDetector(private val context: Context) {

    private var audioRecord: AudioRecord? = null
    private var isListening = false
    private var listeningJob: Job? = null

    private val _screamDetected = MutableStateFlow(false)
    val screamDetected: StateFlow<Boolean> = _screamDetected.asStateFlow()

    private val _currentAmplitude = MutableStateFlow(0)
    val currentAmplitude: StateFlow<Int> = _currentAmplitude.asStateFlow()

    private var onScreamTriggered: (() -> Unit)? = null

    fun setScreamListener(listener: () -> Unit) {
        onScreamTriggered = listener
    }

    @SuppressLint("MissingPermission")
    fun startListening() {
        if (isListening) return

        val sampleRate = 8000
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        if (minBufferSize == AudioRecord.ERROR || minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e("ScreamDetector", "Invalid AudioRecord buffer size")
            return
        }

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                minBufferSize * 2
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.e("ScreamDetector", "AudioRecord initialization failed")
                return
            }

            audioRecord?.startRecording()
            isListening = true

            listeningJob = CoroutineScope(Dispatchers.IO).launch {
                val buffer = ShortArray(minBufferSize)
                var cooldown = 0

                while (isListening) {
                    val readSize = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (readSize > 0) {
                        var maxAmplitude = 0
                        for (i in 0 until readSize) {
                            val absVal = abs(buffer[i].toInt())
                            if (absVal > maxAmplitude) {
                                maxAmplitude = absVal
                            }
                        }

                        _currentAmplitude.value = maxAmplitude

                        // Threshold for loud scream or emergency voice trigger (PCM 16-bit max is 32767)
                        val screamThreshold = 18000

                        if (maxAmplitude > screamThreshold && cooldown <= 0) {
                            Log.w("ScreamDetector", "🚨 Loud Scream / Distress voice detected! Amplitude: $maxAmplitude")
                            _screamDetected.value = true
                            triggerHapticFeedback()

                            // Invoke callback on Main Thread
                            CoroutineScope(Dispatchers.Main).launch {
                                onScreamTriggered?.invoke()
                            }

                            cooldown = 15 // Cooldown period to avoid duplicate rapid triggers
                        } else if (cooldown > 0) {
                            cooldown--
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ScreamDetector", "Error starting scream detector: ${e.message}")
            isListening = false
        }
    }

    fun stopListening() {
        isListening = false
        listeningJob?.cancel()
        listeningJob = null

        try {
            audioRecord?.apply {
                if (recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    stop()
                }
                release()
            }
        } catch (e: Exception) {
            Log.e("ScreamDetector", "Error releasing AudioRecord: ${e.message}")
        } finally {
            audioRecord = null
            _screamDetected.value = false
            _currentAmplitude.value = 0
        }
    }

    fun resetScreamState() {
        _screamDetected.value = false
    }

    fun triggerHapticFeedback() {
        try {
            val timings = longArrayOf(0, 300, 100, 300, 100, 500)
            val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                @Suppress("DEPRECATION")
                vibrator.vibrate(1000)
            }
        } catch (e: Exception) {
            Log.e("ScreamDetector", "Vibration failed: ${e.message}")
        }
    }
}
