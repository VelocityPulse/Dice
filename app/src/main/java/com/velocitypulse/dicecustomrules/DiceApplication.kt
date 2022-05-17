package com.velocitypulse.dicecustomrules

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.widget.Toolbar
import com.velocitypulse.dicecustomrules.core.LogManager
import com.velocitypulse.dicecustomrules.core.Utils.updateMargin

class DiceApplication : Application(), Application.ActivityLifecycleCallbacks {

    private val TAG: String = "DICE APPLICATION"

    override fun onCreate() {
        super.onCreate()
        LogManager.info(TAG, "on create")

        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        var statusBarHeight = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        activity.findViewById<Toolbar>(R.id.toolbar).updateMargin(top = statusBarHeight)
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }


}