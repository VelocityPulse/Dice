package com.velocitypulse.dicecustomrules

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView

class Dice constructor(iFrameView: FrameLayout) {

    private val mFrameLayout: FrameLayout = iFrameView
    private val mDiceShapeMap = Array<ImageView?>(6) { null }
    private var mDiceBackground: ImageView? = null

    private var mDiceValue: Int = 1

    init {
        mDiceShapeMap[0] = mFrameLayout.findViewById(R.id.dice_shape_1)
        mDiceShapeMap[1] = mFrameLayout.findViewById(R.id.dice_shape_2)
        mDiceShapeMap[2] = mFrameLayout.findViewById(R.id.dice_shape_3)
        mDiceShapeMap[3] = mFrameLayout.findViewById(R.id.dice_shape_4)
        mDiceShapeMap[4] = mFrameLayout.findViewById(R.id.dice_shape_5)
        mDiceShapeMap[5] = mFrameLayout.findViewById(R.id.dice_shape_6)
    }

    fun setDiceShape(iNumber: Int) {
        if (mFrameLayout.visibility == View.VISIBLE) {

            for (lItem in mDiceShapeMap) {
                if (lItem!!.visibility == View.VISIBLE)
                    lItem.visibility = View.INVISIBLE
            }

            mDiceShapeMap[iNumber]!!.visibility = View.VISIBLE
            mDiceValue = iNumber + 1
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