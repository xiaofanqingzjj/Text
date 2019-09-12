package com.example.test

import android.app.Activity
import android.util.Log
import java.util.concurrent.LinkedBlockingDeque

object TestLeaks {
    private const val TAG = "TestLeaks"
    private var queque = LinkedBlockingDeque<String>()

    fun test(context: Activity) {
        val someObj = SomeClass(context)
        val obj = queque.take()
        Log.d(TAG, "someObj:$someObj, obj:$obj")
    }
}

class SomeClass(var activity: Activity)