package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioRecordingDao {
    @Query("SELECT * FROM audio_recordings ORDER BY timestamp DESC")
    fun getAllRecordings(): Flow<List<AudioRecordingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecording(recording: AudioRecordingEntity): Long

    @Delete
    suspend fun deleteRecording(recording: AudioRecordingEntity)
}
