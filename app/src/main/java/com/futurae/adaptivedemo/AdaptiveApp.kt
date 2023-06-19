package com.futurae.adaptivedemo

import android.app.Application
import com.futurae.sdk.adaptive.AdaptiveSDK
import timber.log.Timber

class AdaptiveApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AdaptiveSDK.INSTANCE.init(this)
        Timber.plant(Timber.DebugTree())
    }
}