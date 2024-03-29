package com.velocitypulse.dicecustomrules.core

import android.util.Log

object LogManager {
    private const val UNKNOWN_TAG = "UNKNOWN_TAG"
    private const val ENABLED_LOG = true
    private const val TAG = "CORE - Log Manager"
    private const val TAG_PREFIX = "DICE LOG : "
    private var sSilentDebugLog = false
    private var isJunitTestMode: Boolean? = null

    fun setSilentDebugLog(iValue: Boolean) {
        sSilentDebugLog = iValue
    }

    fun error(iMessage: String): Boolean {
        if (ENABLED_LOG)
            error(UNKNOWN_TAG, iMessage)
        return false
    }

    fun error(iTAG: String, iMessage: String): Boolean {
        if (ENABLED_LOG)
            Log.e(TAG_PREFIX, "$iTAG : $iMessage")
        return false
    }

    fun info(iMessage: String) {
        if (ENABLED_LOG)
            info(UNKNOWN_TAG, iMessage)
    }

    fun info(iTAG: String, iMessage: String) {
        if (ENABLED_LOG)
            Log.i(TAG_PREFIX, "$iTAG : $iMessage")
    }

    fun debug(iMessage: String) {
        if (ENABLED_LOG && !sSilentDebugLog)
            debug(UNKNOWN_TAG, iMessage)
    }

    fun debug(iTag: String, iMessage: String) {
        if (ENABLED_LOG && !sSilentDebugLog)
            Log.d(TAG_PREFIX, "$iTag : $iMessage")
    }

    fun verbose(iMessage: String) {
        if (ENABLED_LOG)
            verbose(UNKNOWN_TAG, iMessage)
    }

    fun verbose(iTag: String, iMessage: String) {
        if (ENABLED_LOG)
            Log.v(TAG_PREFIX, "$iTag : $iMessage")
    }

    fun tests(iTag: String, iMessage: String) {
        if (isTestMode())
            println("$iTag : $iMessage")
    }

    fun tests(iMessage: String) {
        if (isTestMode())
            println(iMessage)
    }

    private fun isTestMode(): Boolean {
        if (isJunitTestMode == null) {
            isJunitTestMode = try {
                Class.forName("com.velocitypulse.dicecustomrules.MainActivityViewModelTest")
                true
            } catch (e: Exception) {
                false
            }
        }
        return isJunitTestMode as Boolean
    }
}