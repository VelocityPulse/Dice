package com.velocitypulse.dicecustomrules.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.velocitypulse.dicecustomrules.core.LogManager
import com.velocitypulse.dicecustomrules.models.entity.SettingsProfile
import com.velocitypulse.dicecustomrules.models.repositories.SettingsProfileRepository
import kotlinx.coroutines.launch
import org.jetbrains.annotations.TestOnly

class SettingsActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG: String = "SETTINGS ACTIVITY VIEW MODEL"

    private val mSettingsProfileRepository by lazy {
        SettingsProfileRepository(getApplication())
    }

    val profileList: MutableLiveData<MutableList<SettingsProfile>> by lazy {
        MutableLiveData<MutableList<SettingsProfile>>()
    }

    val updatedProfile: MutableLiveData<SettingsProfile> by lazy {
        MutableLiveData<SettingsProfile>()
    }

    val insertedProfile: MutableLiveData<SettingsProfile> by lazy {
        MutableLiveData<SettingsProfile>()
    }

    val selectedProfile: MutableLiveData<SettingsProfile> by lazy {
        MutableLiveData<SettingsProfile>()
    }

    suspend fun refreshData() {
        LogManager.tests("refresh data")

        val profiles = ArrayList(mSettingsProfileRepository.getAllProfiles())
        profileList.postValue(profiles)

        for (item in profiles) {
            if (item.isSelected)
                selectedProfile.postValue(item) // The reference must be in profiles too
        }
    }

    fun onClickAdd() {
        viewModelScope.launch {
            LogManager.tests(TAG, "click add")
            val newProfile = mSettingsProfileRepository.getNewProfile()
            profileList.value?.let {
                it.add(newProfile)
                insertedProfile.postValue(newProfile)
            }
        }
    }

    fun onItemClicked(profile: SettingsProfile) {
        viewModelScope.launch {
            selectedProfile.value?.let {
                it.isSelected = false
                updatedProfile.value = it
                viewModelScope.launch { mSettingsProfileRepository.updateProfile(it) }
            }

            profile.isSelected = true
            viewModelScope.launch { mSettingsProfileRepository.updateProfile(profile) }
            selectedProfile.value = profile
        }
    }

    @TestOnly
    suspend fun removeAllEntries() {
        mSettingsProfileRepository.nukeTable()
    }

    @TestOnly
    fun getRepository(): SettingsProfileRepository {
        return mSettingsProfileRepository
    }
}