package com.example.test.xlog

import android.content.Context
import android.os.Environment
import com.example.test.BuildConfig
import com.tencent.mars.xlog.Log
import com.tencent.mars.xlog.Xlog


object XLogInitor {


    fun init(context: Context) {
        System.loadLibrary("c++_shared")
        System.loadLibrary("marsxlog")

        val SDCARD = Environment.getExternalStorageDirectory().absolutePath
        val logPath = "$SDCARD/marssample/log"


        // this is necessary, or may crash for SIGBUS
        val cachePath: String = context.filesDir.toString() + "/xlog"


        //init xlog
        val logConfig = Xlog.XLogConfig()


        // 文件写入模式
        logConfig.mode = Xlog.AppednerModeAsync

        // 日志写入目录
        logConfig.logdir = logPath

        // 日志文件名的前缀
        logConfig.nameprefix = "logFileName"

        // 加密所用的 pub_key，具体可参考 Xlog 加密指引。
        logConfig.pubkey = ""


        logConfig.compressmode = Xlog.ZLIB_MODE
        logConfig.compresslevel = 0

        // 缓存目录，当 logDir 不可写时候会写进这个目录，可选项，不选用请给 ""， 如若要给，建议给应用的 /data/data/packname/files/log 目录。
        logConfig.cachedir = ""

        // 一般情况下填0即可。非0表示会在 _cachedir 目录下存放几天的日志。
        logConfig.cachedays = 0

        if (BuildConfig.DEBUG) {


            logConfig.level = Xlog.LEVEL_VERBOSE

            // 是否会把日志打印到 logcat 中， 默认不打印。
            Xlog.setConsoleLogOpen(true)
        } else {
            logConfig.level = Xlog.LEVEL_INFO

            Xlog.setConsoleLogOpen(false)
        }

        Xlog.appenderOpen(logConfig)

        // 设置 Log 的具体实现，这里必须调用 Log.setLogImp(new Xlog()); 日志才会写到 Xlog 中。
        Log.setLogImp(Xlog())
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