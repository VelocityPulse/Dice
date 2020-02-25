package com.cpulse.dicecustomrules

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.cpulse.dicecustomrules.core.LogManager
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MAIN ACTIVITY"
        private const val RANDOMIZING_UPDATE_TIME = 10
        private const val THREAD_RANDOMIZING_TIME = 500
    }

    private val mValuesMap: List<Int> = ArrayList()
    private val mRandom: Random = Random()

    private var mDiceShape: ImageView? = null
    private var mDiceBackground: ImageView? = null

    private var mTimerDiceUpdate: Long = 0
    private var mTimerThread: Long = 0
    private var mRandomizingDice = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lAttributes = window.attributes
            lAttributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        init()
    }

    fun init() {
        LogManager.info(TAG, "Init")

        mDiceShape = findViewById(R.id.dice_shape)
        mDiceBackground = findViewById(R.id.dice_background)
    }

    fun setDiceShape(iNumber: Int) {
        LogManager.info(TAG, "Set dice shape")
        val lNumber = iNumber % 7

        if (mDiceShape == null) {
            LogManager.error(TAG, "mDiceShape not initialized")
            return
        }

        LogManager.info(TAG, "Set image resources : $lNumber")
        when (lNumber) {
            0 -> mDiceShape!!.setImageResource(R.drawable.dice_1)
            1 -> mDiceShape!!.setImageResource(R.drawable.dice_2)
            2 -> mDiceShape!!.setImageResource(R.drawable.dice_3)
            3 -> mDiceShape!!.setImageResource(R.drawable.dice_4)
            4 -> mDiceShape!!.setImageResource(R.drawable.dice_5)
            5 -> mDiceShape!!.setImageResource(R.drawable.dice_6)
        }
    }

    fun randomizeDice() {
        LogManager.debug(TAG, "RandomizeDice")

        if (mRandomizingDice) {
            LogManager.error(TAG, "Already randomizing dice")
            return
        }
        mRandomizingDice = true

        Thread(Runnable {
            mTimerDiceUpdate = System.currentTimeMillis()
            mTimerThread = 0
            while (true) {
                val lCurrentTimeMillis: Long = System.currentTimeMillis()
                val lElapsedTime: Long = lCurrentTimeMillis - mTimerDiceUpdate

                if (lElapsedTime > RANDOMIZING_UPDATE_TIME) {

                    this.runOnUiThread {
                        setDiceShape(mRandom!!.nextInt(6))
                    }

                    mTimerDiceUpdate = lCurrentTimeMillis
                    mTimerThread += lElapsedTime
                }

                if (mTimerThread > THREAD_RANDOMIZING_TIME)
                    break

                Thread.sleep(1) // Slow down the while(true)
            }
            mRandomizingDice = false
        }).start()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onScreenClick(iView: View) {
        LogManager.debug(TAG, "On screen click")
        randomizeDice()
    }

}

