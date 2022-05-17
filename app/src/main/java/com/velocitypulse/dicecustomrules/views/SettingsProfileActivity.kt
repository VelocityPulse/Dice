package com.velocitypulse.dicecustomrules.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.velocitypulse.dicecustomrules.R
import com.velocitypulse.dicecustomrules.viewmodels.SettingsProfileActivityViewModel
import kotlinx.coroutines.launch

class SettingsProfileActivity : AppCompatActivity() {

    private val TAG = "SETTINGS PROFILE ACTIVITY"

    private lateinit var mViewModel: SettingsProfileActivityViewModel

    private lateinit var mSongSwitch: SwitchCompat
    private lateinit var mDiceSumSwitch: SwitchCompat
    private lateinit var mDiceNumberPicker: NumberPicker
    private lateinit var mTitleEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_profile_activity)

        mViewModel = ViewModelProvider(this).get(SettingsProfileActivityViewModel::class.java)

        supportActionBar?.let {
            it.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            it.setCustomView(R.layout.action_bar_template)
            it.customView.findViewById<TextView>(R.id.action_bar_title).text = "Settings"
            it.customView.findViewById<ImageButton>(R.id.home_button).setOnClickListener { finish() }
        }

        initView()
        initObservers()
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch { mViewModel.refreshData(intent.getLongExtra("PROFILE_ID", 0L)) }

        mDiceNumberPicker.let {
            it.minValue = 1
            it.maxValue = 12
        }

        mViewModel.numberOfDice.value?.let { mDiceNumberPicker.value = it }
        mViewModel.isDiceSumEnabled.value?.let { mDiceSumSwitch.isChecked = it }
        mViewModel.isSongEnabled.value?.let { mSongSwitch.isChecked = it }
        mViewModel.title.value?.let { mTitleEditText.setText(it) }
    }

    override fun onCreateOptionsMenu(iMenu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_menu, iMenu)
        return super.onCreateOptionsMenu(iMenu)
    }

    override fun onOptionsItemSelected(iItem: MenuItem): Boolean {
        when (iItem.itemId) {
            android.R.id.home -> finish()
            R.id.action_delete -> delete()
        }
        return super.onOptionsItemSelected(iItem)
    }

    fun initView() {
        mDiceNumberPicker = findViewById(R.id.dice_number_picker)
        mDiceSumSwitch = findViewById(R.id.dice_sum_switch)
        mSongSwitch = findViewById(R.id.song_switch)
        mTitleEditText = findViewById(R.id.title_input)

        mDiceNumberPicker.setOnValueChangedListener { _, _, newVal ->
            mViewModel.setNumberOfDice(newVal)
        }

        mTitleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mViewModel.setTitle(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun initObservers() {
        mViewModel.numberOfDice.observe(this) { num -> mDiceNumberPicker.value = num }
        mViewModel.isDiceSumEnabled.observe(this) { enabled -> mDiceSumSwitch.isChecked = enabled }
        mViewModel.isSongEnabled.observe(this) { enabled -> mSongSwitch.isChecked = enabled }
        mViewModel.title.observe(this) { title -> mTitleEditText.setText(title) }
        mViewModel.finishActivity.observe(this) { if (it) finish() }
    }

    fun onDiceSumSwitchClick(view: View) {
        mViewModel.setIsDiceSumEnabled((view as SwitchCompat).isChecked)
    }

    fun onSongSwitchClick(view: View) {
        mViewModel.setIsSongEnabled((view as SwitchCompat).isChecked)
    }

    fun delete() {

        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Androidly Alert")
        builder.setMessage("Delete profile?")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            mViewModel.deleteProfile()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }
}
