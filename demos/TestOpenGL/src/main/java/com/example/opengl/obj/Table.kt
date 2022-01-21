package com.example.opengl.obj

import android.opengl.GLES20
import com.example.opengl.program.ColorShaderProgram
import com.example.opengl.program.TextureShaderProgram
import com.example.opengl.util.toFloatBuffer
import java.nio.FloatBuffer

/**
 * 一个要绘制的对象
 */
class Table {


    companion object {
        private val POSITION_COMPONENT_COUNT = 2

        private val TEXTURE_COORDINATES_COMPONENT_COUNT = 2

        // 顶点的步长，这个例子中，步长为4，前俩值为顶点数据， 后俩值为纹理分量
        private val STRIDE: Int = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT

        // 对象的顶点数据
        private val VERTEX_DATA = floatArrayOf(
//X,Y,S,T
                0f, 0f, 0f, 0f,
                -0.5f, -0.8f, 0f, 1f,
                0.5f, -0.8f, 1f, 1f,

                0f, 0f, 0f, 0f,
                -0.5f, 0.8f, 0f, 1f,
                0.5f, 0.8f, 1f, 1f

//                0.5f, 0.8f, 1f, 0.1f,
//                -0.5f, 0.8f, 0f, 0.1f,
//                -0.5f, -0.8f, 0f, 0.9f
        )
    }

    // 把顶点数据转成Buffer
    private var vertexArray: FloatBuffer = VERTEX_DATA.toFloatBuffer()


    /**
     * 通过着色器把顶点数据渲染到openGl
     *
     * 渲染过程如下：
     * ShaderProgram会创建好OpenGL program
     * 并可以返回GLSL的顶点id和颜色id
     * 我们直接使用 glVertexAttribPointer方法把当前数据设置到程序返回的顶点id和颜色id上
     */
    fun bindData(textureProgram: TextureShaderProgram) {

        textureProgram.setVertexPosition(
                vertexArray,
                0,
                POSITION_COMPONENT_COUNT,
                STRIDE
        )
        textureProgram.setTextureCoords(
                vertexArray,
                POSITION_COMPONENT_COUNT,
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE
        )

//        vertexArray.setVertexAttribPointer(
//                0,
//                textureProgram.getPositionLocation(),
//                POSITION_COMPONENT_COUNT,
//                STRIDE)
//        vertexArray.setVertexAttribPointer(
//                POSITION_COMPONENT_COUNT,
//                textureProgram.getTextureLocation(),
//                TEXTURE_COORDINATES_COMPONENT_COUNT,
//                STRIDE)
    }

//    fun bindData(textureProgram: ColorShaderProgram) {
//        vertexArray.setVertexAttribPointer(
//                0,
//                textureProgram.getPositionLocation(),
//                POSITION_COMPONENT_COUNT,
//                STRIDE)
//        vertexArray.setVertexAttribPointer(
//                POSITION_COMPONENT_COUNT,
//                textureProgram.getColorLocation(),
//                TEXTURE_COORDINATES_COMPONENT_COUNT,
//                STRIDE)
//    }


    fun draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6)
    }


}