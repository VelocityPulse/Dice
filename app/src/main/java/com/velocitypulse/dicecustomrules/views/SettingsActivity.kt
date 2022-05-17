package com.velocitypulse.dicecustomrules.views

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.velocitypulse.dicecustomrules.R
import com.velocitypulse.dicecustomrules.adapters.SettingsAdapter
import com.velocitypulse.dicecustomrules.models.entity.SettingsProfile
import com.velocitypulse.dicecustomrules.viewmodels.SettingsActivityViewModel
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private val TAG = "SETTINGS ACTIVITY"

    lateinit var mViewModel: SettingsActivityViewModel
    lateinit var adapter: SettingsAdapter
    lateinit var recycler: RecyclerView

    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        mViewModel = ViewModelProvider(this).get(SettingsActivityViewModel::class.java)

        supportActionBar?.let {
            it.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            it.setDisplayHomeAsUpEnabled(true)
            it.setCustomView(R.layout.action_bar_template)
            it.customView.findViewById<TextView>(R.id.action_bar_title).text = "Profiles"
        }

        initView()
        initObserver()
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch { mViewModel.refreshData() }
    }

    override fun onOptionsItemSelected(iItem: MenuItem): Boolean {
        when (iItem.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(iItem)
    }

    val onCardViewClick: (profile: SettingsProfile) -> Unit = { profile ->
        mViewModel.onItemClicked(profile)
    }

    val onCardViewLongClick: (profile: SettingsProfile) -> Boolean = { profile ->
        true
    }

    val onEditButtonClick: (profile: SettingsProfile) -> Unit = { profile ->
        val i = Intent(this, SettingsProfileActivity::class.java)
        i.putExtra("PROFILE_ID", profile.id)
        startActivity(i)
    }

    private fun initView() {
        recycler = findViewById(R.id.settings_recycler_view)
        recycler.layoutManager = LinearLayoutManager(this)

        SettingsAdapter(
            this, mutableListOf(),
            onCardViewClick, onCardViewLongClick, onEditButtonClick
        ).also {
            recycler.adapter = it
            adapter = it
        }

        fab = findViewById(R.id.settings_fab)
        fab.setOnClickListener { mViewModel.onClickAdd() }
    }

    private fun initObserver() {
        mViewModel.profileList.observe(this) { adapter.setProfileList(it) }
        mViewModel.updatedProfile.observe(this) { adapter.notifyItemChanged(it) }
        mViewModel.selectedProfile.observe(this) { adapter.notifyItemChanged(it) }
        mViewModel.insertedProfile.observe(this) { adapter.notifyItemInserted(it) }
    }
}