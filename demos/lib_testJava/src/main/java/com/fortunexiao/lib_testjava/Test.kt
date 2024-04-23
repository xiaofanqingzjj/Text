package com.fortunexiao.lib_testjava



import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun main(args: Array<String>) {
    testCoroutine()
}

fun testCoroutine() {

    // launch启动一个协程，并运行lambda里的代码
    GlobalScope.launch {
        printCurrentThreadName()
//        delay(1000)
        println("World")

        val  r = testSuspend1();
        println("suspendResult:$r")
    }
    printCurrentThreadName()
    println("Hello ");
    Thread.sleep(2000)

}

suspend fun testSuspend1(): Int {
    delay(100);
    return testSuspend();
}

suspend fun testSuspend() : Int{
    // 如果函数内没有挂起操作，则函数的suspend会被提示无效

    // 通过suspendCoroutine执行盖起操作，并
    return suspendCancellableCoroutine {

        println("it:${it::class.java}")

        // 挂起函数里面的代码是在哪执行的呢？

        Thread(Runnable {
            printCurrentThreadName()
            Thread.sleep(1000);
            it.resume(20)
        }).start()

    }

}

fun printCurrentThreadName() {
    val name = Thread.currentThread().name
    println("currentThreadName:$name");
}