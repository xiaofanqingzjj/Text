package com.example.opengl.program

import android.opengl.GLES20
import java.nio.FloatBuffer

/**
 * 纹理着色器程序
 *
 * 需要设置要渲染的顶点数据，纹理id，纹理坐标，以及相机视图
 *
 * 用法如下：
 *
 *
 *
 */
class TextureShaderProgram()
    : ShaderProgram(
        VERTEX_SHADER_SCRIPT,
        FRAGMENT_SHADER_SCRIPT) {

    companion object {

        // 顶点着色器脚本
        const val VERTEX_SHADER_SCRIPT = """
            uniform mat4 u_Matrix;
            attribute vec4 a_Position;
            attribute vec2 a_TextureCoordinates;
            varying vec2 v_TextureCoordinates;
            
            void main(){
                v_TextureCoordinates = a_TextureCoordinates;
                gl_Position =   u_Matrix * a_Position;
            }

        """

        // 片段着色器脚本
        const val FRAGMENT_SHADER_SCRIPT = """
            precision mediump float;

            uniform sampler2D u_TextureUnit;
            varying vec2 v_TextureCoordinates;
            
            void main(){
                gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
            }

        """

        // 着色脚本里的变量名
        const val U_MATRIX = "u_Matrix"
        const val U_TEXTURE_UNIT = "u_TextureUnit"
        const val A_POSITION = "a_Position"
        const val A_TEXTURE_COORDINATES = "a_TextureCoordinates"
    }


    // 着色器脚本中的变量的句柄
    private var uMatrixLocation = 0
    private var uTextureUnitLocation = 0
    private var aPositionLocation = 0
    private var aTextureCoordinatesLocation = 0

    init {
        // 获取角色器脚本中变量的句柄
        this.uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        this.uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);
        this.aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);

        // 纹理向量变量 vec2 a_TextureCoordinates
        this.aTextureCoordinatesLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    /**
     * 设置变换矩阵
     */
    fun setMatrix(matrix: FloatArray?) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
    }

    /**
     * 设置纹理id
     */
    fun setTexture(textureId: Int) {
        // 激活纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE_2D)
        // 绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        // 将纹理绑定到GLSL变量上
        GLES20.glUniform1i(uTextureUnitLocation, 0)
    }

    /**
     * 设置需要绘制的顶点Buffer
     */
    fun setVertexPosition(buffer: FloatBuffer, offset: Int, componentCount: Int, stride: Int) {
        buffer.position(0)
        GLES20.glVertexAttribPointer(aPositionLocation,
                componentCount,
                GLES20.GL_FLOAT,
                false,
                stride,
                buffer
        )
        GLES20.glEnableVertexAttribArray(aPositionLocation)
        buffer.position(0)
    }

    /**
     * 设置纹理坐标
     */
    fun setTextureCoords(buffer: FloatBuffer, offset: Int, componentCount: Int, stride: Int) {
        buffer.position(0)
        GLES20.glVertexAttribPointer(aTextureCoordinatesLocation,
                componentCount,
                GLES20.GL_FLOAT,
                false,
                stride,
                buffer
        )
        GLES20.glEnableVertexAttribArray(aTextureCoordinatesLocation)
        buffer.position(0)
    }



}