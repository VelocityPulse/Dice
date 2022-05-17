package com.velocitypulse.dicecustomrules.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.velocitypulse.dicecustomrules.core.LogManager
import com.velocitypulse.dicecustomrules.models.dao.SettingsProfileDao
import com.velocitypulse.dicecustomrules.models.entity.SettingsProfile

@Database(
    entities = [SettingsProfile::class],
    version = 2,
)
abstract class SettingsProfileDataBase : RoomDatabase() {

    abstract fun settingsProfileDao(): SettingsProfileDao

    companion object {
        private const val TAG: String = "SETTINGS PROFILE DATABASE"

        @Volatile
        private var INSTANCE: SettingsProfileDataBase? = null

        fun getInstance(context: Context): SettingsProfileDataBase? {

            INSTANCE?.let {
                return INSTANCE!!
            }

            Room.databaseBuilder(
                context.applicationContext,
                SettingsProfileDataBase::class.java,
                "app_settings"
            ).apply {
                try {
                    fallbackToDestructiveMigrationFrom(1)
                    INSTANCE = build()
                } catch (th: Throwable) {
                    LogManager.error(TAG, th.stackTraceToString())
                }
            }
            return INSTANCE
        }

        val MIGRATION_1_x = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("DELETE FROM ${SettingsProfile.tableName_V1}")
                } catch (th: Throwable) {
                    LogManager.error(TAG, th.stackTraceToString())
                }
            }
        }
    }
}