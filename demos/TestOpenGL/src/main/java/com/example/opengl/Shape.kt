package com.example.opengl

import android.opengl.GLES20
import com.example.opengl.util.toFloatBuffer
import com.example.opengl.util.toShortBuffer
import java.nio.FloatBuffer
import java.nio.ShortBuffer


class Triangle() {

    companion object {
        const val COORDS_PER_VERTEX = 3

        // 一组一纬数组表示的顶点，每3个值表示一个顶点
        val triangleCoords = floatArrayOf(
//                0.0f, 0.5f, 0.0f, // top
//                -0.5f, -0.5f, 0.0f, // bottom left
//                0.5f, -0.5f, 0.0f,

                0.0f, 1f, 0.0f,      // top
                -1f, -1f, 0.0f,    // bottom left
                1f, -1f, 0.0f

//                0.0f, -1f, 0f,
//                0.0f, 0.5f, 0.0f, // top
//                -0.5f, -0.5f, 0.0f // bottom left

//                -1f, -0.8f, 0f,
//                -0.8f, -1f, 0f,
//                -1f, -1f, 0f,
//
//                1f, -0.8f, 0f,
//                0.8f, -1f, 0f,
//                1f, -1f, 0f,
//
//                -1f, 0.8f, 0f,
//                -0.8f, 1f, 0f,
//                -1f, 1f, 0f,
//
//                1f, 0.8f, 0f,
//                0.8f, 1f, 0f,
//                1f, 1f, 0f
        )

        var color = floatArrayOf(
                1.0f, 0f, 0f, 1.0f
//                0f, 1.0f, 0f, 1.0f,
//                0f, 0f, 1.0f, 1.0f
        )


        /**
         * 顶点着色器GLSL
         */
        private val vertexShaderCode = """
            uniform mat4 uMVPMatrix; // 4纬矩阵  
            attribute vec4 vPosition;  // 4纬向量
            
            varying vec4 vColor;
            
            attribute vec4 aColor;
            
            void main() {
                // 顶点着色，只是设置了一下gl_Position?
                gl_Position = uMVPMatrix * vPosition;
                vColor = aColor;
            }
        """.trimIndent()

        /**
         * 片段着色器GLSL
         */
        private val fragmentShaderCode = """
            precision mediump float;
            varying vec4 vColor;
            void main(){
                // 片段着色器设置颜色
                gl_FragColor = vColor;
            }
        """.trimIndent()
    }

    /**
     * 顶点数据
     */
    private var vertexBuffer: FloatBuffer = triangleCoords.toFloatBuffer() // 把顶点数据转换成Buffer
    private var colorBuffer: FloatBuffer = color.toFloatBuffer()


    /**
     * OpenGL程序
     */
    private var glProgram: Int


    private val vertexCount = triangleCoords.size / COORDS_PER_VERTEX;
    private val vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // 一些操作GLSL变量的句柄
    private var mPositionHandle: Int = 0
    private var mColorHandle: Int = 0
    private var mMVPMatrixHandle = 0


    init {

        // 根据GLSL创建着色器对象
        val vertexShader = OpenGLUtils.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = OpenGLUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        // 创建OpenGL程序对象
        glProgram = GLES20.glCreateProgram().apply {
            // 把着色器添加到程序中
            GLES20.glAttachShader(this, vertexShader)
            GLES20.glAttachShader(this, fragmentShader)

            //  链接程序
            GLES20.glLinkProgram(this)
        }

    }


    /**
     * 绘制当前的图形
     */
    fun draw(mvpMatrix: FloatArray) {

        // 将程序添加OpenGL
        GLES20.glUseProgram(glProgram)

        // 设置矩阵变换
        mMVPMatrixHandle = GLES20.glGetUniformLocation(glProgram, "uMVPMatrix");
        // 将投影和视图转换传递给着色器
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle,
                1,
                false,
                mvpMatrix,
                0);


        // 给vPosition变量赋值
        vertexBuffer.position(0)
        OpenGLUtils.setGLSLVecVarValue(glProgram,
                "vPosition",
                VarType.Attribute,
                COORDS_PER_VERTEX,
                vertexStride,
                vertexBuffer
        );



        // 获取片段着色器的颜色的句柄
        mColorHandle = GLES20.glGetUniformLocation(glProgram, "vColor");

        colorBuffer.position(0)
        // 设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, colorBuffer);


        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES20.glGetAttribLocation(glProgram, "aColor");
        //设置绘制三角形的颜色
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle,
                4,
                GLES20.GL_FLOAT,
                false,
                0,
                colorBuffer)



        // 绘制顶点数据
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);


        // 禁用顶点数组
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }


}


class TestTexture {

    init {
        GLES20.glGenTextures(1, null)
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D)
    }
}

class Square() {

    companion object {


        // number of coordinates per vertex in this array
        const val COORDS_PER_VERTEX = 3
        var squareCoords = floatArrayOf(
                -0.5f,  0.5f, 0.0f,      // top left
                -0.5f, -0.5f, 0.0f,      // bottom left
                0.5f, -0.5f, 0.0f,      // bottom right
                0.5f,  0.5f, 0.0f       // top right
        )

        /**
         * 顶点着色器GLSL
         */
        private val vertexShaderCode = """
            uniform mat4 uMVPMatrix; // 4纬矩阵  
            attribute vec4 vPosition;  // 4纬向量
            
            varying vec4 vColor;
            
            attribute vec4 aColor;
            
            void main() {
                // 顶点着色，只是设置了一下gl_Position?
                gl_Position = uMVPMatrix * vPosition;
                vColor = aColor;
            }
        """.trimIndent()

        /**
         * 片段着色器GLSL
         */
        private val fragmentShaderCode = """
            precision mediump float;
            varying vec4 vColor;
            void main(){
                gl_FragColor = vColor;
            }
        """.trimIndent()

    }

    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices

    private var vertexBuffer: FloatBuffer = squareCoords.toFloatBuffer()
    private var drawListBuffer: ShortBuffer = drawOrder.toShortBuffer()

    private var program: Int = 0


    init {

        val vertexShader = OpenGLUtils.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)



    }

}