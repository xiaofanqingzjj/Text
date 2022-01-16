package com.example.opengl.program

import android.content.Context
import android.opengl.GLES20
import com.example.opengl.R
import com.example.opengl.program.ShaderProgram

class TextureShaderProgram(context: Context)
    : ShaderProgram(context,
        R.raw.texture_vertex_shader,
        R.raw.texture_fragment_shader) {

    private var uMatrixLocation = 0
    private var uTextureUnitLocation = 0
    private var aPositionLocation = 0
    private var aTextureCoordinatesLocation = 0

    init {
        this.uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        this.uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);
        this.aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        this.aTextureCoordinatesLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);


    }

    fun setUniforms(matrix: FloatArray?, textureId: Int) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE_2D)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(uTextureUnitLocation, 0)
    }

    fun getPositionLocation(): Int {
        return aPositionLocation
    }


    fun getTextureLocation(): Int {
        return aTextureCoordinatesLocation
    }


}