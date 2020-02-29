package com.velocitypulse.dicecustomrules

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.NumberPicker
import android.widget.Switch
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.velocitypulse.dicecustomrules.core.PreferencesManager

class SettingsActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SETTINGS ACTIVITY"
    }

    var mDiceNumberPicker: NumberPicker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setCustomView(R.layout.action_bar_settings)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lAttributes = window.attributes
            lAttributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }

    override fun onResume() {
        super.onResume()
        mDiceNumberPicker = findViewById(R.id.dice_number_picker)
        mDiceNumberPicker!!.setOnValueChangedListener { _, _, newVal ->
            PreferencesManager.setDiceNumber(this, newVal)
        }
        mDiceNumberPicker!!.minValue = 1
        mDiceNumberPicker!!.maxValue = 12
        mDiceNumberPicker!!.value = PreferencesManager.getDiceNumber(this)

        val lAlphaNumericSwitch = findViewById<Switch>(R.id.alpha_numeric_switch)
        val lSongSwitch = findViewById<Switch>(R.id.song_switch)

        if (PreferencesManager.getAlphaNumericShowing(this))
            lAlphaNumericSwitch.isChecked = true
        if (PreferencesManager.getSongEnabled(this))
            lSongSwitch.isChecked = true
    }

    override fun onOptionsItemSelected(iItem: MenuItem): Boolean {
        when (iItem.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(iItem)
    }

    fun onAlphaNumericClick(iView: View) {
        iView as Switch

        PreferencesManager.setAlphaNumericShowing(this, iView.isChecked)
    }

    fun onSongClick(iView: View) {
        iView as Switch

        PreferencesManager.setSongEnabled(this, iView.isChecked)
    }
}
