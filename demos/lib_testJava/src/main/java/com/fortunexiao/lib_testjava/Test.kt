package com.fortunexiao.lib_testjava

import com.sun.xml.internal.fastinfoset.util.StringArray
import java.util.zip.CRC32

class Test {


}

fun main(args: Array<String>) {



    println("Hello World")

    val str = "Hello world"
    val crc32 = CRC32()
    crc32.update(str.toByteArray())
    println("$str 's crc32 value:${crc32.value % 10}")
}