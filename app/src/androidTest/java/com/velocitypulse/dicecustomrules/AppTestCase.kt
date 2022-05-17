package com.velocitypulse.dicecustomrules

import com.velocitypulse.dicecustomrules.core.LogManager
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

open class AppTestCase : TestCase() {

    fun blockingRunOnMainThread(block: suspend () -> Unit) {
        runBlocking {
            launch(Dispatchers.Main) {
                try {
                    block()
                } catch (th: Throwable) {
                    LogManager.tests(th.stackTraceToString())
                    throw th
                }
            }
        }
    }
}