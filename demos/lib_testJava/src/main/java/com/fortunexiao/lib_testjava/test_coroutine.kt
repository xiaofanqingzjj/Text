package com.fortunexiao.lib_testjava

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


suspend fun testSus(): Int {
    // 调用另一个挂起函数
    return testSus1();
}

// 另一个挂起函数
suspend fun testSus1() : Int{
    delay(1000)
    // 通过suspendCoroutine执行盖起操作，并
    return suspendCancellableCoroutine <Int> {
        Thread(Runnable {
            Thread.sleep(1000);
            // 模拟在挂起函数中的耗时操作
            it.resume(20)
        }).start()
    }
}

fun main(args: Array<String>) {
    runBlocking { // 启动一个协程
        // 调用一个挂起函数
        val  r = testSus();
        // 等待挂起函数执行完毕，打印挂起函数返回的结果
        println("suspendResult:$r")
    }
}

// 挂起函数
