package com.ma.tehro.app

import android.app.Application
import com.ma.tehro.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.osmdroid.config.Configuration

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Configuration.getInstance().userAgentValue = packageName

        initKoin {
            androidContext(this@AndroidApp)
        }

    }
}