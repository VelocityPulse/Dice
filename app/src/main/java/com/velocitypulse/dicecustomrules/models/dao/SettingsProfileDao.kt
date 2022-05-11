package com.velocitypulse.dicecustomrules.models.dao

import androidx.room.*
import com.velocitypulse.dicecustomrules.models.entity.SettingsProfile

@Dao
interface SettingsProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppSettings(vararg iSettings: SettingsProfile)

    @Update
    suspend fun updateAppSettings(iSettings: SettingsProfile)

    @Delete
    suspend fun deleteAppSettings(iSettings: SettingsProfile)

    @Query("DELETE FROM ${SettingsProfile.tableName}")
    suspend fun deleteAllAppSettings()

    @Query("SELECT * FROM ${SettingsProfile.tableName}")
    suspend fun getAllAppSettings(): List<SettingsProfile>
}