package com.google.mlkit.codelab.objectdetection

import android.app.Application

class MyApplication : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer(this)
    }
}