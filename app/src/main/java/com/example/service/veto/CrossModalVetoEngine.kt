package com.example.service.veto

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
import com.example.data.veto.AcousticData
import com.example.data.veto.KineticData
import com.example.data.veto.OpticalData
import com.example.data.veto.VetoDecisionResult
import com.example.data.veto.VetoTelemetryInput
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

enum class VetoScenario {
    POCKET_BAG_DIAL,
    MEDIA_TV_PLAYBACK,
    CONFIRMED_VIOLENT_STRUGGLE,
    GENUINE_DISTRESS_SCREAM,
    LIVE_SENSORS
}

class CrossModalVetoEngine {

    companion object {
        const val TAG = "CrossModalVetoEngine"
    }

    /**
     * Evaluates the 2,000ms multi-sensor snapshot and decides whether to
     * ASSERT VETO (cancel emergency dispatch) or CONFIRM DISPATCH (connect to Singappen Police 1091).
     */
    fun evaluate(input: VetoTelemetryInput): VetoDecisionResult {
        val kinetic = input.kinetic_data
        val optical = input.optical_data
        val acoustic = input.acoustic_data
        val source = input.trigger_source

        Log.d(TAG, "Evaluating Snapshot: Trigger=$source, ProxCovered=${optical.proximity_covered}, Jerk=${kinetic.jerk}, VoiceStress=${acoustic.voice_stress_detected}")

        // -------------------------------------------------------------
        // DECISION RULE 1: POCKET / BAG DIAL VETO
        // -------------------------------------------------------------
        // If optical_data.proximity_covered is TRUE,
        // AND kinetic_data shows rhythmic or low energy (mean_g < 1.5, jerk < 2.0),
        // AND acoustic_data.voice_stress_detected is FALSE:
        // -> Set action to "VETO", veto_reason to "ACCIDENTAL_POCKET_DIAL", confidence: 0.95+.
        if (optical.proximity_covered &&
            kinetic.mean_g < 1.5f &&
            kinetic.jerk < 2.0f &&
            !acoustic.voice_stress_detected
        ) {
            val confidence = min(0.99f, 0.95f + (2.0f - kinetic.jerk) * 0.02f)
            return VetoDecisionResult(
                action = "VETO",
                confidence_score = confidence,
                veto_reason = "ACCIDENTAL_POCKET_DIAL",
                cancel_countdown_seconds = 0,
                haptic_feedback_pattern = "SILENT_ABORT",
                technical_justification = "Optical proximity sensor is fully covered (in pocket/bag) with low kinetic jerk (${String.format(java.util.Locale.US, "%.2f", kinetic.jerk)} g/s < 2.0) and zero biological voice stress. Confirmed accidental button trigger."
            )
        }

        // -------------------------------------------------------------
        // DECISION RULE 2: MEDIA / TV PLAYBACK VETO
        // -------------------------------------------------------------
        // If trigger_source is "AUDIO_SPIKE",
        // AND kinetic_data shows stationary rest (mean_g ~ 1.0, jerk < 0.2),
        // AND acoustic_data.high_freq_cutoff_khz <= 15.0 (typical speaker/codec compression):
        // -> Set action to "VETO", veto_reason to "MEDIA_PLAYBACK_DETECTED", confidence: 0.90+.
        val isStationary = abs(kinetic.mean_g - 1.0f) < 0.25f && kinetic.jerk < 0.20f
        if (source == "AUDIO_SPIKE" &&
            isStationary &&
            acoustic.high_freq_cutoff_khz <= 15.0f
        ) {
            val confidence = min(0.97f, 0.90f + (15.0f - acoustic.high_freq_cutoff_khz) * 0.01f + (0.20f - kinetic.jerk) * 0.1f)
            return VetoDecisionResult(
                action = "VETO",
                confidence_score = confidence,
                veto_reason = "MEDIA_PLAYBACK_DETECTED",
                cancel_countdown_seconds = 0,
                haptic_feedback_pattern = "SILENT_ABORT",
                technical_justification = "Acoustic trigger occurred while phone is completely at rest (jerk ${String.format(java.util.Locale.US, "%.2f", kinetic.jerk)} < 0.2) with compressed audio frequency cutoff (${acoustic.high_freq_cutoff_khz} kHz <= 15.0 kHz). Characteristic television/loudspeaker playback."
            )
        }

        // -------------------------------------------------------------
        // DECISION RULE 3: CONFIRMED EMERGENCY DISPATCH
        // -------------------------------------------------------------
        // If kinetic_data indicates violent impact or erratic struggle (jerk > 3.0, rotational_variance high),
        // OR acoustic_data shows genuine biological distress scream (voice_stress_detected is TRUE and high_freq_cutoff_khz > 16.0):
        // -> Set action to "DISPATCH", cancel_countdown_seconds to 0 (immediate) or standard (3s), confidence: 0.90+.
        val isViolentMotion = kinetic.jerk > 3.0f || (kinetic.jerk > 2.5f && kinetic.rotational_variance > 3.5f)
        val isGenuineScream = acoustic.voice_stress_detected && acoustic.high_freq_cutoff_khz > 16.0f

        if (isViolentMotion || isGenuineScream) {
            val confidence = when {
                isViolentMotion && isGenuineScream -> 0.98f
                isViolentMotion -> min(0.96f, 0.90f + (kinetic.jerk - 3.0f) * 0.02f)
                else -> min(0.95f, 0.90f + (acoustic.high_freq_cutoff_khz - 16.0f) * 0.015f)
            }
            val reasonDetail = when {
                isViolentMotion && isGenuineScream -> "Violent impact/struggle (jerk ${String.format(java.util.Locale.US, "%.2f", kinetic.jerk)} g/s) paired with high-frequency biological scream (${acoustic.high_freq_cutoff_khz} kHz)."
                isViolentMotion -> "Severe physical impact/struggle dynamics (jerk ${String.format(java.util.Locale.US, "%.2f", kinetic.jerk)} g/s > 3.0, rot var ${String.format(java.util.Locale.US, "%.2f", kinetic.rotational_variance)})."
                else -> "Authentic biological vocal tract distress verified (voice stress active, high-frequency bandwidth ${acoustic.high_freq_cutoff_khz} kHz > 16.0 kHz)."
            }

            return VetoDecisionResult(
                action = "DISPATCH",
                confidence_score = confidence,
                veto_reason = null,
                cancel_countdown_seconds = 0,
                haptic_feedback_pattern = "DISTRESS_CONFIRMED",
                technical_justification = "EMERGENCY DISPATCH CONFIRMED: $reasonDetail Immediate connection to Singappen Police Control Room 1091 authorized."
            )
        }

        // -------------------------------------------------------------
        // FALLBACK: Cross-Modal Confidence Score (CMCS) Arbitration
        // -------------------------------------------------------------
        val kScore = ((kinetic.jerk / 3.0f) * 0.6f + (kinetic.rotational_variance / 4.0f) * 0.4f).coerceIn(0.0f, 1.0f)
        val oScore = if (optical.proximity_covered) 0.1f else 0.8f
        val aScore = if (acoustic.voice_stress_detected) 0.9f else (acoustic.high_freq_cutoff_khz / 20.0f).coerceIn(0.1f, 0.6f)

        val cmcs = 0.45f * kScore + 0.25f * oScore + 0.30f * aScore

        return if (cmcs < 0.40f) {
            VetoDecisionResult(
                action = "VETO",
                confidence_score = (1.0f - cmcs).coerceIn(0.85f, 0.96f),
                veto_reason = "CROSS_MODAL_LOW_CONFIDENCE",
                cancel_countdown_seconds = 0,
                haptic_feedback_pattern = "SILENT_ABORT",
                technical_justification = "Cross-Modal Confidence Score ($cmcs < 0.40) indicates safe baseline environmental noise without violent physical assault or vocal distress."
            )
        } else {
            VetoDecisionResult(
                action = "DISPATCH",
                confidence_score = cmcs.coerceIn(0.70f, 0.95f),
                veto_reason = null,
                cancel_countdown_seconds = 3,
                haptic_feedback_pattern = "DISTRESS_CONFIRMED",
                technical_justification = "Cross-Modal Confidence Score ($cmcs >= 0.40) indicates elevated risk telemetry. Dispatching to Singappen Police 1091."
            )
        }
    }

