package com.velocitypulse.dicecustomrules.views

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.velocitypulse.dicecustomrules.R
import com.velocitypulse.dicecustomrules.core.LogManager
import com.velocitypulse.dicecustomrules.viewmodels.MainActivityViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity() {

    private val TAG = "MAIN ACTIVITY"
    private val ROLLING_UPDATE_SPEED = 10L

    lateinit var mViewModel: MainActivityViewModel

    private val mRandom: Random = Random()

    private lateinit var mDiceViewList: List<DiceView>

    private var mPlayerDiceSong1: MediaPlayer? = null
    private var mPlayerDiceSong2: MediaPlayer? = null

    // TODO Impl
    private var mDiceBackground: ImageView? = null
    private var mAlphaNumericText: TextView? = null

    private var mRollingDiceJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        mViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        lifecycleScope.launchWhenCreated { mViewModel.refreshData() }

        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM

        initView()
        initObserver()
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch { mViewModel.refreshData() }
    }

    fun setSumTextVisibility(visible: Boolean) {
        if (visible)
            mAlphaNumericText?.visibility = View.VISIBLE
        else
            mAlphaNumericText?.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(iMenu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, iMenu)
        return super.onCreateOptionsMenu(iMenu)
    }

    override fun onOptionsItemSelected(iItem: MenuItem): Boolean {
        when (iItem.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(iItem)
    }

    fun initView() {
        LogManager.info(TAG, "Init")

        mDiceViewList = listOf(
            DiceView(findViewById(R.id.dice_1)),
            DiceView(findViewById(R.id.dice_2)),
            DiceView(findViewById(R.id.dice_3)),
            DiceView(findViewById(R.id.dice_4)),
            DiceView(findViewById(R.id.dice_5)),
            DiceView(findViewById(R.id.dice_6)),
            DiceView(findViewById(R.id.dice_7)),
            DiceView(findViewById(R.id.dice_8)),
            DiceView(findViewById(R.id.dice_9)),
            DiceView(findViewById(R.id.dice_10)),
            DiceView(findViewById(R.id.dice_11)),
            DiceView(findViewById(R.id.dice_12)))

        mPlayerDiceSong1 = MediaPlayer.create(this, R.raw.dice_song_1)
        mPlayerDiceSong2 = MediaPlayer.create(this, R.raw.dice_song_2)

        mAlphaNumericText = findViewById(R.id.alpha_numeric_text_view)
    }

    fun initObserver() {
        mViewModel.numberOfDice.observe(this) {
            setDisplayedDices(it)
        }

        mViewModel.isPlayingDiceSong.observe(this) {
            playSong(it)
        }

        mViewModel.diceValues.observe(this) {
            setDiceValues(it)
        }

        mViewModel.diceSum.observe(this) {
            mAlphaNumericText?.text = it.toString()
        }

        mViewModel.isRandomizingDice.observe(this) {
            if (it)
                rollDice()
            else
                mRollingDiceJob?.cancel()
        }

        mViewModel.isDiceSumEnabled.observe(this) {
            setSumTextVisibility(it)
        }
    }

    private fun rollDice() {
        mRollingDiceJob = lifecycleScope.launch {
            while (isActive) {
                for (item in mDiceViewList) {
                    item.setDiceShape(mViewModel.getRandomDiceShape())
                }
                delay(ROLLING_UPDATE_SPEED)
            }
        }
    }

    private fun setDiceValues(list: List<Int>) {
        for (i in list.indices) {
            mDiceViewList[i].setDiceShape(list[i])
        }
    }

    fun setDisplayedDices(numberOfDice: Int) {
        var lIndex: Int = -1

        while (++lIndex < numberOfDice) {
            mDiceViewList[lIndex].setVisibility(View.VISIBLE)
        }
        lIndex--
        while (++lIndex < 12) {
            mDiceViewList[lIndex].setVisibility(View.GONE)
        }
    }

    private fun playSong(id: Int) {
        mPlayerDiceSong1?.seekTo(0)
        mPlayerDiceSong2?.seekTo(0)
        if (id == 1)
            mPlayerDiceSong1?.start()
        else if (id == 2)
            mPlayerDiceSong2?.start()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onScreenClick(iView: View) {
        mViewModel.onDiceClick()
    }
}