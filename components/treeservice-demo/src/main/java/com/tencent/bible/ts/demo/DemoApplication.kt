package com.tencent.bible.ts.demo

import android.app.Application
import android.content.res.Configuration
import com.tencent.bible.ts.RemoteServiceManager
import com.tencent.bible.ts.demo.login.LoginModule
import com.tencent.bible.ts.demo.pay.PayModule
import com.tencent.bible.ts.demo.usermanager.UserManager
import com.tencent.bible.ts.demo.usermanager.UserManagerInitor

/**
 * Created by hugozhong on 2020-01-10
 */

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()


        LoginModule().onCreate(this)
        PayModule().onCreate(this)

        RemoteServiceManager.start(this)


        UserManagerInitor.init();


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