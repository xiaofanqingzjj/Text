package com.example.opengl.program

import android.opengl.GLES20
import com.example.opengl.util.ShaderHelper


/**
 * 着色器程序
 *
 * 根据顶点着色器脚本和片段着色器脚本创建一个OpenGL program，并编译好Program
 *
 * 这个类返回的programId是可以直接被使用的，只需在绘制的时候调用glUseProgram(programId)
 *
 * @param vertexShaderCode 顶点着色器脚本
 * @param fragmentShaderCode 片段着色器脚本
 */
open class ShaderProgram constructor(
        vertexShaderCode: String,
        fragmentShaderCode: String) {

    /**
     * 着色器程序的句柄
     */
    var program: Int = 0

    init {
        // 根据顶点着色器和片段着色器创建OpenGL程序
        this.program = ShaderHelper.buildProgram(
                vertexShaderCode,
                fragmentShaderCode)
    }

    /**
     * 启用openGL程序
     */
    fun useProgram() {
        GLES20.glUseProgram(program)
    }
}