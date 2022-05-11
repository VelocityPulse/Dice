package com.velocitypulse.dicecustomrules.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.velocitypulse.dicecustomrules.core.LogManager
import com.velocitypulse.dicecustomrules.models.repositories.AppSettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG: String = "MAIN ACTIVITY VIEW MODEL"
    private val MAX_DICE: Int = 12 // Todo : Set to Application
    private val MAXIMUM_GAP = 3

    private val mDiceListOfSeenFace: MutableList<IntArray> = ArrayList()
    private val mRandom: Random = Random()
    private var mIsSongEnabled = true

    var mRandomizingTime = 500L

    private val mAppSettingsRepository by lazy {
        AppSettingsRepository(getApplication())
    }

    val numberOfDice: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val diceValues: MutableLiveData<List<Int>> by lazy {
        MutableLiveData<List<Int>>()
    }

    val diceSum: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val isDiceSumEnabled: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isRandomizingDice: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isPlayingDiceSong: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    suspend fun refreshData() {
        LogManager.tests("refresh data")

        isDiceSumEnabled.postValue(mAppSettingsRepository.getIsDiceSumEnabled())

        mAppSettingsRepository.getNumberOfDice().let {
            if (it != numberOfDice.value || diceValues.value?.sum() != diceSum.value) {
                numberOfDice.postValue(it)
                diceValues.postValue(Array(it) { 1 }.toList())
                diceSum.postValue(it)
            }
        }

        mAppSettingsRepository.getNumberOfDice().let {
            numberOfDice.postValue(it)

            mDiceListOfSeenFace.clear()

            for (index in 1..12)
                mDiceListOfSeenFace.add(IntArray(6))
        }

        mIsSongEnabled = mAppSettingsRepository.getIsSongEnabled()

        LogManager.tests("refresh finished")
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
            delay(mRandomizingTime)

            try {
                val newDices = ArrayList<Int>(numberOfDice.value!!)

                var i = -1
                while (++i < numberOfDice.value!!)
                    newDices.add(getExcludedNumberOrRandom(mDiceListOfSeenFace[i]))

                diceValues.postValue(newDices)
                diceSum.postValue(newDices.sum())
            } catch (th: Throwable) {
                th.printStackTrace()
            } finally {
                isRandomizingDice.postValue(false)
            }
        }
    }

    private fun getExcludedNumberOrRandom(numberOfTimeAFaceHasBeenSeenMap: IntArray): Int {
        // Calculating the gap between all numbers inside mNumberOfTimeAFaceHasBeenSeenMap
        for (lTestedFace in numberOfTimeAFaceHasBeenSeenMap.indices) {

            for (lItem in numberOfTimeAFaceHasBeenSeenMap) {
                if (lItem - numberOfTimeAFaceHasBeenSeenMap[lTestedFace] > MAXIMUM_GAP) {
                    LogManager.error(
                        TAG,
                        "Index '" + (lTestedFace + 1) + "' hasn't been printed since a long time..."
                    )

                    // A big gap has been found so we fix it by returning the excluded number
                    numberOfTimeAFaceHasBeenSeenMap[lTestedFace] += 1

                    LogManager.tests("excluded number returning : ${lTestedFace + 1}")
                    return lTestedFace + 1
                }
            }
        }

        val ret = getRandomDiceShape()
        numberOfTimeAFaceHasBeenSeenMap[ret] += 1
        return ret
    }

    fun getRandomDiceShape(): Int {
        return mRandom.nextInt(5) + 1
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