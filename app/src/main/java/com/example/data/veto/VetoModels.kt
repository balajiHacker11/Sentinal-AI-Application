package com.example.data.veto

import org.json.JSONObject

data class KineticData(
    val mean_g: Float,
    val jerk: Float,
    val rotational_variance: Float,
    val motion_type: String
)

data class OpticalData(
    val proximity_covered: Boolean,
    val ambient_lux: Float
)

data class AcousticData(
    val thd_percentage: Float,
    val high_freq_cutoff_khz: Float,
    val voice_stress_detected: Boolean,
    val source_type: String
)

data class VetoTelemetryInput(
    val kinetic_data: KineticData,
    val optical_data: OpticalData,
    val acoustic_data: AcousticData,
    val trigger_source: String // "HARDWARE_BUTTON" | "AUDIO_SPIKE" | "SHAKE_TRIGGER" | "MANUAL_TRIGGER"
)

data class VetoDecisionResult(
    val action: String, // "VETO" | "DISPATCH"
    val confidence_score: Float,
    val veto_reason: String?,
    val cancel_countdown_seconds: Int,
    val haptic_feedback_pattern: String, // "NONE" | "SILENT_ABORT" | "DISTRESS_CONFIRMED"
    val technical_justification: String
) {
    fun toJsonString(): String {
        val json = JSONObject()
        json.put("action", action)
        json.put("confidence_score", String.format(java.util.Locale.US, "%.2f", confidence_score).toDouble())
        json.put("veto_reason", if (veto_reason != null) veto_reason else JSONObject.NULL)
        json.put("cancel_countdown_seconds", cancel_countdown_seconds)
        json.put("haptic_feedback_pattern", haptic_feedback_pattern)
        json.put("technical_justification", technical_justification)
        return json.toString(2)
    }

    companion object {
        fun fromJsonString(jsonStr: String): VetoDecisionResult {
            val json = JSONObject(jsonStr)
            val action = json.getString("action")
            val confidence = json.getDouble("confidence_score").toFloat()
            val reason = if (json.isNull("veto_reason")) null else json.getString("veto_reason")
            val countdown = json.getInt("cancel_countdown_seconds")
            val haptic = json.getString("haptic_feedback_pattern")
            val justification = json.getString("technical_justification")
            return VetoDecisionResult(
                action = action,
                confidence_score = confidence,
                veto_reason = reason,
                cancel_countdown_seconds = countdown,
                haptic_feedback_pattern = haptic,
                technical_justification = justification
            )
        }
    }
}
