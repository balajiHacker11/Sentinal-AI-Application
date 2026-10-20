package com.example.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "veto_incidents")
data class VetoIncidentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "trigger_source")
    val triggerSource: String,

    @ColumnInfo(name = "action")
    val action: String, // "VETO" or "DISPATCH"

    @ColumnInfo(name = "confidence_score")
    val confidenceScore: Float,

    @ColumnInfo(name = "veto_reason")
    val vetoReason: String?,

    @ColumnInfo(name = "cancel_countdown_seconds")
    val cancelCountdownSeconds: Int,

    @ColumnInfo(name = "haptic_feedback_pattern")
    val hapticFeedbackPattern: String,

    @ColumnInfo(name = "technical_justification")
    val technicalJustification: String,

    // Specific Kinetic Columns
    @ColumnInfo(name = "mean_g")
    val meanG: Float,

    @ColumnInfo(name = "jerk")
    val jerk: Float,

    @ColumnInfo(name = "rotational_variance")
    val rotationalVariance: Float,

    @ColumnInfo(name = "motion_type")
    val motionType: String,

    // Specific Optical Columns
    @ColumnInfo(name = "proximity_covered")
    val proximityCovered: Boolean,

    @ColumnInfo(name = "ambient_lux")
    val ambientLux: Float,

    // Specific Acoustic Columns
    @ColumnInfo(name = "thd_percentage")
    val thdPercentage: Float,

    @ColumnInfo(name = "high_freq_cutoff_khz")
    val highFreqCutoffKhz: Float,

    @ColumnInfo(name = "voice_stress_detected")
    val voiceStressDetected: Boolean,

    @ColumnInfo(name = "source_type")
    val sourceType: String,

    // Exact strict JSON Output string
    @ColumnInfo(name = "raw_json_output")
    val rawJsonOutput: String
)
