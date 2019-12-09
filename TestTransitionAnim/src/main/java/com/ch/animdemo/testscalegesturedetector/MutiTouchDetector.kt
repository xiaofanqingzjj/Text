package com.ch.animdemo.testscalegesturedetector

import android.util.Log
import android.view.MotionEvent


/**
 * muti touch detector
 *
 * @author fortune
 */
class MutiTouchDetector  {

    companion object {
        const val TAG = "MutiTouchDetector"
    }

    private var mode = 0

    fun onTouch(event: MotionEvent) {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> mode = 1
            MotionEvent.ACTION_UP -> mode = 0

            MotionEvent.ACTION_POINTER_DOWN -> mode += 1
            MotionEvent.ACTION_POINTER_UP -> mode -= 1
        }

        Log.e(TAG, "onTouchEvent:$mode， :action:${event.action and MotionEvent.ACTION_MASK}")
    }


    fun isMutiTouch(): Boolean {
        return mode > 1
    }
}