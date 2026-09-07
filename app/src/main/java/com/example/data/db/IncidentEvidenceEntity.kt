package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incident_evidences")
data class IncidentEvidenceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val mediaType: String, // "PHOTO" or "AUDIO"
    val filePath: String,
    val durationSeconds: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val locationInfo: String = "TN Location Recorded"
)
