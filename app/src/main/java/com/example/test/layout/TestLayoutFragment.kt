package com.example.test.layout

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.bedrock.module_base.SimpleFragment
import com.example.test.DownloadProgressBarButton
import com.example.test.R
import kotlinx.android.synthetic.main.fragment_test_layout.*

class TestLayoutFragment : SimpleFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_test_layout)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbtn.mState = DownloadProgressBarButton.STATE_NORMAL

        var count = 0

        btn.setOnLongTouchListener {
            Log.d(TAG, "onTick")
            btn.text = "${count++}"
            true
        }

        btn.setOnClickListener {
            Log.d(TAG, "onClick")
        }

        btn.setOnLongClickListener {
            Log.d(TAG, "setOnLongClickListener")
            false
        }


    }
}

const val TAG = "LongTouch"



fun View.setOnLongTouchListener(initTime: Long = 1000, tickDuration:Long = 100, longTick: ()->Boolean) {

    val tickTask = object: Runnable{
        override fun run() {
            if (longTick()) {
                postDelayed(this, tickDuration)
            }
        }
    }

    setOnTouchListener { v, event ->
        Log.d(TAG, "action:${event.action}")

        if (event.action == MotionEvent.ACTION_DOWN) {
            postDelayed(tickTask, initTime)
        } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            removeCallbacks(tickTask)
        }
        false
    }
}


