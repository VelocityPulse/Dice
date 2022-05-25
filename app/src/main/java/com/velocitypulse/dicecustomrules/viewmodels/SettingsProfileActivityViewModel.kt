package com.velocitypulse.dicecustomrules.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.velocitypulse.dicecustomrules.core.LogManager
import com.velocitypulse.dicecustomrules.models.entity.SettingsProfile
import com.velocitypulse.dicecustomrules.models.repositories.SettingsProfileRepository
import kotlinx.coroutines.launch

class SettingsProfileActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG: String = "SETTINGS ACTIVITY VIEW MODEL"

    private var mIsSongEnabled: Boolean = true

    private lateinit var mProfile: SettingsProfile

    private val mSettingsProfileRepository by lazy {
        SettingsProfileRepository(getApplication())
    }

    val finishActivity: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val title: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val numberOfDice: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val isDiceSumEnabled: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isSongEnabled: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val diceDescriptionEnabled: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val diceDescriptionPickerSize: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val descriptionMap: MutableLiveData<MutableMap<Int, String>> by lazy {
        MutableLiveData<MutableMap<Int, String>>()
    }

    suspend fun refreshData(id: Long) {
        LogManager.tests("refresh data")

        mSettingsProfileRepository.getProfileById(id).also {
            mProfile = it ?: mSettingsProfileRepository.getNewProfile()
        }

        numberOfDice.postValue(mProfile.numberOfDice)
        isDiceSumEnabled.postValue(mProfile.isDiceSumEnabled)
        isSongEnabled.postValue(mProfile.isSongEnabled)
        title.postValue(mProfile.title)

        diceDescriptionEnabled.postValue(mProfile.isDiceDescriptionEnabled)
        descriptionMap.postValue(mProfile.getMapDefinition())
        diceDescriptionPickerSize.postValue(6 * mProfile.numberOfDice)
    }

    fun setTitle(newTitle: String) {
        viewModelScope.launch {
            mProfile.title = newTitle
            mSettingsProfileRepository.updateProfile(mProfile)
            // Title not updated because it creates infinite loop. Observable useful only for init
        }
    }

    fun setNumberOfDice(num: Int) {
        viewModelScope.launch {
            mProfile.numberOfDice = num
            mSettingsProfileRepository.updateProfile(mProfile)
            numberOfDice.postValue(num)
            diceDescriptionPickerSize.postValue(6 * mProfile.numberOfDice)
        }
    }

    fun setIsDiceSumEnabled(enabled: Boolean) {
        viewModelScope.launch {
            mProfile.isDiceSumEnabled = enabled
            mSettingsProfileRepository.updateProfile(mProfile)
            isDiceSumEnabled.postValue(enabled)
        }
    }

    fun setIsSongEnabled(enabled: Boolean) {
        viewModelScope.launch {
            mProfile.isSongEnabled = enabled
            mSettingsProfileRepository.updateProfile(mProfile)
            isSongEnabled.postValue(enabled)
        }
    }

    fun setIsDiceDescriptionEnabled(enabled: Boolean) {
        viewModelScope.launch {
            mProfile.isDiceDescriptionEnabled = enabled
            mSettingsProfileRepository.updateProfile(mProfile)
            diceDescriptionEnabled.postValue(enabled)
        }
    }

    fun setMapDescription(map: Map<Int, String>) {

    }

    fun deleteProfile() {
        viewModelScope.launch {
            mSettingsProfileRepository.deleteProfile(mProfile)
            finishActivity.postValue(true)
        }
    }

    fun onDescriptionEdit(description: String, position: Int) {
        descriptionMap.value?.let {
            if (description.isBlank())
                it.remove(position)
            else
                it[position] = description

            viewModelScope.launch {
                mProfile.setMapDefinition(descriptionMap.value!! as Map<Int, String>)
                mSettingsProfileRepository.updateProfile(mProfile)
            }
        }
    }
}