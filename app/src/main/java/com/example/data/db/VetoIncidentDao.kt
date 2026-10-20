package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VetoIncidentDao {

    @Query("SELECT * FROM veto_incidents ORDER BY timestamp DESC")
    fun getAllVetoIncidents(): Flow<List<VetoIncidentEntity>>

    @Query("SELECT * FROM veto_incidents WHERE action = 'VETO' ORDER BY timestamp DESC")
    fun getVetoedIncidents(): Flow<List<VetoIncidentEntity>>

    @Query("SELECT * FROM veto_incidents WHERE action = 'DISPATCH' ORDER BY timestamp DESC")
    fun getDispatchedIncidents(): Flow<List<VetoIncidentEntity>>

    @Query("SELECT COUNT(*) FROM veto_incidents WHERE action = 'VETO'")
    fun getVetoCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM veto_incidents WHERE action = 'DISPATCH'")
    fun getDispatchCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVetoIncident(incident: VetoIncidentEntity): Long

    @Delete
    suspend fun deleteVetoIncident(incident: VetoIncidentEntity)

    @Query("DELETE FROM veto_incidents")
    suspend fun clearAllVetoIncidents()
}
