package com.velocitypulse.dicecustomrules.views

import android.view.View
import android.view.ViewGroup
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.textfield.TextInputEditText
import com.velocitypulse.dicecustomrules.R
import com.velocitypulse.dicecustomrules.core.LogManager
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SettingsProfileActivityTest : TestCase() {

    @Before
    public override fun setUp() {
        super.setUp()

        LogManager.tests("set up")
    }

    @Test
    fun checkTitleTextFocusability() {
        val activityScenario = launch(SettingsProfileActivity::class.java)

        lateinit var titleEditText: TextInputEditText

        activityScenario.onActivity {
            titleEditText = it.findViewById(R.id.title_input)

        }
        assertEquals(false, titleEditText.isFocused)



        onView(withId(R.id.title_filed)).perform(click())
        assertEquals(true, titleEditText.isFocused)
        onView(withId(R.id.form_layout)).perform(click())
        assertEquals(false, titleEditText.isFocused)

        // Additional tests
        run {
            onView(withId(R.id.title_filed)).perform(click())
            assertEquals(true, titleEditText.isFocused)
            onView(withId(R.id.song_switch)).perform(click())
            assertEquals(false, titleEditText.isFocused)

            onView(withId(R.id.title_filed)).perform(click())
            assertEquals(true, titleEditText.isFocused)
            onView(withId(R.id.number_of_dice_layout)).perform(click())
            assertEquals(false, titleEditText.isFocused)

            onView(withId(R.id.title_filed)).perform(click())
            assertEquals(true, titleEditText.isFocused)
            onView(withId(R.id.number_of_dice_layout)).perform(click())
            assertEquals(false, titleEditText.isFocused)

            onView(withId(R.id.title_filed)).perform(click())
            assertEquals(true, titleEditText.isFocused)
            onView(withId(R.id.number_of_dice_text)).perform(click())
            assertEquals(false, titleEditText.isFocused)
        }
    }
}