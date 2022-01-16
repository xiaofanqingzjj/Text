package com.fortunexiao.lib_testjava

import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel


fun main(args: Array<String>) {
    println("Hello world")

//    val f = File("test.txt")
//    f.writeText("aa")
//
//    val content = f.readText()
//    println("file content:$content")


    //1.创建一个RandomAccessFile（随机访问文件）对象，
    val raf = RandomAccessFile("test.txt", "rw")

    //通过RandomAccessFile对象的getChannel()方法。FileChannel是抽象类。
    val inChannel: FileChannel = raf.channel



//    //创建一个写数据缓冲区对象
//    val writeBuf: ByteBuffer = ByteBuffer.allocate(48)
//
//    //写入数据到Buf
//    writeBuf.put("filechannel test".toByteArray())
//    writeBuf.flip()
//    inChannel.write(writeBuf)
//    inChannel.


    //2.创建一个读数据缓冲区对象
    val readBuf: ByteBuffer = ByteBuffer.allocate(raf.length().toInt())

    //3.从通道中读取数据到readBuf
    var bytesRead: Int = inChannel.read(readBuf)

    println("readBytes:${bytesRead}")

//    readBuf.asCharBuffer().toString()

    if (bytesRead != -1) {
//        readBuf.flip()
        val content = String(readBuf.array())
        println("readContent:$content")
    }

//
//    while (bytesRead != -1) {
//
//        //Buffer有两种模式，写模式和读模式。在写模式下调用flip()之后，Buffer从写模式变成读模式。
//        readBuf.flip()
//
////        //如果还有未读内容
////        while (readBuf.hasRemaining()) {
//////            readBuf.char
////            println(readBuf.char)
////        }
//        val readStr = readBuf.asCharBuffer().toString()
//        println("readStr:$readStr")
//        //清空缓存区
//        readBuf.clear()
//
//        bytesRead = inChannel.read(readBuf)
//    }

    //关闭RandomAccessFile（随机访问文件）对象
    raf.close()
}