package com.velocitypulse.dicecustomrules.models.repositories

import android.content.Context
import com.velocitypulse.dicecustomrules.models.SettingsProfileDataBase
import com.velocitypulse.dicecustomrules.models.entity.SettingsProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class SettingsProfileRepository(context: Context) {

    private var db = SettingsProfileDataBase.getInstance(context)?.settingsProfileDao()!!

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

    suspend fun updateAppSettings(iSettingsProfile: SettingsProfile) {
        db.updateAppSettings(iSettingsProfile)
    }

    private suspend fun getSettings(): SettingsProfile {
        val settingsList: List<SettingsProfile> = db.getAllAppSettings()
        val settings: SettingsProfile

        if (settingsList.isEmpty()) {
            settings = SettingsProfile()
            CoroutineScope(coroutineContext).launch {
                db.insertAppSettings(settings)
            }
        } else {
            settings = settingsList[0]
        }
        return settings
    }
}