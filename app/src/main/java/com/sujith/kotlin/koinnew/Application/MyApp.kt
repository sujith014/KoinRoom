package com.sujith.kotlin.koinnew.Application

import android.app.Application
import com.sujith.kotlin.koinnew.di.module
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApp)
            modules(module)
            androidLogger(level = Level.ERROR)
        }

    }
}