package com.example.opengl

import android.opengl.GLES20
import java.nio.FloatBuffer

object OpenGLUtils {


    /**
     * 根据着色器代码创建着色器
     */
    fun loadShader(type: Int, shaderCode: String): Int {
        // 创建着色器
        // 类型为顶点着色器类型(GLES20.GL_VERTEX_SHADER)
        // 或者是片段着色器类型 (GLES20.GL_FRAGMENT_SHADER)
        val shader = GLES20.glCreateShader(type);

        // 添加着色器代码
        GLES20.glShaderSource(shader, shaderCode);
        // 编译着色器代码
        GLES20.glCompileShader(shader);
        return shader
    }

    fun attachShaders(glProgram: Int,  shaders: List<Int> ) {
        // 把着色器添加到程序中
        shaders.forEach {
            GLES20.glAttachShader(glProgram, it)
        }

        //  链接程序
        GLES20.glLinkProgram(glProgram)
    }


    /**
     * 给GLSL中的向量设值
     */
    fun setGLSLVecVarValue(glProgram: Int, varName: String, varType: VarType, vecSize: Int, stride: Int, buffer: FloatBuffer) {
        // 获取GLSL中的变量
        val varHandle =
            if (varType == VarType.Attribute) {
                GLES20.glGetAttribLocation(glProgram, varName)
            } else {
                GLES20.glGetUniformLocation(glProgram, varName)
            }

        // 启用三角形顶点位置的句柄
        GLES20.glEnableVertexAttribArray(varHandle)

        //准备三角形坐标数据
        GLES20.glVertexAttribPointer(varHandle,
                vecSize,
                GLES20.GL_FLOAT,
                false,
                stride,
                buffer);

    }
}

enum class VarType {
    Attribute, Uniform
}