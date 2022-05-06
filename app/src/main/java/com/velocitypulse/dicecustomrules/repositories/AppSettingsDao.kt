package com.velocitypulse.dicecustomrules.repositories

import androidx.room.*

@Dao
interface AppSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppSettings(vararg settings: AppSettings)

    @Update
    suspend fun updateAppSettings(settings: AppSettings)

    @Delete
    suspend fun deleteAppSettings(settings: AppSettings)

    @Query("DELETE FROM app_settings_table")
    suspend fun deleteAllAppSettings()

    @Query("SELECT * FROM app_settings_table")
    suspend fun getAllAppSettings(): List<AppSettings>
}