package com.tencent.bible.ts.demo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by hugozhong on 2020-01-13
 */
class ThirdActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ThirdActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG,"onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG,"onDestroy")
    }
}