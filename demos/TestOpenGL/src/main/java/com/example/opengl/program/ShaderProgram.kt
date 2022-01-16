package com.example.opengl.program

import android.content.Context
import android.opengl.GLES20
import com.example.opengl.util.ShaderHelper
import com.example.opengl.util.getRawFileContent


open class ShaderProgram(context: Context, vertexShaderResourceId: Int, fragmentShaderReourceId: Int) {
    companion object {
        const val U_MATRIX = "u_Matrix";

        const val U_TEXTURE_UNIT = "u_TextureUnit";

        const val A_POSITION = "a_Position";

        const val A_COLOR = "a_Color";

        const val A_TEXTURE_COORDINATES = "a_TextureCoordinates";
    }

    protected var program: Int = 0

    init {
        this.program = ShaderHelper.buildProgram(
                context.getRawFileContent(vertexShaderResourceId),
                context.getRawFileContent(fragmentShaderReourceId))
    }

    fun useProgram() {
        GLES20.glUseProgram(program)
    }


}