package com.velocitypulse.dicecustomrules

import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.velocitypulse.dicecustomrules.core.LogManager
import com.velocitypulse.dicecustomrules.viewmodels.MainActivityViewModel
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.annotation.concurrent.GuardedBy

@RunWith(AndroidJUnit4::class)
class MainActivityViewModelTest : TestCase() {

    private lateinit var mViewModel: MainActivityViewModel

    private data class ClickResult(
        var mDiceSum: Int = 0,
        var mDiceValues: List<Int> = ArrayList<Int>()
    )

    @GuardedBy("this")
    private var mCurrentClickResult: ClickResult? = null

    @Before
    public override fun setUp() {
        super.setUp()

        LogManager.tests("set up")

        mViewModel = MainActivityViewModel(ApplicationProvider.getApplicationContext())
        runBlocking {
            mViewModel.refreshData()
        }

    }

    @Test
    fun checkSumEqualsDiceValues() {
        LogManager.tests("check sum equal dice values")
        mViewModel.mRandomizingTime = 0

        var assertNumber = 0
        val checkIfValueAreEqual = fun(result: ClickResult): Boolean {
            var sum = 0
            for (item in result.mDiceValues)
                sum += item
            if (sum != result.mDiceSum)
                LogManager.tests("break error")
            LogManager.tests("assert n°$assertNumber")
            assertNumber++
            return sum == result.mDiceSum
        }

        runBlocking {
            launch(Dispatchers.Main) {

                val diceSumObserver = Observer<Int> {
                    synchronized(this) {
                        if (mCurrentClickResult == null)
                            mCurrentClickResult = ClickResult(mDiceSum = it)
                        else {
                            mCurrentClickResult!!.mDiceSum = it
                            assertEquals(true, checkIfValueAreEqual(mCurrentClickResult!!))
                            mCurrentClickResult = null
                        }
                    }
                }.apply {
                    mViewModel.diceSum.observeForever(this)
                }

                val diceValueObserver = Observer<List<Int>> {
                    synchronized(this) {
                        if (mCurrentClickResult == null)
                            mCurrentClickResult = ClickResult(mDiceValues = it)
                        else {
                            mCurrentClickResult!!.mDiceValues = it
                            assertEquals(true, checkIfValueAreEqual(mCurrentClickResult!!))
                            mCurrentClickResult = null
                        }
                    }
                }.apply {
                    mViewModel.diceValues.observeForever(this)
                }

                for (numberOfDice in 1..12) {
                    mViewModel.numberOfDice.postValue(numberOfDice)

                    loopDiceClick(200, 15)
                }
                mViewModel.diceValues.removeObserver(diceValueObserver)
                mViewModel.diceSum.removeObserver(diceSumObserver)
            }
        }
    }

    @Test
    fun checkRandomBounds() {
        LogManager.tests("check random bounds values")
        mViewModel.mRandomizingTime = 0

        val checkBounds = fun(list: List<Int>): Boolean {
            for (item in list) {
                if (item < 1 || item > 6)
                    return false
            }
            return true
        }

        runBlocking {
            launch(Dispatchers.Main) {

                val diceValueObserver = Observer<List<Int>> {
                    assert(checkBounds(it))
                }.apply {
                    mViewModel.diceValues.observeForever(this)
                }

                loopDiceClick(200, 15)

                mViewModel.diceValues.removeObserver(diceValueObserver)
            }
        }
    }

    suspend fun loopDiceClick(loopCount: Int = 200, delayMillis: Long = 15) {
        for (i in 0..loopCount) {
            LogManager.tests("dice click $i")
            delay(delayMillis)
            mViewModel.onDiceClick()
        }
    }

    @Test
    fun failingTest() {
//        fail()
    }
}