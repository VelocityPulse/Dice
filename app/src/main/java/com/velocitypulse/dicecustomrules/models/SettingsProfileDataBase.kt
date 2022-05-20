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
    version = 3,
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
                    addMigrations(MIGRATION_2_3)
                    INSTANCE = build()
                } catch (th: Throwable) {
                    LogManager.error(TAG, th.stackTraceToString())
                }
            }
            return INSTANCE
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("ALTER TABLE ${SettingsProfile.tableName}" +
                            " ADD COLUMN text_definition_enabled INTEGER DEFAULT 0 not null")
                    db.execSQL("ALTER TABLE ${SettingsProfile.tableName}" +
                            " ADD COLUMN map_definition TEXT DEFAULT '' not null")
                } catch (th: Throwable) {
                    LogManager.error(TAG, th.stackTraceToString())
                }
            }
        }
    }
}