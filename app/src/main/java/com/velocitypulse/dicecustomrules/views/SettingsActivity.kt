package com.velocitypulse.dicecustomrules.views

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.velocitypulse.dicecustomrules.R

class SettingsActivity : AppCompatActivity() {

    private val TAG = "SETTINGS ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.settings_activity)
    }
}