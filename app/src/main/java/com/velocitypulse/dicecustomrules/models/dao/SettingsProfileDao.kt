package com.velocitypulse.dicecustomrules.models.dao

import androidx.room.*
import com.velocitypulse.dicecustomrules.models.entity.SettingsProfile

@Dao
interface SettingsProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettingsProfile(vararg iSettings: SettingsProfile): LongArray

    @Update
    suspend fun updateSettingsProfile(iSettings: SettingsProfile)

    @Delete
    suspend fun deleteSettingsProfile(iSettings: SettingsProfile)

    @Query("DELETE FROM ${SettingsProfile.tableName}")
    suspend fun deleteAllSettingsProfile()

    @Query("SELECT * FROM ${SettingsProfile.tableName} WHERE id = :id")
    suspend fun getProfileById(id: Long): SettingsProfile?

    @Query("SELECT * FROM ${SettingsProfile.tableName}")
    suspend fun getAllSettingsProfile(): List<SettingsProfile>

    @Query("SELECT * FROM ${SettingsProfile.tableName} WHERE is_selected = 1")
    suspend fun getSelectedProfile(): SettingsProfile?

    @Query("SELECT COUNT(title) FROM ${SettingsProfile.tableName}")
    suspend fun getTableSize(): Int

    @Query("DELETE FROM ${SettingsProfile.tableName}")
    suspend fun nukeTable()

}