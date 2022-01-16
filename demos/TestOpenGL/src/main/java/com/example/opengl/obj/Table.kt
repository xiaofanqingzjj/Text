package com.example.opengl.obj

import android.opengl.GLES20
import com.example.opengl.program.ColorShaderProgram
import com.example.opengl.program.TextureShaderProgram

class Table {


    companion object {
        private val POSITION_COMPONENT_COUNT = 2

        private val TEXTURE_COORDINATES_COMPONENT_COUNT = 2

        private val STRIDE: Int = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT

        private val VERTEX_DATA = floatArrayOf(
//X,Y,S,T
                0f, 0f, 0.5f, 0.5f,
                -0.5f, -0.8f, 0f, 0.9f,
                0.5f, -0.8f, 1f, 0.9f,
                0.5f, 0.8f, 1f, 0.1f,
                -0.5f, 0.8f, 0f, 0.1f,
                -0.5f, -0.8f, 0f, 0.9f
        )
    }

    private var vertexArray: VertexArray = VertexArray(VERTEX_DATA)


    fun bindData(textureProgram: TextureShaderProgram) {
        vertexArray.setVertexAttribPointer(
                0,
                textureProgram.getPositionLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE)
        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.getTextureLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE)
    }

    fun bindData(textureProgram: ColorShaderProgram) {
        vertexArray.setVertexAttribPointer(
                0,
                textureProgram.getPositionLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE)
        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.getColorLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE)
    }


    fun draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6)
    }


}