package com.velocitypulse.dicecustomrules.viewmodels

import android.app.Instrumentation.ActivityMonitor
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewModelScope
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.velocitypulse.dicecustomrules.AppTestCase
import com.velocitypulse.dicecustomrules.core.LogManager
import com.velocitypulse.dicecustomrules.views.MainActivity
import com.velocitypulse.dicecustomrules.views.SettingsActivity
import kotlinx.coroutines.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsActivityViewModelTest : AppTestCase() {

    private val globalViewModel =
        SettingsActivityViewModel(ApplicationProvider.getApplicationContext())

    @Before
    public override fun setUp() {
        super.setUp()

        LogManager.tests("set up")
    }

    @Test
    fun isNotEmptyAtFirstOpening() {
        for (i in 0..3) {
            val activityScenario = provideEmptyResumedActivity()

            Thread.sleep(200)
            activityScenario.onActivity {
                assertEquals(1, it.adapter.settingsProfileList.size)
            }

            activityScenario.moveToState(Lifecycle.State.DESTROYED)
        }
    }

    @Test
    fun isNotEmptyAtFirstOpeningFromMainActivity() {
        clearDataBase()

        val activityMonitor: ActivityMonitor =
            getInstrumentation().addMonitor(SettingsActivity::class.java.name, null, false)

        val mainActivityScenario = ActivityScenario.launch(MainActivity::class.java)

        mainActivityScenario.onActivity {
            it.launchSettings()
        }

        val nextActivity: SettingsActivity =
            getInstrumentation().waitForMonitorWithTimeout(
                activityMonitor,
                500
            ) as SettingsActivity

        Thread.sleep(100)
        assertEquals(1, nextActivity.adapter.settingsProfileList.size)
    }

    @Test
    fun isDefaultSelected() {
        val activityScenario = provideEmptyResumedActivity()

        activityScenario.onActivity {
            assertEquals(true, it.mViewModel.profileList.value!![0].isSelected)
            assertEquals(true, it.mViewModel.selectedProfile.value!!.isSelected)

            // Should be in SettingsActivityTest.kt
            assertEquals(true, it.adapter.settingsProfileList[0].isSelected)
        }

        activityScenario.moveToState(Lifecycle.State.DESTROYED)
    }

    @Test
    fun isSelectionUnique1() {
        val activityScenario = provideEmptyResumedActivity()

        val numberOfRow = 10
        val clickDelay = 100L
        val testCount = 10

        activityScenario.onActivity {
            for (i in 0..numberOfRow)
                it.mViewModel.onClickAdd()
        }

        Thread.sleep(100)
        activityScenario.onActivity {

            it.mViewModel.viewModelScope.launch(Dispatchers.Default) {
                for (i in 0..testCount) {
                    val list = it.mViewModel.profileList.value!!
                    it.mViewModel.onItemClicked(list[(0..numberOfRow).random()])
                    LogManager.tests("Click Delay")
                    delay(clickDelay)

                    var selected = 0
                    for (j in 0..numberOfRow) {
                        selected += it.mViewModel.profileList.value!![j].isSelected
                    }
                    if (selected > 1)
                        LogManager.tests("future fail")

                    GlobalScope.launch(Dispatchers.Main) {
                        assertEquals(1, selected)
                    }
                }
            }
        }

        Thread.sleep(testCount * clickDelay + clickDelay)

        activityScenario.recreate() // Check persistent database

        Thread.sleep(100)
        activityScenario.onActivity {
            it.mViewModel.viewModelScope.launch {
                var selected = 0
                for (i in 0..numberOfRow) {
                    selected += it.mViewModel.profileList.value!![i].isSelected
                }
                assertEquals(1, selected)
            }
        }

        activityScenario.moveToState(Lifecycle.State.DESTROYED)
    }

    @Test
    fun isSelectionUnique2() {
        val activityScenario = provideEmptyResumedActivity()
        val numberOfRow = 10

        activityScenario.onActivity {
            for (i in 0..numberOfRow)
                it.mViewModel.onClickAdd()
        }

        Thread.sleep(100)
        activityScenario.onActivity {
            it.mViewModel.onItemClicked(it.adapter.settingsProfileList[4])
            it.mViewModel.onItemClicked(it.adapter.settingsProfileList[6])
        }

        Thread.sleep(100)
        activityScenario.onActivity {
            var selected = 0
            for (i in 0..numberOfRow) {
                selected += it.mViewModel.profileList.value!![i].isSelected
            }
            assertEquals(1, selected)
        }
    }

    @Test
    fun checkSelectionAfterSelectedDeleted() {
        val activityScenario = provideEmptyResumedActivity()
        val numberOfRow = 10

        activityScenario.onActivity {
            for (i in 0..numberOfRow) {
                it.mViewModel.onClickAdd()
            }

            // Si on utilise 1 call onActivity, les coroutines du view model n'ont pas le temps
            // de se dérouler
        }

        Thread.sleep(100)
        activityScenario.onActivity {
            val list = it.mViewModel.profileList.value!!
            it.mViewModel.onItemClicked(list[numberOfRow / 2])
        }

        Thread.sleep(100)
        activityScenario.onActivity {
            it.mViewModel.viewModelScope.launch {
                val selected = it.mViewModel.selectedProfile.value!!
                it.mViewModel.getRepository().deleteProfile(selected)
                it.mViewModel.refreshData()
            }
        }

        Thread.sleep(100)
        activityScenario.onActivity {
            assertEquals(true, it.mViewModel.profileList.value!![0].isSelected)
        }

        activityScenario.moveToState(Lifecycle.State.DESTROYED)
    }

    fun clearDataBase() {
        runBlocking {
            launch(Dispatchers.Main) {
                globalViewModel.removeAllEntries()
            }
        }
    }

    fun provideEmptyResumedActivity(): ActivityScenario<SettingsActivity> {
        clearDataBase()
        return ActivityScenario.launch(SettingsActivity::class.java)
    }
}

private operator fun Int.plus(v: Boolean): Int {
    return this + if (v) 1 else 0
}
