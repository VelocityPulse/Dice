package com.velocitypulse.dicecustomrules.views

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.NumberPicker
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.velocitypulse.dicecustomrules.R
import com.velocitypulse.dicecustomrules.viewmodels.SettingsProfileActivityViewModel

class SettingsProfileActivity : AppCompatActivity() {

    private val TAG = "SETTINGS PROFILE ACTIVITY"

    private lateinit var mViewModelProfile: SettingsProfileActivityViewModel

    private var mSongSwitch: SwitchCompat? = null
    private var mDiceSumSwitch: SwitchCompat? = null
    private var mDiceNumberPicker: NumberPicker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_profile_activity)

        mViewModelProfile = ViewModelProvider(this).get(SettingsProfileActivityViewModel::class.java)

        supportActionBar?.let {
            it.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            it.setDisplayHomeAsUpEnabled(true)
            it.setCustomView(R.layout.action_bar_settings)
        }

        initView()
        initObservers()
    }

    override fun onResume() {
        super.onResume()

        mDiceNumberPicker?.let {
            it.minValue = 1
            it.maxValue = 12
        }

        mViewModelProfile.numberOfDice.value?.let { mDiceNumberPicker?.value = it }
        mViewModelProfile.isDiceSumEnabled.value?.let { mDiceSumSwitch?.isChecked = it }
        mViewModelProfile.isSongEnabled.value?.let { mSongSwitch?.isChecked = it }
    }

    override fun onOptionsItemSelected(iItem: MenuItem): Boolean {
        when (iItem.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(iItem)
    }

    fun initView() {
        mDiceNumberPicker = findViewById(R.id.dice_number_picker)
        mDiceSumSwitch = findViewById(R.id.dice_sum_switch)
        mSongSwitch = findViewById(R.id.song_switch)

        mDiceNumberPicker!!.setOnValueChangedListener { _, _, newVal ->
            mViewModelProfile.setNumberOfDice(newVal)
        }
    }

    private fun initObservers() {
        mViewModelProfile.numberOfDice.observe(this) { num -> mDiceNumberPicker?.value = num }
        mViewModelProfile.isDiceSumEnabled.observe(this) { enabled -> mDiceSumSwitch?.isChecked = enabled }
        mViewModelProfile.isSongEnabled.observe(this) { enabled -> mSongSwitch?.isChecked = enabled }
    }

    fun onDiceSumSwitchClick(view: View) {
        mViewModelProfile.setIsDiceSumEnabled((view as SwitchCompat).isChecked)
    }

    fun onSongSwitchClick(view: View) {
        mViewModelProfile.setIsSongEnabled((view as SwitchCompat).isChecked)
    }
}
