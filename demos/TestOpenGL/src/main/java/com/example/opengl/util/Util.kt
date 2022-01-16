package com.example.opengl.util

import android.content.Context
import java.nio.*


/**
 * float数组转成FloatBuffer
 */
fun FloatArray.toFloatBuffer(): FloatBuffer {
    return ByteBuffer.allocateDirect(this.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(this)

}

/**
 * 数组转Buffer
 */
fun IntArray.toShortBuffer(): IntBuffer {
    return  ByteBuffer.allocateDirect(this.size * 4)
            .order(ByteOrder.nativeOrder())
            .asIntBuffer()
            .put(this)
}

/**
 *  数组转Buffer
 */
fun ShortArray.toShortBuffer(): ShortBuffer {
    return ByteBuffer.allocateDirect(this.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .put(this)
}

/**
 * 读取raw文件
 */
fun Context.getRawFileContent(id: Int): String?{
    val inStream = resources.openRawResource(id)
    return String(inStream.readBytes())
}
