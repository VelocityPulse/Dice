package com.velocitypulse.dicecustomrules

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
// Instrumentation Tests

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

        val checkIfValueAreEqual = fun(result: ClickResult): Boolean {
            var sum = 0
            for (item in result.mDiceValues)
                sum += item
            if (sum != result.mDiceSum)
                LogManager.tests("break error")
            return sum == result.mDiceSum
        }

        runBlocking {
            launch(Dispatchers.Main) {

                mViewModel.diceSum.observeForever {
                    synchronized(this) {
                        if (mCurrentClickResult == null)
                            mCurrentClickResult = ClickResult(mDiceSum = it)
                        else {
                            mCurrentClickResult!!.mDiceSum = it
                            assertEquals(true, checkIfValueAreEqual(mCurrentClickResult!!))
                            mCurrentClickResult = null
                        }
                    }
                }

                mViewModel.diceValues.observeForever {
                    synchronized(this) {
                        if (mCurrentClickResult == null)
                            mCurrentClickResult = ClickResult(mDiceValues = it)
                        else {
                            mCurrentClickResult!!.mDiceValues = it
                            assertEquals(true, checkIfValueAreEqual(mCurrentClickResult!!))
                            mCurrentClickResult = null
                        }
                    }
                }

                for (i in 0..200) {
                    LogManager.tests("dice click")
                    delay(15)
                    mViewModel.onDiceClick()
                }
            }
        }
    }

    @Test
    fun failingTest() {
//        fail()
    }
}