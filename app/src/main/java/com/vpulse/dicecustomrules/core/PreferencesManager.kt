package com.vpulse.dicecustomrules.core

import android.content.Context

object PreferencesManager {

    const val APP_ADDRESS = "com.vpulse.dicecustomrules"
    const val PREFERENCE_ALPHA_NUM_SHOWING = "PREFERENCE_ALPHA_NUM_SHOWING"

    fun setAlphaNumericShowing(iContext: Context, iValue: Boolean) {
        val mSharedPreferences = iContext.getSharedPreferences(APP_ADDRESS, Context.MODE_PRIVATE);
        mSharedPreferences.edit().putBoolean(PREFERENCE_ALPHA_NUM_SHOWING, iValue).apply();
    }

    fun getAlphaNumericShowing(iContext: Context): Boolean {
        val mSharedPreferences = iContext.getSharedPreferences(APP_ADDRESS, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(PREFERENCE_ALPHA_NUM_SHOWING, false)
    }



}