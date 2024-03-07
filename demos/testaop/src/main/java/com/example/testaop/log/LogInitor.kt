package com.example.testaop.log

import android.content.Context
import android.os.Environment
import com.example.testaop.BuildConfig
import com.tencent.common.log.TLog
import com.tencent.mars.xlog.Log
import com.tencent.mars.xlog.Xlog

object LogInitor {

    fun init(context: Context) {
        initXlog(context)
    }


    private fun initXlog(context: Context) {
        System.loadLibrary("c++_shared");
        System.loadLibrary("marsxlog");

        val sdcard = Environment.getExternalStorageDirectory().absolutePath;
        val logPath = "$sdcard/marssample/log";

        val cachePath = "${context.filesDir}/xlog"

        //init xlog
        if (BuildConfig.DEBUG) {
            Xlog.appenderOpen(Xlog.LEVEL_DEBUG,
                Xlog.AppednerModeAsync,
                cachePath,
                logPath,
                "MarsSample",
                0,
                "");
            Xlog.setConsoleLogOpen(true);
        } else {
            Xlog.appenderOpen(Xlog.LEVEL_INFO,
                Xlog.AppednerModeAsync,
                cachePath,
                logPath,
                "MarsSample",
                0,
                "");
            Xlog.setConsoleLogOpen(false)
        }
        Log.setLogImp(Xlog());
    }

//    private fun initTlog() {
//        //    public static RefWatcher getRefWatcher() {
//        //        return refWatcher;
//        //    }
//        val debug: Boolean = BuildConfig.DEBUG
//        TLog.enableFileAppender(
//            this, debug,
//            "${this.getExternalFilesDir(null)!!.absolutePath}/log",
//            "main"
//        )
//        if (debug) {
//            Xlog.setLogLevel(Xlog.LEVEL_ALL)
//        }
//
//    }

}