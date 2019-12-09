package com.example.test

import android.app.Application
import com.squareup.leakcanary.LeakCanary

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()


        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your context in this process.
            return;
        }
//
//        LeakCanary.refWatcher(this)
//                .listenerServiceClass()
//                .buildAndInstall()
//        LeakCanary.install(this);
//        // Normal context init code...

    }
}