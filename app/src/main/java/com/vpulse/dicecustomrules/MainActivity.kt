package com.vpulse.dicecustomrules

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
import androidx.core.view.isVisible
import com.vpulse.dicecustomrules.core.LogManager
import com.vpulse.dicecustomrules.core.PreferencesManager
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MAIN ACTIVITY"
        private const val RARITY_FACTOR = 2
        private const val RANDOMIZING_UPDATE_TIME = 10
        private const val THREAD_RANDOMIZING_TIME = 500
    }

    private val mValuesMap = IntArray(6)
    private val mDiceShapeMap = Array<ImageView?>(6) { null }
    private val mRandom = Random()

    private var mPlayerDiceSong1: MediaPlayer? = null
    private var mPlayerDiceSong2: MediaPlayer? = null

    private var mDiceBackground: ImageView? = null
    private var mAlphaNumericText: TextView? = null

    private var mThread: Thread? = null

    private var mTimerDiceUpdate: Long = 0
    private var mTimerThread: Long = 0
    private var mRandomizingDice = false
    private var mUpdateAlphaNumericText = false

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
        mDiceBackground = findViewById(R.id.dice_background)
        mDiceShapeMap[0] = findViewById(R.id.dice_shape_1)
        mDiceShapeMap[1] = findViewById(R.id.dice_shape_2)
        mDiceShapeMap[2] = findViewById(R.id.dice_shape_3)
        mDiceShapeMap[3] = findViewById(R.id.dice_shape_4)
        mDiceShapeMap[4] = findViewById(R.id.dice_shape_5)
        mDiceShapeMap[5] = findViewById(R.id.dice_shape_6)

        mPlayerDiceSong1 = MediaPlayer.create(this, R.raw.dice_song_1)
        mPlayerDiceSong2 = MediaPlayer.create(this, R.raw.dice_song_2)

        mAlphaNumericText = findViewById(R.id.alpha_numeric_text_view)
    }

    fun setDiceShape(iNumber: Int) {

        for (lItem in mDiceShapeMap) {
            if (lItem!!.isVisible)
                lItem.visibility = View.INVISIBLE
        }

        mDiceShapeMap[iNumber]!!.visibility = View.VISIBLE
    }

    fun randomizeDice() {
        if (mRandomizingDice) {
            LogManager.error(TAG, "Already randomizing dice")
            return
        }
        mRandomizingDice = true

        Thread(Runnable {

            mPlayerDiceSong1?.seekTo(0)
            mPlayerDiceSong2?.seekTo(0)
            if (mRandom.nextInt(2) == 0)
                mPlayerDiceSong1?.start()
            else
                mPlayerDiceSong2?.start()

            mTimerDiceUpdate = System.currentTimeMillis()
            mTimerThread = 0
            while (true) {
                val lCurrentTimeMillis: Long = System.currentTimeMillis()
                val lElapsedTime: Long = lCurrentTimeMillis - mTimerDiceUpdate

                if (lElapsedTime > RANDOMIZING_UPDATE_TIME) {

                    runOnUiThread {
                        setDiceShape(mRandom.nextInt(6))
                    }

                    mTimerDiceUpdate = lCurrentTimeMillis
                    mTimerThread += lElapsedTime
                }

                if (mTimerThread > THREAD_RANDOMIZING_TIME) {
                    runOnUiThread {
                        setDiceShape(getExcludedNumberOrRandom())
                    }
                    break
                }

                Thread.sleep(1) // Slow down the while(true)
            }
            mRandomizingDice = false
        }).start()

    }

    private fun getExcludedNumberOrRandom(): Int {
        var lIndex = -1

        while (++lIndex < 6) {
            val lNumberOfShowedIndex = mValuesMap[lIndex]

            for (lTotalList in mValuesMap) {
                if (lTotalList - lNumberOfShowedIndex > RARITY_FACTOR) {
                    LogManager.error(
                        TAG,
                        "Index '" + (lIndex + 1) + "' hasn't been printed since a long time..."
                    )
                    notifyNewDice(lIndex + 1)
                    mValuesMap[lIndex] += 1
                    return lIndex
                }
            }
        }
        val oValue = mRandom.nextInt(6)
        mValuesMap[oValue] += 1
        notifyNewDice(oValue + 1)
        return oValue
    }

    private fun notifyNewDice(iValue: Int) {
        LogManager.info(TAG, "NEW DICE : $iValue")
        if (mUpdateAlphaNumericText) {
            mAlphaNumericText?.text = iValue.toString()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onScreenClick(iView: View) {
        randomizeDice()
    }
}

