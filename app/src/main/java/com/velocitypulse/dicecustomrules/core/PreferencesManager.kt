package com.velocitypulse.dicecustomrules.core

import android.content.Context

object PreferencesManager {

    const val APP_ADDRESS = "com.vpulse.dicecustomrules"
    const val PREFERENCE_ALPHA_NUM_SHOWING = "PREFERENCE_ALPHA_NUM_SHOWING"
    const val PREFERENCE_SONG_ENABLED = "PREFERENCE_SONG_ENABLED"
    const val PREFERENCE_DICE_NUMBER = "PREFERENCE_DICE_NUMBER"

    const val DEFAULT_SONG_ENABLED = true
    const val DEFAULT_ALPHA_NUM_SHOWING = false
    const val DEFAULT_DICE_NUMBER = 1

    fun setAlphaNumericShowing(iContext: Context, iValue: Boolean) {
        val mSharedPreferences = iContext.getSharedPreferences(APP_ADDRESS, Context.MODE_PRIVATE)
        mSharedPreferences.edit().putBoolean(PREFERENCE_ALPHA_NUM_SHOWING, iValue).apply()
    }

    fun getAlphaNumericShowing(iContext: Context): Boolean {
        val mSharedPreferences = iContext.getSharedPreferences(APP_ADDRESS, Context.MODE_PRIVATE)
        return mSharedPreferences.getBoolean(PREFERENCE_ALPHA_NUM_SHOWING, DEFAULT_ALPHA_NUM_SHOWING)
    }

    fun setSongEnabled(iContext: Context, iValue: Boolean) {
        val mSharedPreferences = iContext.getSharedPreferences(APP_ADDRESS, Context.MODE_PRIVATE)
        mSharedPreferences.edit().putBoolean(PREFERENCE_SONG_ENABLED, iValue).apply()
    }

    fun getSongEnabled(iContext: Context): Boolean {
        val mSharedPreferences = iContext.getSharedPreferences(APP_ADDRESS, Context.MODE_PRIVATE)
        return mSharedPreferences.getBoolean(PREFERENCE_SONG_ENABLED, DEFAULT_SONG_ENABLED)
    }

    fun setDiceNumber(iContext: Context, iValue: Int) {
        val mSharedPreferences = iContext.getSharedPreferences(APP_ADDRESS, Context.MODE_PRIVATE)
        mSharedPreferences.edit().putInt(PREFERENCE_DICE_NUMBER, iValue).apply()
    }

    fun getDiceNumber(iContext: Context): Int {
        val mSharedPreferences = iContext.getSharedPreferences(APP_ADDRESS, Context.MODE_PRIVATE)
        return mSharedPreferences.getInt(PREFERENCE_DICE_NUMBER, DEFAULT_DICE_NUMBER)
    }

}