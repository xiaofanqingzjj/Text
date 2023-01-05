package com.example.test.intentservice

import android.app.IntentService
import android.content.Intent
import com.example.test.intentservice.Logout.e
import com.example.test.intentservice.Logout
import com.example.test.intentservice.IntentServiceActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * IntentService的例子
 *
 * IntentService 内有一个工作线程来处理耗时操作。
 */
class MyIntentService : IntentService("MyIntentService") {

    /**
     * 广播
     */
    //    private LocalBroadcastManager mLocalBroadcastManager;
    init {
        e("MyIntentService")
    }

    /**
     * 是否正在运行
     */
    private var isRunning = false

    /**
     * 进度
     */
    private var count = 0


    override fun onCreate() {
        super.onCreate()
        e("onCreate")
    }

    /**
     * 此方法在工作线程上调用，并具有要处理的请求。
     * 在独立的线程中运行，一次只处理一个Intent，当多次调用startService时，会按队列排序。
     * 当所有请求都被处理完后，IntentService停止自己，所以你不应该调用stopSelf。
     * @param intent
     */
    override fun onHandleIntent(intent: Intent?) {
        e("onHandleIntent")
        try {
            Thread.sleep(1000)
            isRunning = true
            count = 0
            while (isRunning) {
                count++
                if (count >= 100) {
                    isRunning = false
                }
                Thread.sleep(50)
                sendThreadStatus("线程运行中...", count)
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    /**
     * 发送进度消息
     */
    private fun sendThreadStatus(status: String, progress: Int) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(IntentServiceActivity.ACTION_TYPE_THREAD).apply {
            putExtra("status", status)
            putExtra("progress", progress)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        e("线程结束运行...$count")
    }


}