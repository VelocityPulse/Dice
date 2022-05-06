package com.velocitypulse.dicecustomrules.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.velocitypulse.dicecustomrules.core.LogManager
import com.velocitypulse.dicecustomrules.repositories.AppSettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG: String = "MAIN ACTIVITY VIEW MODEL"
    private val RANDOMIZING_TIME = 500L
    private val MAXIMUM_GAP = 2

    private var mTimerDiceUpdate: Long = 0
    private var mIsSongEnabled = true
    private val mNumberOfTimeAFaceHasBeenSeenMap: IntArray = IntArray(6)
    private val mRandom: Random = Random()

    private val mAppSettingsRepository by lazy {
        AppSettingsRepository(getApplication())
    }

    val numberOfDice: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val diceValues: MutableLiveData<List<Int>> by lazy {
        MutableLiveData<List<Int>>()
    }

    val isRandomizingDice: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    // TODO impl observer
    val isDiceSumEnabled: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val textDiceSum: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val isPlayingDiceSong: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    init {
        viewModelScope.launch {
            isDiceSumEnabled.postValue(mAppSettingsRepository.getIsDiceSumEnabled())
            numberOfDice.postValue(mAppSettingsRepository.getNumberOfDice())

            mIsSongEnabled = mAppSettingsRepository.getIsSongEnabled()
        }
    }

    fun onDiceClick() {
        playSong()
        randomizeDice()
    }

    private fun randomizeDice() {
        if (isRandomizingDice.value == true) {
            LogManager.error(TAG, "Already randomizing dice")
            return
        }

        viewModelScope.launch {
            isRandomizingDice.postValue(true)
            delay(RANDOMIZING_TIME)

            try {
                val list = ArrayList<Int>(numberOfDice.value!!)

                var i = -1
                while (++i < numberOfDice.value!!) {
                    list.add(getExcludedNumberOrRandom())
                }

                isRandomizingDice.postValue(false)
                diceValues.postValue(list)
                textDiceSum.postValue(list.sum().toString())

            } catch (th: Throwable) {
                th.printStackTrace()
            }

        }
    }

    private fun getExcludedNumberOrRandom(): Int {
        // Calculating the gap between all numbers inside mNumberOfTimeAFaceHasBeenSeenMap
        for (lCheckedFace in mNumberOfTimeAFaceHasBeenSeenMap.indices) {

            for (lItem in mNumberOfTimeAFaceHasBeenSeenMap) {
                if (lItem - mNumberOfTimeAFaceHasBeenSeenMap[lCheckedFace] > MAXIMUM_GAP) {
                    LogManager.error(
                        TAG,
                        "Index '" + (lCheckedFace + 1) + "' hasn't been printed since a long time..."
                    )

                    // A big gap has been found so we fix it by returning the excluded number
                    mNumberOfTimeAFaceHasBeenSeenMap[lCheckedFace] += 1
                    return lCheckedFace
                }
            }
        }

        val ret = mRandom.nextInt(6)
        mNumberOfTimeAFaceHasBeenSeenMap[ret] += 1
        return ret
    }

    private fun playSong() {
        if (mIsSongEnabled) {
            if (mRandom.nextInt(2) == 0)
                isPlayingDiceSong.postValue(1)
            else
                isPlayingDiceSong.postValue(2)

            isPlayingDiceSong.value = 0
        }
    }
}