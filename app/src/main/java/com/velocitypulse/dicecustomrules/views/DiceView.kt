package com.velocitypulse.dicecustomrules.views

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.velocitypulse.dicecustomrules.R

class DiceView constructor(iFrameView: FrameLayout) {

    private val mFrameLayout: FrameLayout = iFrameView
    private val mDiceShapeMap: List<ImageView>
    private var mDiceBackground: ImageView? = null

    private var mDiceValue: Int = 1

    init {
        mFrameLayout.apply {
            mDiceShapeMap = arrayListOf(
                findViewById(R.id.dice_shape_1),
                findViewById(R.id.dice_shape_2),
                findViewById(R.id.dice_shape_3),
                findViewById(R.id.dice_shape_4),
                findViewById(R.id.dice_shape_5),
                findViewById(R.id.dice_shape_6)
            )
        }

        for (i in mDiceShapeMap.indices) {
            if (mDiceShapeMap[i].visibility == View.VISIBLE) {
                mDiceValue = i + 1
                break
            }
        }
    }

    /**
     * TODO kdoc & dokka
     */
    fun setDiceShape(iNumber: Int) {
        if (mFrameLayout.visibility == View.VISIBLE) {

            for (lItem in mDiceShapeMap) {
                if (lItem.visibility == View.VISIBLE)
                    lItem.visibility = View.INVISIBLE
            }

            mDiceShapeMap[iNumber - 1].visibility = View.VISIBLE
            mDiceValue = iNumber // from 1 to 6
        }
    }

    fun getDiceValue(): Int {
        if (mFrameLayout.visibility == View.VISIBLE)
            return mDiceValue
        return 0
    }

    fun setVisibility(iVisibility: Int) {
        mFrameLayout.visibility = iVisibility
    }
}