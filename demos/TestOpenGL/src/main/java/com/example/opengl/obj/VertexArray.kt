package com.example.opengl.obj

import android.opengl.GLES20
import com.example.opengl.obj.Constants.BYTES_PER_FLOAT
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class VertexArray(vertexData: FloatArray) {

    private var floatBuffer: FloatBuffer = ByteBuffer.allocateDirect(vertexData.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData)


    /**
     * 把缓冲区设置到着色器上
     *
     * @param dataOffset buffer的开始位置
     * @param attributeLocation GLSL中要设置的变量id
     * @param compontCount
     */
    fun setVertexAttribPointer(dataOffset: Int, attributeLocation: Int, compontCount: Int, stride: Int) {
        floatBuffer.position(dataOffset)
        GLES20.glVertexAttribPointer(
                attributeLocation, // glsl的变量
                compontCount, // 属性分量的大小，也就是把数组的几个值作为一个分量
                GLES20.GL_FLOAT, //
                false,
                stride, // 步长，数组中每格多少取下一个值
                floatBuffer) // buffer对象
        GLES20.glEnableVertexAttribArray(attributeLocation)
        floatBuffer.position(0)
    }


}