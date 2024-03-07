package com.example.testaop

import com.example.testaop.aop.TimeConsume

class Foo2 {

    @TimeConsume
    fun testFoo(name: String, age: Int, isAnim: Boolean): Int {

        return 40
    }
}