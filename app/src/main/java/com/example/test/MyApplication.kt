package com.example.test

import android.app.Application
import android.content.Context
import android.os.Environment
import com.example.test.xlog.XLogInitor
import com.squareup.leakcanary.LeakCanary
import com.tencent.common.log.TLog
import com.tencent.mars.xlog.Log
import com.tencent.mars.xlog.Xlog
import com.tencent.qa.RecordReplayManager

class MyApplication : Application() {

    companion object {
        const val  TAG = "MyApplication"
    }

    override fun onCreate() {
        super.onCreate()


        Log.d(TAG, "onCreate")

        //初始化工作应该和业务的启动初始化放在一起，这里在测试的工程中放这里
//        RecordReplayManager.getInstance().init();

//        initXlog()
//
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your context in this process.
//            return;
//        }
////
//        LeakCanary.refWatcher(this)
//                .listenerServiceClass()
//                .buildAndInstall()
//        LeakCanary.install(this);
//        // Normal context init code...


//        XLogInitor.init(this)

//        initTlog();

    }

    private fun initXlog() {
        System.loadLibrary("c++_shared");
        System.loadLibrary("marsxlog");

        val SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
        val logPath = SDCARD + "/marssample/log";

        val cachePath = "${this.getFilesDir()}/xlog"

//init xlog
        if (BuildConfig.DEBUG) {
            Xlog.appenderOpen(Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync, cachePath, logPath, "MarsSample", 0, "");
            Xlog.setConsoleLogOpen(true);

        } else {
            Xlog.appenderOpen(Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, cachePath, logPath, "MarsSample", 0, "");
            Xlog.setConsoleLogOpen(false);
        }


        Log.setLogImp(Xlog());
    }

    private fun initTlog() {
        //    public static RefWatcher getRefWatcher() {
        //        return refWatcher;
        //    }
        val debug: Boolean = BuildConfig.DEBUG
        TLog.enableFileAppender(
            this, debug,
            "${this.getExternalFilesDir(null)!!.absolutePath}/log",
            "main"
        )
        if (debug) {
            Xlog.setLogLevel(Xlog.LEVEL_ALL)
        }

    }
}