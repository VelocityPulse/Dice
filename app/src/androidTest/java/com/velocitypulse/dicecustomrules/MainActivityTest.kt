package com.velocitypulse.dicecustomrules

import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.velocitypulse.dicecustomrules.core.LogManager
import com.velocitypulse.dicecustomrules.repositories.AppSettingsRepository
import com.velocitypulse.dicecustomrules.views.MainActivity
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest : TestCase() {

    @Before
    public override fun setUp() {
        super.setUp()

        LogManager.tests("set up")
    }

    @Test
    fun checkDiceAndSumAreEquals() {
        val activityScenario = launch(MainActivity::class.java)

        activityScenario.moveToState(Lifecycle.State.CREATED)

        for (i in 1..12) {

            activityScenario.onActivity {
                runBlocking {
                    AppSettingsRepository(ApplicationProvider.getApplicationContext())
                        .setNumberOfDice(i)
                    it.mViewModel.refreshData()
                }
            }

            activityScenario.moveToState(Lifecycle.State.RESUMED)

            activityScenario.onActivity { activity ->
                val sum = activity.findViewById<TextView>(R.id.alpha_numeric_text_view).text

                val diceList: List<Dice> = arrayListOf(
                    Dice(activity.findViewById(R.id.dice_1)),
                    Dice(activity.findViewById(R.id.dice_2)),
                    Dice(activity.findViewById(R.id.dice_3)),
                    Dice(activity.findViewById(R.id.dice_4)),
                    Dice(activity.findViewById(R.id.dice_5)),
                    Dice(activity.findViewById(R.id.dice_6)),
                    Dice(activity.findViewById(R.id.dice_7)),
                    Dice(activity.findViewById(R.id.dice_8)),
                    Dice(activity.findViewById(R.id.dice_9)),
                    Dice(activity.findViewById(R.id.dice_10)),
                    Dice(activity.findViewById(R.id.dice_11)),
                    Dice(activity.findViewById(R.id.dice_12))
                )

                var viewSum = 0
                for (item in diceList) {
                    viewSum += item.getDiceValue()
                }
                assertEquals(viewSum, sum.toString().toInt())
            }
        }

        activityScenario.moveToState(Lifecycle.State.DESTROYED)
    }
}
