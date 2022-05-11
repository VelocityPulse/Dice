package com.velocitypulse.dicecustomrules.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.velocitypulse.dicecustomrules.models.dao.SettingsProfileDao
import com.velocitypulse.dicecustomrules.models.entity.SettingsProfile

@Database(entities = [SettingsProfile::class], version = 1)
abstract class SettingsProfileDataBase : RoomDatabase() {

    abstract fun settingsProfileDao(): SettingsProfileDao

    companion object {
        @Volatile
        private var INSTANCE: SettingsProfileDataBase? = null

        fun getInstance(context: Context): SettingsProfileDataBase? {

            INSTANCE?.let {
                return INSTANCE!!
            }
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                SettingsProfileDataBase::class.java,
                "settings_profiles.db"
            ).build()

            return INSTANCE
        }
    }
}