package com.velocitypulse.dicecustomrules

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.velocitypulse.dicecustomrules.viewmodels.MainActivityViewModel
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityViewModelTest : TestCase() {
// Instrumentation Tests
    private lateinit var viewModel: MainActivityViewModel

    @Before
    public override fun setUp() {
        super.setUp()

        viewModel = MainActivityViewModel(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun testMainActivityViewModel() {

    }

    @Test
    fun failingTest() {
        fail()
    }
}