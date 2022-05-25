package com.velocitypulse.dicecustomrules.views

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
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
    lateinit var mAdapter: SettingsAdapter
    lateinit var mRecycler: RecyclerView

    private lateinit var mFab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        mViewModel = ViewModelProvider(this).get(SettingsActivityViewModel::class.java)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

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
        mRecycler = findViewById(R.id.settings_recycler_view)
        mRecycler.layoutManager = LinearLayoutManager(this)

        SettingsAdapter(
            this, mutableListOf(),
            onCardViewClick, onCardViewLongClick, onEditButtonClick
        ).also {
            mRecycler.adapter = it
            mAdapter = it
        }

        mFab = findViewById(R.id.settings_fab)
        mFab.setOnClickListener { mViewModel.onClickAdd() }

        setupFabMargin()
    }

    private fun setupFabMargin() {
        val resources: Resources = resources
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            mFab.layoutParams = (mFab.layoutParams as ConstraintLayout.LayoutParams).apply {
                val navBarSize = resources.getDimensionPixelSize(resourceId)
                setMargins(0, 0, rightMargin, navBarSize + rightMargin)
            }
        }
    }

    private fun initObserver() {
        mViewModel.profileList.observe(this) { mAdapter.setProfileList(it) }
        mViewModel.updatedProfile.observe(this) { mAdapter.notifyItemChanged(it) }
        mViewModel.selectedProfile.observe(this) { mAdapter.notifyItemChanged(it) }
        mViewModel.insertedProfile.observe(this) { mAdapter.notifyItemInserted(it) }
    }
}