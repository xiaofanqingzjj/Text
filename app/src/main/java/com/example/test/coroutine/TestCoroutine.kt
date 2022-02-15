package com.example.test.coroutine

import android.os.Bundle
import android.os.Looper
import android.util.Log
import com.bedrock.module_base.MenuFragment
import com.bedrock.module_base.SimpleFragment
import com.example.test.R
import kotlinx.coroutines.*

import java.util.concurrent.Executors
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TestCoroutine : MenuFragment() {

    companion object {
        const val TAG = "TestCoroutine"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addMenu("runBlocking") {
            Log.e(TAG, "主线程id：${Looper.getMainLooper().thread.id}")
            Log.e(TAG, "主线程id：${Thread.currentThread().id}")
            runBlocking {

//            Log.e(TAG, "协程执行 线程id：${Thread.currentThread().id}")
//            delay(1000)

                repeat(8) {
                    Log.e(TAG, "协程执行$it 线程id：${Thread.currentThread().id}")
                    delay(1000)
                }
            }
            Log.e(TAG, "协程执行结束")
        }

        addMenu("Test2") {
            Log.e(TAG, "主线程id：${Looper.getMainLooper().thread.id}")

            val job = GlobalScope.launch {
                delay(6000)
                Log.e(TAG, "协程执行结束 -- 线程id：${Thread.currentThread().id}")
            }
            Log.e(TAG, "主线程执行结束")

            GlobalScope.launch(context = Dispatchers.Default) {

            }
        }

        addMenu("Test3") {
            async {
                Log.d(TAG, "async:${Thread.currentThread().id}")
            }


        }

        addMenu("CoroutineAPI") {
            runTask(object : SingleMethodCallback {
                override fun onCallBack(value: String) {
                    Log.d(TAG, "result:$value")
                }

            })
        }

        addMenu("suspendCoroutine") {
            GlobalScope.launch {
                val result = runTask2()
                Log.d(TAG, "result:$result");
            }
        }


    }

    /**
     * suspend表示这个函数是一个挂起函数
     *
     * 在哪一行代码开始挂起呢？
     *
     *
     */
    suspend fun runTask2(): String {
        // 这个函数只能在suspend函数中调用，同时我们在回调函数中将结果值传入到Coutination的resume方法中
        return suspendCoroutine<String> {
            // 在这里做耗时
            runTask(object : SingleMethodCallback {
                override fun onCallBack(value: String) {
                    it.resume(value)
                }
            })
        }
    }


    private fun runTask(callback: SingleMethodCallback) {
        thread {
            Thread.sleep(1000)
            callback.onCallBack("result")
        }
    }

    interface SingleMethodCallback {
        fun onCallBack(value: String)
    }



    fun async(task:()->Unit) {
        // sync
//        task.invoke()

        // async by thread pool
        ThreadPool.run {
            task.invoke();
        }


    }
}


