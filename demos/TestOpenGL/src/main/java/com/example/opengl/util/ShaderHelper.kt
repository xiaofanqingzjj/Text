package com.example.opengl.util

import android.opengl.GLES20
import android.util.Log


object ShaderHelper {

    const val TAG = "ShaderHelper"


    /**
     * 根据glsl创建顶点着色器
     */
    fun compileVertexShader(shaderCode: String): Int {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);
    }

    /**
     * 根据glsl创建片段着色器
     */
    fun compileFragmentShader(shaderCode: String): Int {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);
    }


    /**
     * 根据着色器代码创建编译好的
     */
    private fun compileShader(type: Int, shaderCode: String): Int {
        // 创建着色器
        // 类型为顶点着色器类型(GLES20.GL_VERTEX_SHADER)
        // 或者是片段着色器类型 (GLES20.GL_FRAGMENT_SHADER)
        val shader = GLES20.glCreateShader(type)

        // 添加着色器代码
        GLES20.glShaderSource(shader, shaderCode)
        // 编译着色器代码
        GLES20.glCompileShader(shader)
        return shader
    }

    /**
     * 根据着色器创建gl程序
     */
    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShaderId)
        GLES20.glAttachShader(program, fragmentShaderId)
        GLES20.glLinkProgram(program)
        return program
    }

    /**
     * 验证程序是否合法
     */
    fun validateProgram(programObjectId: Int): Boolean {
        GLES20.glValidateProgram(programObjectId)
        val validateStatus = IntArray(1)
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0)
        Log.v(TAG, GLES20.glGetProgramInfoLog(programObjectId))
        return validateStatus[0] != 0
    }

    /**
     * 根据着色器代码创建程序对象
     */
    fun buildProgram(vertexShaderSource: String?, fragmentShaderSource: String?): Int {
        val program: Int
        val vertexShader = compileVertexShader(vertexShaderSource!!)
        val fragmentShader = compileFragmentShader(fragmentShaderSource!!)
        program = linkProgram(vertexShader, fragmentShader)
        validateProgram(program)
        return program
    }
}