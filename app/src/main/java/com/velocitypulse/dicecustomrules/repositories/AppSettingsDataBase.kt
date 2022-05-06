package com.velocitypulse.dicecustomrules.repositories

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AppSettings::class], version = 1)
abstract class AppSettingsDataBase : RoomDatabase() {

    abstract fun appSettingsDao(): AppSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppSettingsDataBase? = null

        fun getInstance(context: Context): AppSettingsDataBase? {

            INSTANCE?.let {
                return INSTANCE!!
            }
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AppSettingsDataBase::class.java,
                "app_settings"
            ).build()

            return INSTANCE
        }
    }
}