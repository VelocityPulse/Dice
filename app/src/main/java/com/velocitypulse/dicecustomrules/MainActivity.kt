package com.velocitypulse.dicecustomrules

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.velocitypulse.dicecustomrules.core.LogManager
import com.velocitypulse.dicecustomrules.core.PreferencesManager
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MAIN ACTIVITY"
        private const val RARITY_FACTOR = 2
        private const val RANDOMIZING_UPDATE_TIME = 10
        private const val THREAD_RANDOMIZING_TIME = 500
    }

    private val mOneDiceValueMap: IntArray = IntArray(6)
    private val mRandom: Random = Random()

    private val mDiceList: MutableList<Dice> = ArrayList()

    private var mPlayerDiceSong1: MediaPlayer? = null
    private var mPlayerDiceSong2: MediaPlayer? = null

    private var mDiceBackground: ImageView? = null
    private var mAlphaNumericText: TextView? = null
    private var mThread: Thread? = null

    private var mDiceNumber: Int = 1
    private var mTimerDiceUpdate: Long = 0
    private var mTimerThread: Long = 0
    private var mRandomizingDice = false

    private var mUpdateAlphaNumericText = false
    private var mSongEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.action_bar_no_title)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lAttributes = window.attributes
            lAttributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        init()
    }

    override fun onResume() {
        if (PreferencesManager.getAlphaNumericShowing(this)) {
            mAlphaNumericText?.visibility = View.VISIBLE
            mUpdateAlphaNumericText = true
        } else {
            mAlphaNumericText?.visibility = View.GONE
            mUpdateAlphaNumericText = false
        }

        mSongEnabled = PreferencesManager.getSongEnabled(this)

        mDiceNumber = PreferencesManager.getDiceNumber(this)
        updateDicesDisplayed()
        notifyNewDiceSum()
        super.onResume()
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

    fun init() {
        LogManager.info(TAG, "Init")

        mDiceList.add(Dice(findViewById(R.id.dice_1)))
        mDiceList.add(Dice(findViewById(R.id.dice_2)))
        mDiceList.add(Dice(findViewById(R.id.dice_3)))
        mDiceList.add(Dice(findViewById(R.id.dice_4)))
        mDiceList.add(Dice(findViewById(R.id.dice_5)))
        mDiceList.add(Dice(findViewById(R.id.dice_6)))
        mDiceList.add(Dice(findViewById(R.id.dice_7)))
        mDiceList.add(Dice(findViewById(R.id.dice_8)))
        mDiceList.add(Dice(findViewById(R.id.dice_9)))
        mDiceList.add(Dice(findViewById(R.id.dice_10)))
        mDiceList.add(Dice(findViewById(R.id.dice_11)))
        mDiceList.add(Dice(findViewById(R.id.dice_12)))

        mPlayerDiceSong1 = MediaPlayer.create(this, R.raw.dice_song_1)
        mPlayerDiceSong2 = MediaPlayer.create(this, R.raw.dice_song_2)

        mAlphaNumericText = findViewById(R.id.alpha_numeric_text_view)
    }

    fun updateDicesDisplayed() {
        var lIndex: Int = -1

        while (++lIndex < mDiceNumber) {
            mDiceList[lIndex].setVisibility(View.VISIBLE)
        }
        lIndex--
        while (++lIndex < 12) {
            mDiceList[lIndex].setVisibility(View.GONE)
        }
    }

    fun randomizeDice() {
        if (mRandomizingDice) {
            LogManager.error(TAG, "Already randomizing dice")
            return
        }
        mRandomizingDice = true

        Thread(Runnable {

            playSong()

            mTimerDiceUpdate = System.currentTimeMillis()
            mTimerThread = 0
            while (true) {
                val lCurrentTimeMillis: Long = System.currentTimeMillis()
                val lElapsedTime: Long = lCurrentTimeMillis - mTimerDiceUpdate

                if (lElapsedTime > RANDOMIZING_UPDATE_TIME) {

                    runOnUiThread {
                        for (lItem in mDiceList) {
                            lItem.setDiceShape(mRandom.nextInt(6))
                        }
                    }

                    mTimerDiceUpdate = lCurrentTimeMillis
                    mTimerThread += lElapsedTime
                }

                if (mTimerThread > THREAD_RANDOMIZING_TIME) {
                    runOnUiThread {
                        if (mDiceNumber == 1)
                            mDiceList[0].setDiceShape(getExcludedNumberOrRandom())
                        else {
                            for (lItem in mDiceList) {
                                lItem.setDiceShape(mRandom.nextInt(6))
                            }
                        }
                        notifyNewDiceSum()
                    }
                    break
                }

                Thread.sleep(10) // Slow down the while(true)
            }
            mRandomizingDice = false
        }).start()

    }

    private fun getExcludedNumberOrRandom(): Int {
        var lIndex = -1

        while (++lIndex < 6) {
            val lNumberOfShowedIndex = mOneDiceValueMap[lIndex]

            for (lTotalList in mOneDiceValueMap) {
                if (lTotalList - lNumberOfShowedIndex > RARITY_FACTOR) {
                    LogManager.error(
                        TAG,
                        "Index '" + (lIndex + 1) + "' hasn't been printed since a long time..."
                    )
                    mOneDiceValueMap[lIndex] += 1
                    return lIndex
                }
            }
        }
        val oValue = mRandom.nextInt(6)
        mOneDiceValueMap[oValue] += 1
        return oValue
    }

    private fun notifyNewDiceSum() {

        var lSumValue = 0

        for (lItem in mDiceList) {
            lSumValue += lItem.getDiceValue()
        }

        LogManager.info(TAG, "NEW DICE : $lSumValue")
        if (mUpdateAlphaNumericText) {
            mAlphaNumericText?.text = lSumValue.toString()
        }
    }

    private fun playSong() {
        if (mSongEnabled) {
            mPlayerDiceSong1?.seekTo(0)
            mPlayerDiceSong2?.seekTo(0)
            if (mRandom.nextInt(2) == 0)
                mPlayerDiceSong1?.start()
            else
                mPlayerDiceSong2?.start()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onScreenClick(iView: View) {
        randomizeDice()
    }
}

