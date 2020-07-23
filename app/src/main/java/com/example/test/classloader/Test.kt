package com.example.test.classloader

import dalvik.system.DexClassLoader
import dalvik.system.DexFile


fun test() {


//    listOf(DexFile)

    val dexFile = DexFile.loadDex("", "", 0)
    dexFile.run {
        val name = name
        loadClass("", ClassLoader.getSystemClassLoader())
        val entries = entries()
        close()
    }


    val dc :DexClassLoader
}