package com.velocitypulse.dicecustomrules.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.*
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.velocitypulse.dicecustomrules.R
import com.velocitypulse.dicecustomrules.adapters.DiceDescriptionAdapter
import com.velocitypulse.dicecustomrules.core.Utils.hideKeyboard
import com.velocitypulse.dicecustomrules.viewmodels.SettingsProfileActivityViewModel
import kotlinx.coroutines.launch

class SettingsProfileActivity : AppCompatActivity() {

    private val TAG = "SETTINGS PROFILE ACTIVITY"

    private lateinit var mViewModel: SettingsProfileActivityViewModel

    private lateinit var mSongSwitch: SwitchCompat
    private lateinit var mDiceSumSwitch: SwitchCompat
    private lateinit var mDiceNumberPicker: NumberPicker
    private lateinit var mTitleEditText: TextInputEditText
    private lateinit var mTitleInputLayout: TextInputLayout
    private lateinit var mDiceDescriptionSwitch: SwitchCompat
    private lateinit var mDescriptionRecyclerView: RecyclerView

    private lateinit var mAdapter: DiceDescriptionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_profile)

        mViewModel = ViewModelProvider(this).get(SettingsProfileActivityViewModel::class.java)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        findView()
        initView()
        initObservers()
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch { mViewModel.refreshData(intent.getLongExtra("PROFILE_ID", 0L)) }
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

    fun findView() {
        mDiceNumberPicker = findViewById(R.id.dice_number_picker)
        mDiceSumSwitch = findViewById(R.id.dice_sum_switch)
        mSongSwitch = findViewById(R.id.song_switch)
        mTitleEditText = findViewById(R.id.title_input)
        mTitleInputLayout = findViewById(R.id.title_filed)
        mDiceDescriptionSwitch = findViewById(R.id.description_enable)
        mDescriptionRecyclerView = findViewById(R.id.description_recycler_view)
    }

    fun initView() {
        mDiceNumberPicker.setOnValueChangedListener { _, _, newVal ->
            mViewModel.setNumberOfDice(newVal)
        }

        mDiceNumberPicker.apply {
            minValue = 1
            maxValue = 12
        }

        mTitleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mViewModel.setTitle(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        mTitleEditText.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus)
                hideKeyboard(v)
        }

        mDescriptionRecyclerView.layoutManager = LinearLayoutManager(this)

        DiceDescriptionAdapter(
            this,
            mDescriptionRecyclerView,
            mutableMapOf(),
            0,
            onDescriptionTextEdit
        ).also {
            mDescriptionRecyclerView.adapter = it
            mAdapter = it
            it.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus)
                    hideKeyboard(v)
            }
        }


    }

    val onDescriptionTextEdit = DiceDescriptionAdapter.OnTextEditListener { description, position ->
        mViewModel.onDescriptionEdit(description, position)
    }

    private fun initObservers() {
        mViewModel.numberOfDice.observe(this) { num -> mDiceNumberPicker.value = num }
        mViewModel.isDiceSumEnabled.observe(this) { enabled -> mDiceSumSwitch.isChecked = enabled }
        mViewModel.isSongEnabled.observe(this) { enabled -> mSongSwitch.isChecked = enabled }
        mViewModel.title.observe(this) { title -> mTitleEditText.setText(title) }
        mViewModel.finishActivity.observe(this) { if (it) finish() }

        mViewModel.descriptionMap.observe(this) { setDescriptionMap(it) }
        mViewModel.diceDescriptionPickerSize.observe(this) { setDiceDescriptionPickerSize(it) }
        mViewModel.diceDescriptionEnabled.observe(this) { setDiceDescriptionEnabled(it) }
    }

    private fun setDescriptionMap(it: MutableMap<Int, String>) {
        mAdapter.descriptionMap = it
    }

    private fun setDiceDescriptionEnabled(it: Boolean) {
        mDiceDescriptionSwitch.isChecked = it
        mDescriptionRecyclerView.visibility = if (it) VISIBLE else GONE

        // Necessary post because at activity launch, recycler miss information due to its own
        // instantiation process
        mDescriptionRecyclerView.post {
            mAdapter.updateScroll()
        }
//        mAdapter.notifyDataSetChangedInfiniteLoop()
    }

    private fun setDiceDescriptionPickerSize(it: Int) {
        mAdapter.listSize = it
        mAdapter.notifyDataSetChangedInfiniteLoop()
    }

    fun onDiceSumSwitchClick(view: View) {
        mViewModel.setIsDiceSumEnabled((view as SwitchCompat).isChecked)
    }

    fun onSongSwitchClick(view: View) {
        mViewModel.setIsSongEnabled((view as SwitchCompat).isChecked)
    }

    fun onDescriptionSwitchClick(view: View) {
        mViewModel.setIsDiceDescriptionEnabled((view as SwitchCompat).isChecked)
    }

    fun delete() {

        val builder = AlertDialog.Builder(this)
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
