package com.example.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.math.sign

class SirenPlayer(private val context: Context? = null) {
    private var audioTrack: AudioTrack? = null
    private var toneGenerator: ToneGenerator? = null
    private var toneJob: Job? = null
    private var isPlayingSiren = false

    private val scope = CoroutineScope(Dispatchers.Default)

    fun isPlaying(): Boolean = isPlayingSiren

    fun maximizeVolume() {
        context?.let { ctx ->
            try {
                val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                audioManager?.let { am ->
                    val streams = listOf(
                        AudioManager.STREAM_ALARM,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.STREAM_RING,
                        AudioManager.STREAM_NOTIFICATION,
                        AudioManager.STREAM_SYSTEM
                    )
                    for (stream in streams) {
                        try {
                            val maxVol = am.getStreamMaxVolume(stream)
                            am.setStreamVolume(stream, maxVol, 0)
                        } catch (_: Exception) {
                            // ignore restricted stream exceptions
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SirenPlayer", "Failed to maximize volume: ${e.message}")
            }
        }
    }

    fun startSiren() {
        if (isPlayingSiren) return

        maximizeVolume()

        try {
            val sampleRate = 44100
            val durationSec = 2.0 // 2 second police siren sweep cycle
            val numSamples = (sampleRate * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            // Synthesize piercing Police Siren / Buzzer sound waveform
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                
                // Frequency sweep between 700 Hz and 1600 Hz every 0.70 seconds
                val sweepPhase = (t % 0.70) / 0.70
                val currentFreq = if (sweepPhase < 0.5) {
                    700.0 + (1600.0 - 700.0) * (sweepPhase * 2.0)
                } else {
                    1600.0 - (1600.0 - 700.0) * ((sweepPhase - 0.5) * 2.0)
                }

                val primaryAngle = 2.0 * Math.PI * currentFreq * t
                val harmonicAngle = 2.0 * Math.PI * (currentFreq * 1.5) * t

                // Combine sine + harmonic + square-wave saturation for sharp police buzzer tone
                val sineWave = sin(primaryAngle)
                val harmonic = sin(harmonicAngle) * 0.35
                val squareDistortion = sign(sineWave) * 0.25

                val combined = (sineWave * 0.65 + harmonic + squareDistortion).coerceIn(-1.0, 1.0)
                buffer[i] = (combined * Short.MAX_VALUE * 0.98).toInt().toShort()
            }

            val bufferSizeBytes = buffer.size * 2
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSizeBytes)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack?.let { track ->
                track.write(buffer, 0, buffer.size)
                track.setLoopPoints(0, numSamples, -1) // Loop continuously
                track.play()
                isPlayingSiren = true
            }

            // ToneGenerator hardware pulse backup for guaranteed loud alarm buzzer
            try {
                toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
                toneJob = scope.launch {
                    while (isActive && isPlayingSiren) {
                        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 300)
                        delay(400)
                    }
                }
            } catch (e: Exception) {
                Log.e("SirenPlayer", "ToneGenerator error: ${e.message}")
            }

        } catch (e: Exception) {
            Log.e("SirenPlayer", "Error playing police buzzer siren: ${e.message}")
            isPlayingSiren = false
        }
    }

    fun stopSiren() {
        isPlayingSiren = false

        toneJob?.cancel()
        toneJob = null

        try {
            toneGenerator?.stopTone()
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            Log.e("SirenPlayer", "Error stopping ToneGenerator: ${e.message}")
        }

        try {
            audioTrack?.apply {
                if (playState == AudioTrack.PLAYSTATE_PLAYING) {
                    stop()
                }
                release()
            }
        } catch (e: Exception) {
            Log.e("SirenPlayer", "Error stopping AudioTrack: ${e.message}")
        } finally {
            audioTrack = null
        }
    }
}

