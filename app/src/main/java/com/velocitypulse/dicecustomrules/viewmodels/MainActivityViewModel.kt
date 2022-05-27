package com.velocitypulse.dicecustomrules.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.velocitypulse.dicecustomrules.core.LogManager
import com.velocitypulse.dicecustomrules.models.entity.SettingsProfile
import com.velocitypulse.dicecustomrules.models.repositories.SettingsProfileRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG: String = "MAIN ACTIVITY VIEW MODEL"
    private val MAX_DICE: Int = 12 // Todo : Set to Application
    private val MAXIMUM_GAP = 3

    private val mDiceListOfSeenFace: MutableList<IntArray> = ArrayList()
    private val mRandom: Random = Random()

    private lateinit var mProfile: SettingsProfile

    var randomizingTime = 500L

    private val mAppSettingsRepository by lazy {
        SettingsProfileRepository(getApplication())
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

    val isDescriptionEnabled: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val description: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    suspend fun refreshData() {
        LogManager.tests("refresh data")

        mProfile = mAppSettingsRepository.getSelectedProfile()

        isDiceSumEnabled.postValue(mProfile.isDiceSumEnabled)
        numberOfDice.postValue(mProfile.numberOfDice)
        diceSum.postValue(mProfile.numberOfDice)
        diceValues.postValue(Array(mProfile.numberOfDice) { 1 }.toList())
        isDescriptionEnabled.postValue(mProfile.isDiceDescriptionEnabled)
        description.postValue(mProfile.getMapDefinition()[mProfile.numberOfDice - 1])

        for (index in 1..12)
            mDiceListOfSeenFace.add(IntArray(6))

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
            delay(randomizingTime)

            try {
                val newDices = ArrayList<Int>(numberOfDice.value!!)

                var i = -1
                while (++i < numberOfDice.value!!)
                    newDices.add(getExcludedNumberOrRandom(mDiceListOfSeenFace[i]))

                diceValues.postValue(newDices)
                diceSum.postValue(newDices.sum())
                description.postValue(mProfile.getMapDefinition()[newDices.sum() - 1])
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
        if (mProfile.isSongEnabled) {
            if (mRandom.nextInt(2) == 0)
                isPlayingDiceSong.postValue(1)
            else
                isPlayingDiceSong.postValue(2)

            isPlayingDiceSong.value = 0
        }
    }
}