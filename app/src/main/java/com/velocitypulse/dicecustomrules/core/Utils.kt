package com.velocitypulse.dicecustomrules.core

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.Px
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

object Utils {

    private val TAG = "UTILS"

    fun View.updateMargin(
        @Px left: Int = marginLeft,
        @Px top: Int = marginTop,
        @Px right: Int = marginRight,
        @Px bottom: Int = marginBottom
    ) {
        try {
            (layoutParams as MarginLayoutParams).setMargins(
                left,
                top,
                right,
                bottom
            )
        } catch (th: Throwable) {
            LogManager.error(TAG, "updateMargin crashing :\n" + th.stackTraceToString())
        }
    }

}