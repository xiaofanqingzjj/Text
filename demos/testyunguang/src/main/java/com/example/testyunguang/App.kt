package com.example.testyunguang

import android.app.Application
import com.fortunexiao.tktx.BuildConfig
import com.tencent.mars.xlog.Log
import com.tencent.mars.xlog.Xlog

class App : Application() {

    companion object {
        const val  TAG = "AppTag"
    }

    override fun onCreate() {
        super.onCreate()

        initXlog()
    }

    private fun initXlog() {

        // 加载so
        System.loadLibrary("c++_shared");
        System.loadLibrary("marsxlog");

        // 配置xlog
        val rootPath = getExternalFilesDir(null)!!.absolutePath
        val logPath = "$rootPath/log";
        val cachePath = "$rootPath/xlog_cache"
        android.util.Log.d(TAG, "cacheDir:$cachePath, logDir:$logPath")

        val config = Xlog.XLogConfig().apply {
            this.level = Xlog.LEVEL_VERBOSE // 要打印的日志级别
            this.mode = Xlog.AppednerModeAsync // 日志写入模式
            this.cachedir = cachePath // 缓存目录，当 logDir 不可写时候会写进这个目录，可选项，不选用请给 ""， 如若要给，建议给应用的 /data/data/packname/files/log 目录。
            this.logdir = logPath  // 真正的日志，后缀为 .xlog
            this.nameprefix = "log" // 日志文件名的前缀
            this.cachedays = 0 // 一般情况下填0即可。非0表示会在 _cachedir 目录下存放几天的日志。
            this.pubkey = "" // 加密的公钥
            this.compressmode = Xlog.ZLIB_MODE //
            this.compresslevel = 0
        }

        if (BuildConfig.DEBUG) {
            config.level = Xlog.LEVEL_VERBOSE

            // 设置上面的配置
            Xlog.appenderOpen(config)

            // 是否在LogCat上输出日志
            Xlog.setConsoleLogOpen(true);

        } else {
            config.level = Xlog.LEVEL_INFO
            Xlog.appenderOpen(config);
            Xlog.setConsoleLogOpen(false);
        }

        // Log的实现改成Xlog的实现
        Log.setLogImp(Xlog());
    }
}