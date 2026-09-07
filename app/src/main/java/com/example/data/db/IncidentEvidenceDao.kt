package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentEvidenceDao {
    @Query("SELECT * FROM incident_evidences ORDER BY timestamp DESC")
    fun getAllEvidences(): Flow<List<IncidentEvidenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidence(evidence: IncidentEvidenceEntity): Long

    @Delete
    suspend fun deleteEvidence(evidence: IncidentEvidenceEntity)
}
