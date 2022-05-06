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

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG: String = "MAIN ACTIVITY VIEW MODEL"
    private val RANDOMIZING_TIME = 500L
    private val MAXIMUM_GAP = 3

    private var mIsSongEnabled: Boolean = true
    private val mDiceListOfSeenFace: MutableList<IntArray> = ArrayList()
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

    val isDiceSumEnabled: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val diceSum: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val isPlayingDiceSong: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    init {
        viewModelScope.launch { refreshData() }
    }

    suspend fun refreshData() {
        isDiceSumEnabled.postValue(mAppSettingsRepository.getIsDiceSumEnabled())
        numberOfDice.postValue(mAppSettingsRepository.getNumberOfDice())

        mAppSettingsRepository.getNumberOfDice().let {
            numberOfDice.postValue(it)

            mDiceListOfSeenFace.clear()

            for (index in 1..it)
                mDiceListOfSeenFace.add(IntArray(6))
        }

        mIsSongEnabled = mAppSettingsRepository.getIsSongEnabled()
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
                    list.add(getExcludedNumberOrRandom(mDiceListOfSeenFace[i]))
                }

                isRandomizingDice.postValue(false)
                diceValues.postValue(list)
                diceSum.postValue(list.sum())

            } catch (th: Throwable) {
                th.printStackTrace()
            }
        }
    }

    private fun getExcludedNumberOrRandom(numberOfTimeAFaceHasBeenSeenMap: IntArray): Int {
        // Calculating the gap between all numbers inside mNumberOfTimeAFaceHasBeenSeenMap
        for (lCheckedFace in numberOfTimeAFaceHasBeenSeenMap.indices) {

            for (lItem in numberOfTimeAFaceHasBeenSeenMap) {
                if (lItem - numberOfTimeAFaceHasBeenSeenMap[lCheckedFace] > MAXIMUM_GAP) {
                    LogManager.error(
                        TAG,
                        "Index '" + (lCheckedFace + 1) + "' hasn't been printed since a long time..."
                    )

                    // A big gap has been found so we fix it by returning the excluded number
                    numberOfTimeAFaceHasBeenSeenMap[lCheckedFace] += 1
                    return lCheckedFace
                }
            }
        }

        val ret = mRandom.nextInt(6)
        numberOfTimeAFaceHasBeenSeenMap[ret] += 1
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