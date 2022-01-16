package com.example.opengl.obj

import android.opengl.GLES20
import com.example.opengl.program.ColorShaderProgram

class Mallet {

    companion object {
        private const val POSITION_COMPONENT_COUNT = 2;

        private const val COLOR_COMPONENT_COUNT = 3;

        private const val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;

        private val VERTEX_DATA = floatArrayOf(
                0f, -0.4f, 0f, 0f, 1f,
                0f, 0.4f, 1f, 0f, 0f
        )
    }

    private var vertexArray: VertexArray

    init {
        vertexArray = VertexArray(VERTEX_DATA)
    }


    fun bindData(colorProgram: ColorShaderProgram) {
        vertexArray!!.setVertexAttribPointer(
                0,
                colorProgram.getPositionLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE)
        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                colorProgram.getColorLocation(),
                COLOR_COMPONENT_COUNT,
                STRIDE)
    }


    fun draw() {
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 2)
    }
}