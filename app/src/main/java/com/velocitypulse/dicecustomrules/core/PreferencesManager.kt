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

    @Deprecated("database impl")
    fun setDiceSumEnabled(iContext: Context, iValue: Boolean) {
        val mSharedPreferences = iContext.getSharedPreferences(APP_ADDRESS, Context.MODE_PRIVATE)
        mSharedPreferences.edit().putBoolean(PREFERENCE_ALPHA_NUM_SHOWING, iValue).apply()
    }

    @Deprecated("database impl")
    fun getDiceSumEnabled(iContext: Context): Boolean {
        val mSharedPreferences = iContext.getSharedPreferences(APP_ADDRESS, Context.MODE_PRIVATE)
        return mSharedPreferences.getBoolean(
            PREFERENCE_ALPHA_NUM_SHOWING,
            DEFAULT_ALPHA_NUM_SHOWING
        )
    }

    @Deprecated("database impl")
    fun setSongEnabled(iContext: Context, iValue: Boolean) {
        val mSharedPreferences = iContext.getSharedPreferences(APP_ADDRESS, Context.MODE_PRIVATE)
        mSharedPreferences.edit().putBoolean(PREFERENCE_SONG_ENABLED, iValue).apply()
    }

    @Deprecated("database impl")
    fun getSongEnabled(iContext: Context): Boolean {
        val mSharedPreferences = iContext.getSharedPreferences(APP_ADDRESS, Context.MODE_PRIVATE)
        return mSharedPreferences.getBoolean(PREFERENCE_SONG_ENABLED, DEFAULT_SONG_ENABLED)
    }

    @Deprecated("database impl")
    fun setNumberOfDice(iContext: Context, iValue: Int) {
        val mSharedPreferences = iContext.getSharedPreferences(APP_ADDRESS, Context.MODE_PRIVATE)
        mSharedPreferences.edit().putInt(PREFERENCE_DICE_NUMBER, iValue).apply()
    }

    @Deprecated("database impl")
    fun getNumberOfDice(iContext: Context): Int {
        val mSharedPreferences = iContext.getSharedPreferences(APP_ADDRESS, Context.MODE_PRIVATE)
        return mSharedPreferences.getInt(PREFERENCE_DICE_NUMBER, DEFAULT_DICE_NUMBER)
    }
}