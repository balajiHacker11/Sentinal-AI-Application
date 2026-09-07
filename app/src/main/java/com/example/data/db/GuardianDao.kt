package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GuardianDao {
    @Query("SELECT * FROM guardians ORDER BY isPrimary DESC, name ASC")
    fun getAllGuardians(): Flow<List<GuardianEntity>>

    @Query("SELECT * FROM guardians ORDER BY isPrimary DESC, name ASC")
    suspend fun getGuardiansListDirect(): List<GuardianEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuardian(guardian: GuardianEntity): Long

    @Update
    suspend fun updateGuardian(guardian: GuardianEntity)

    @Delete
    suspend fun deleteGuardian(guardian: GuardianEntity)

    @Query("SELECT COUNT(*) FROM guardians")
    suspend fun getGuardianCount(): Int
}
