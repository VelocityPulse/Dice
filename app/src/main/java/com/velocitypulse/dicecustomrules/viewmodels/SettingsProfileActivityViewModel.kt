package com.velocitypulse.dicecustomrules.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.velocitypulse.dicecustomrules.models.repositories.SettingsProfileRepository
import kotlinx.coroutines.launch

class SettingsProfileActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG: String = "SETTINGS ACTIVITY VIEW MODEL"

    private var mIsSongEnabled: Boolean = true

    private val mAppSettingsRepository by lazy {
        SettingsProfileRepository(getApplication())
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

    init {
        viewModelScope.launch {
            with(mAppSettingsRepository) {
                numberOfDice.postValue(getNumberOfDice())
                isDiceSumEnabled.postValue(getIsDiceSumEnabled())
                isSongEnabled.postValue(getIsSongEnabled())
            }
        }
    }

    fun setNumberOfDice(num: Int) {
        viewModelScope.launch {
            mAppSettingsRepository.setNumberOfDice(num)
            numberOfDice.postValue(num)
        }
    }

    fun setIsDiceSumEnabled(enabled: Boolean) {
        viewModelScope.launch {
            mAppSettingsRepository.setIsDiceSumEnabled(enabled)
            isDiceSumEnabled.postValue(enabled)
        }
    }

    fun setIsSongEnabled(enabled: Boolean) {
        viewModelScope.launch {
            mAppSettingsRepository.setIsSongEnabled(enabled)
            isSongEnabled.postValue(enabled)
        }
    }
}