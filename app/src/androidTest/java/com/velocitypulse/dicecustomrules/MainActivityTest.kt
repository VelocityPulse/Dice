package com.velocitypulse.dicecustomrules

import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.velocitypulse.dicecustomrules.core.LogManager
import com.velocitypulse.dicecustomrules.models.repositories.SettingsProfileRepository
import com.velocitypulse.dicecustomrules.views.DiceView
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
                    SettingsProfileRepository(ApplicationProvider.getApplicationContext())
                        .setNumberOfDice(i)
                    it.mViewModel.refreshData()
                }
            }

            activityScenario.moveToState(Lifecycle.State.RESUMED)

            activityScenario.onActivity { activity ->
                val sum = activity.findViewById<TextView>(R.id.alpha_numeric_text_view).text

                val diceViewList: List<DiceView> = arrayListOf(
                    DiceView(activity.findViewById(R.id.dice_1)),
                    DiceView(activity.findViewById(R.id.dice_2)),
                    DiceView(activity.findViewById(R.id.dice_3)),
                    DiceView(activity.findViewById(R.id.dice_4)),
                    DiceView(activity.findViewById(R.id.dice_5)),
                    DiceView(activity.findViewById(R.id.dice_6)),
                    DiceView(activity.findViewById(R.id.dice_7)),
                    DiceView(activity.findViewById(R.id.dice_8)),
                    DiceView(activity.findViewById(R.id.dice_9)),
                    DiceView(activity.findViewById(R.id.dice_10)),
                    DiceView(activity.findViewById(R.id.dice_11)),
                    DiceView(activity.findViewById(R.id.dice_12))
                )

                var viewSum = 0
                for (item in diceViewList) {
                    viewSum += item.getDiceValue()
                }
                assertEquals(viewSum, sum.toString().toInt())
            }
        }

        activityScenario.moveToState(Lifecycle.State.DESTROYED)
    }
}
