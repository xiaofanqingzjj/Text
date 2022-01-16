package com.example.opengl.program

import android.content.Context
import android.opengl.GLES20
import com.example.opengl.R
import com.example.opengl.program.ShaderProgram

class ColorShaderProgram(context: Context) : ShaderProgram(
        context,
        R.raw.simple_vertex_shader, R.raw.simple_fragment_shader
) {

    private var uMatrixLocation = 0


    private var aPositionLocation = 0

    private var aColorLocation = 0


    init {
        this.uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        this.aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        this.aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);
    }

    fun setUniforms(matrix: FloatArray?) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
    }


    fun getPositionLocation(): Int {
        return aPositionLocation
    }


    fun getColorLocation(): Int {
        return aColorLocation
    }


}