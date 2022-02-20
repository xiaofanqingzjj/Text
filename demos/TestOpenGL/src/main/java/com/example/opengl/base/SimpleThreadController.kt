package com.example.opengl.base

import android.util.Log

class SimpleThreadController {


    companion object {
        const val  TAG = "SimpleThreadController";
    }

    var glThread: GLThread? = null

    fun start() {
        if (glThread == null) {
            glThread = GLThread(lock);
        }
        glThread?.start()
    }

    fun parse() {
        val thread = glThread ?: return

        synchronized(lock) {
            // 唤醒所有线程

            thread.state = 1
            lock.notifyAll()
            // 等待线程暂停成功
//            while (thread.state)
        }
    }

    fun resume() {
        synchronized(lock) {

            glThread?.state = 0
            lock.notifyAll()
        }
    }

    fun exit() {

    }


    private  val lock:Object = Object()

    class GLThread(var lock: Object) : Thread() {

//        var requestParse = false
//        var requestResume = false;

        // 请求状态
        var requstState: Int = 0;

        // 状态：暂停1，执行0，退出2
        var state: Int = 0;

        override fun run() {
            super.run()
            while (true) {

                synchronized(lock) {
                    while (state == 1) { // 请求暂停标志
                        lock.wait()
                    }
                }



                Log.d(TAG," render frame");
                Thread.sleep(1000)
            }
        }

    }
}