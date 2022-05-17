package com.velocitypulse.dicecustomrules.models.repositories

import android.content.Context
import com.velocitypulse.dicecustomrules.models.SettingsProfileDataBase
import com.velocitypulse.dicecustomrules.models.entity.SettingsProfile
import org.jetbrains.annotations.TestOnly

class SettingsProfileRepository(context: Context) {

    private var db = SettingsProfileDataBase.getInstance(context)?.settingsProfileDao()!!

    init {
//        GlobalScope.launch {
//            nukeTable()
//        }
    }

    suspend fun getProfileById(id: Long): SettingsProfile? {
        return db.getProfileById(id)
    }

    @Deprecated("Use getAll")
    suspend fun getNumberOfDice(): Int {
        return getFirstOrNewSettings().numberOfDice
    }

    @Deprecated("Use update")
    suspend fun setNumberOfDice(num: Int) {
        db.updateSettingsProfile(getFirstOrNewSettings().apply { numberOfDice = num })
    }

    @Deprecated("Use getAll")
    suspend fun getIsSongEnabled(): Boolean {
        return getFirstOrNewSettings().isSongEnabled
    }

    @Deprecated("Use update")
    suspend fun setIsSongEnabled(enabled: Boolean) {
        db.updateSettingsProfile(getFirstOrNewSettings().apply { isSongEnabled = enabled })
    }

    @Deprecated("Use getAll")
    suspend fun getIsDiceSumEnabled(): Boolean {
        return getFirstOrNewSettings().isDiceSumEnabled
    }

    @Deprecated("Use update")
    suspend fun setIsDiceSumEnabled(enabled: Boolean) {
        db.updateSettingsProfile(getFirstOrNewSettings().apply { isDiceSumEnabled = enabled })
    }

    suspend fun getSelectedProfile(): SettingsProfile {
        db.getSelectedProfile()?.let {
            return it
        }
        return getFirstOrNewSettings().also { it.isSelected = true }
    }

    suspend fun getNewSettings(): SettingsProfile {
        return SettingsProfile().also {
            it.isSelected = db.getTableSize() == 0
            it.id = db.insertSettingsProfile(it)[0]
        }
    }

    suspend fun updateProfile(iSettingsProfile: SettingsProfile) {
        db.updateSettingsProfile(iSettingsProfile)
    }

    suspend fun getAllProfiles(): List<SettingsProfile> {
        val list = db.getAllSettingsProfile()

        if (list.isEmpty()) {
            return listOf(getFirstOrNewSettings())
        } else {
            if (getSelected(list) == null)
                list[0].isSelected = true
        }
        return list
    }

    suspend fun deleteProfile(mProfile: SettingsProfile) {
        db.deleteSettingsProfile(mProfile)
    }

    @TestOnly
    suspend fun nukeTable() {
        db.nukeTable()
    }

    private suspend fun getFirstOrNewSettings(): SettingsProfile {
        val settingsList: List<SettingsProfile> = db.getAllSettingsProfile()
        val settings: SettingsProfile

        if (settingsList.isEmpty()) {
            settings = SettingsProfile(isSelected = true)
            val id = db.insertSettingsProfile(settings)[0]
            settings.id = id
        } else {
            settings = settingsList[0]
            db.updateSettingsProfile(settings)
        }

        return settings
    }

    private fun getSelected(list: List<SettingsProfile>): SettingsProfile? {
        for (item in list) {
            if (item.isSelected)
                return item
        }
        return null
    }
}
