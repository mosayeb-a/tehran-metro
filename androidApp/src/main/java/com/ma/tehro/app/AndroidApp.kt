package com.ma.tehro.app

import android.app.Application
import com.ma.tehro.di.initKoin
import org.koin.android.ext.koin.androidContext

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@AndroidApp)
        }
    }
}