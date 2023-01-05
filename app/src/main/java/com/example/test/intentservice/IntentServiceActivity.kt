package com.example.test.intentservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.test.R
import com.example.test.intentservice.IntentServiceActivity.MyBroadcastReceiver
import com.example.test.intentservice.MyIntentService
import com.example.test.intentservice.IntentServiceActivity

class IntentServiceActivity : AppCompatActivity() {
    /**
     * 状态文字
     */
    var tv_status: TextView? = null

    /**
     * 进度文字
     */
    var tv_progress: TextView? = null

    /**
     * 进度条
     */
    var progressbar: ProgressBar? = null
    private var mLocalBroadcastManager: LocalBroadcastManager? = null
    private var mBroadcastReceiver: MyBroadcastReceiver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intent_service)

        tv_status = findViewById(R.id.tv_status)
        tv_progress = findViewById(R.id.tv_progress)
        progressbar = findViewById(R.id.progressbar)

        findViewById<View>(R.id.btn_start).setOnClickListener {
            val intent = Intent(this@IntentServiceActivity, MyIntentService::class.java)
            startService(intent)
        }


        //注册广播
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this)

        mBroadcastReceiver = MyBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_TYPE_THREAD)
        mLocalBroadcastManager!!.registerReceiver(mBroadcastReceiver!!, intentFilter)
        initView()
    }

    fun initView() {
        tv_status!!.text = "线程状态：未运行"
        progressbar!!.max = 100
        progressbar!!.progress = 0
        tv_progress!!.text = "0%"
    }

    override fun onDestroy() {
        super.onDestroy()
        //注销广播
        mLocalBroadcastManager!!.unregisterReceiver(mBroadcastReceiver!!)
    }

    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_TYPE_THREAD -> {
                    //更改UI
                    val progress = intent.getIntExtra("progress", 0)
                    tv_status!!.text = "线程状态：" + intent.getStringExtra("status")
                    progressbar!!.progress = progress
                    tv_progress!!.text = "$progress%"
                    if (progress >= 100) {
                        tv_status!!.text = "线程结束"
                    }
                }
            }
        }
    }

    companion object {
        const val ACTION_TYPE_THREAD = "action.type.thread"
    }
}