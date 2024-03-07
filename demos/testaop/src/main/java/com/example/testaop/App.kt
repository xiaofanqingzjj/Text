package com.example.testaop

import android.app.Application
import com.example.testaop.log.LogInitor

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        LogInitor.init(this)
    }




}