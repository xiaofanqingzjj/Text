package com.example.test.xlog

import android.content.Context
import com.tencent.mars.xlog.Log


object XLogInitor {


    fun init(context: Context) {
//        System.loadLibrary("c++_shared");
//        System.loadLibrary("marsxlog");
//
//        val SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
//        val logPath = SDCARD + "/marssample/log";
//
//// this is necessary, or may crash for SIGBUS
//        val cachePath = "${context.filesDir}/xlog"
//
////init xlog
//        val logConfig =  Xlog.XLogConfig();
//        logConfig.mode = Xlog.AppednerModeAsync;
//        logConfig.logdir = logPath;
//        logConfig.nameprefix = "xlog";
//        logConfig.pubkey = "";
//        logConfig.compressmode = Xlog.ZLIB_MODE;
//        logConfig.compresslevel = 0;
//        logConfig.cachedir = "";
//        logConfig.cachedays = 0;
//        if (BuildConfig.DEBUG) {
//            logConfig.level = Xlog.LEVEL_VERBOSE;
//            Xlog.setConsoleLogOpen(true);
//        } else {
//            logConfig.level = Xlog.LEVEL_INFO;
//            Xlog.setConsoleLogOpen(false);
//        }
//
//        Log.setLogImp(Xlog());

//
//        System.loadLibrary("c++_shared");
//        System.loadLibrary("marsxlog");
//
//        val SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
//        val logPath = SDCARD + "/marssample/log";
//
//// this is necessary, or may crash for SIGBUS
//        val cachePath = "${context.getFilesDir()}/xlog"
//
////init xlog
//        val xlog =  Xlog();
//        Log.setLogImp(xlog);
//
//        if (BuildConfig.DEBUG) {
//            Log.setConsoleLogOpen(true);
//            Log.appenderOpen(Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync, "", logPath, logFileName, 0);
//        } else {
//            Log.setConsoleLogOpen(false);
//            Log.appenderOpen(Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, "", logPath, logFileName, 0);
//        }
    }

    fun close() {
        Log.appenderClose()
    }

//    fun init2() {
//        val SDCARD = Environment.getExternalStorageDirectory().absolutePath
//        val logPath = "$SDCARD/marssample/log"
//
//// this is necessary, or may cash for SIGBUS
//
//// this is necessary, or may cash for SIGBUS
//        val cachePath: String = this.getFilesDir().toString() + "/xlog"
//
////init xlog
//
////init xlog
//        if (BuildConfig.DEBUG) {
//            appenderOpen(Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync, cachePath, logPath, "MarsSample", 0, PUB_KEY)
//            Xlog.setConsoleLogOpen(true)
//        } else {
//            appenderOpen(Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, cachePath, logPath, "MarsSample", 0, PUB_KEY)
//            Xlog.setConsoleLogOpen(false)
//        }
//
//        Log.setLogImp(Xlog())
//    }

}