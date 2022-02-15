package com.tencent.bible.ts.demo.annoaidl


class Test {

    fun test() {

        val foo = AnnoAidlManager.getRemoteService(IFoo::class.java)
    }
}