package com.velocitypulse.dicecustomrules

import android.app.Application
import com.velocitypulse.dicecustomrules.core.LogManager

class DiceApplication : Application() {

    private val TAG: String = "DICE APPLICATION"

    override fun onCreate() {
        super.onCreate()
        LogManager.info(TAG, "on create")
    }

}