package com.tencent.bible.ts.demo

import android.app.Application
import android.content.res.Configuration
import com.tencent.bible.ts.TreeServiceManager
import com.tencent.bible.ts.demo.login.LoginModule
import com.tencent.bible.ts.demo.pay.PayManager
import com.tencent.bible.ts.demo.pay.PayModule

/**
 * Created by hugozhong on 2020-01-10
 */

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()


        LoginModule().onCreate(this)
        PayModule().onCreate(this)

        TreeServiceManager.start(this)


    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}