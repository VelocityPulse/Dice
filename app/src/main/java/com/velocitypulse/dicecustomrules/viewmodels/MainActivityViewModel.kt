package com.velocitypulse.dicecustomrules.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velocitypulse.dicecustomrules.core.LogManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class MainActivityViewModel : ViewModel() {

    private val TAG: String = "MAIN ACTIVITY VIEW MODEL"
    private val RANDOMIZING_TIME = 500L
    private val MAXIMUM_GAP = 2

    private var mTimerDiceUpdate: Long = 0
    private val mNumberOfTimeAFaceHasBeenSeenMap: IntArray = IntArray(6)
    private val mRandom: Random = Random()
    private var mSongEnabled = true

    val numberOfDice: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val diceValues: MutableLiveData<List<Int>> by lazy {
        MutableLiveData<List<Int>>()
    }

    val isRandomizingDice: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val textDiceSum: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val playDiceSong: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    init {
        numberOfDice.value = 1
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

        val oValue = mRandom.nextInt(6)
        mNumberOfTimeAFaceHasBeenSeenMap[oValue] += 1
        return oValue
    }

    private fun playSong() {
        if (mSongEnabled) {
            if (mRandom.nextInt(2) == 0)
                playDiceSong.postValue(1)
            else
                playDiceSong.postValue(2)

            playDiceSong.value = 0
        }
    }
}