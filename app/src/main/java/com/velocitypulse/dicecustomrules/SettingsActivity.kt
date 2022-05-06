package com.velocitypulse.dicecustomrules

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.NumberPicker
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.velocitypulse.dicecustomrules.viewmodels.SettingsActivityViewModel

class SettingsActivity : AppCompatActivity() {

    private val TAG = "SETTINGS ACTIVITY"

    private lateinit var viewModel: SettingsActivityViewModel

    private var mSongSwitch: SwitchCompat? = null
    private var mDiceSumSwitch: SwitchCompat? = null
    private var mDiceNumberPicker: NumberPicker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        viewModel = ViewModelProvider(this).get(SettingsActivityViewModel::class.java)

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

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

        viewModel.numberOfDice.value?.let { mDiceNumberPicker?.value = it }
        viewModel.isDiceSumEnabled.value?.let { mDiceSumSwitch?.isChecked = it }
        viewModel.isSongEnabled.value?.let { mSongSwitch?.isChecked = it }
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
            viewModel.setNumberOfDice(newVal)
        }
    }

    private fun initObservers() {
        viewModel.numberOfDice.observe(this) { num -> mDiceNumberPicker?.value = num }
        viewModel.isDiceSumEnabled.observe(this) { enabled -> mDiceSumSwitch?.isChecked = enabled }
        viewModel.isSongEnabled.observe(this) { enabled -> mSongSwitch?.isChecked = enabled }
    }

    fun onDiceSumSwitchClick(view: View) {
        viewModel.setIsDiceSumEnabled((view as SwitchCompat).isChecked)
    }

    fun onSongSwitchClick(view: View) {
        viewModel.setIsSongEnabled((view as SwitchCompat).isChecked)
    }
}
