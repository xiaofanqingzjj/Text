package com.example.test.coroutine

import java.util.concurrent.Executor
import java.util.concurrent.Executors

object ThreadPool {

    val executors: Executor = Executors.newFixedThreadPool(1);

    fun run(task: ()->Unit) {
        executors.execute { task.invoke() }
    }
}