    /**
     * Executes the prescribed haptic feedback pattern on the device.
     */
    fun performHapticFeedback(context: Context, pattern: String) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            } ?: return

            when (pattern) {
                "SILENT_ABORT" -> {
                    // Two gentle tick pulses to signal silent false alarm abort
                    vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 40, 80, 40), -1))
                }
                "DISTRESS_CONFIRMED" -> {
                    // Firm continuous buzz to indicate emergency confirmed
                    vibrator.vibrate(VibrationEffect.createOneShot(350, VibrationEffect.DEFAULT_AMPLITUDE))
                }
                else -> Unit
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to vibrate: ${e.message}")
        }
    }

    /**
     * Generates a concrete test snapshot matching the required format.
     */
    fun getPresetSnapshot(scenario: VetoScenario): VetoTelemetryInput {
        return when (scenario) {
            VetoScenario.POCKET_BAG_DIAL -> VetoTelemetryInput(
                kinetic_data = KineticData(
                    mean_g = 1.08f,
                    jerk = 0.65f,
                    rotational_variance = 0.42f,
                    motion_type = "RHYTHMIC_WALKING"
                ),
                optical_data = OpticalData(
                    proximity_covered = true,
                    ambient_lux = 0.05f
                ),
                acoustic_data = AcousticData(
                    thd_percentage = 4.2f,
                    high_freq_cutoff_khz = 11.4f,
                    voice_stress_detected = false,
                    source_type = "FABRIC_FRICTION"
                ),
                trigger_source = "HARDWARE_BUTTON"
            )

            VetoScenario.MEDIA_TV_PLAYBACK -> VetoTelemetryInput(
                kinetic_data = KineticData(
                    mean_g = 1.01f,
                    jerk = 0.08f,
                    rotational_variance = 0.02f,
                    motion_type = "STATIONARY_REST"
                ),
                optical_data = OpticalData(
                    proximity_covered = false,
                    ambient_lux = 180.0f
                ),
                acoustic_data = AcousticData(
                    thd_percentage = 18.6f,
                    high_freq_cutoff_khz = 14.2f, // Typical lossy TV codec cutoff <= 15kHz
                    voice_stress_detected = false,
                    source_type = "LOUDSPEAKER_AUDIO"
                ),
                trigger_source = "AUDIO_SPIKE"
            )

            VetoScenario.CONFIRMED_VIOLENT_STRUGGLE -> VetoTelemetryInput(
                kinetic_data = KineticData(
                    mean_g = 2.45f,
                    jerk = 4.82f, // violent jerk > 3.0
                    rotational_variance = 6.15f,
                    motion_type = "VIOLENT_STRUGGLE"
                ),
                optical_data = OpticalData(
                    proximity_covered = false,
                    ambient_lux = 12.0f
                ),
                acoustic_data = AcousticData(
                    thd_percentage = 8.5f,
                    high_freq_cutoff_khz = 18.2f,
                    voice_stress_detected = true,
                    source_type = "LIVE_VOCAL_SCREAM"
                ),
                trigger_source = "SHAKE_TRIGGER"
            )

            VetoScenario.GENUINE_DISTRESS_SCREAM -> VetoTelemetryInput(
                kinetic_data = KineticData(
                    mean_g = 1.25f,
                    jerk = 2.15f,
                    rotational_variance = 2.80f,
                    motion_type = "ACTIVE_EVASION"
                ),
                optical_data = OpticalData(
                    proximity_covered = false,
                    ambient_lux = 45.0f
                ),
                acoustic_data = AcousticData(
                    thd_percentage = 5.8f,
                    high_freq_cutoff_khz = 19.4f, // Full human vocal tract bandwidth > 16kHz
                    voice_stress_detected = true,
                    source_type = "BIOLOGICAL_DISTRESS_CRY"
                ),
                trigger_source = "AUDIO_SPIKE"
            )

            VetoScenario.LIVE_SENSORS -> VetoTelemetryInput(
                kinetic_data = KineticData(1.0f, 0.4f, 0.2f, "LIVE_NORMAL"),
                optical_data = OpticalData(false, 80.0f),
                acoustic_data = AcousticData(3.0f, 16.5f, false, "AMBIENT_MIC"),
                trigger_source = "HARDWARE_BUTTON"
            )
        }
    }
}
