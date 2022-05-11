package com.velocitypulse.dicecustomrules.models.repositories

import android.content.Context
import com.velocitypulse.dicecustomrules.models.AppSettingsDataBase
import com.velocitypulse.dicecustomrules.models.entity.AppSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class AppSettingsRepository(context: Context) {

    private var db = AppSettingsDataBase.getInstance(context)?.appSettingsDao()!!

    suspend fun getNumberOfDice(): Int {
        return getSettings().numberOfDice
    }

    suspend fun setNumberOfDice(num: Int) {
        db.updateAppSettings(getSettings().apply { numberOfDice = num })
    }

    suspend fun getIsSongEnabled(): Boolean {
        return getSettings().isSongEnabled
    }

    suspend fun setIsSongEnabled(enabled: Boolean) {
        db.updateAppSettings(getSettings().apply { isSongEnabled = enabled })
    }

    suspend fun getIsDiceSumEnabled(): Boolean {
        return getSettings().isDiceSumEnabled
    }

    suspend fun setIsDiceSumEnabled(enabled: Boolean) {
        db.updateAppSettings(getSettings().apply { isDiceSumEnabled = enabled })
    }

    suspend fun updateAppSettings(appSettings: AppSettings) {
        db.updateAppSettings(appSettings)
    }

    private suspend fun getSettings(): AppSettings {
        val settingsList: List<AppSettings> = db.getAllAppSettings()
        val settings: AppSettings

        if (settingsList.isEmpty()) {
            settings = AppSettings()
            CoroutineScope(coroutineContext).launch {
                db.insertAppSettings(settings)
            }
        } else {
            settings = settingsList[0]
        }
        return settings
    }
}