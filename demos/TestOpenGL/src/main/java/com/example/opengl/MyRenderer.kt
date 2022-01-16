package com.example.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.opengl.util.ShaderHelper
import com.example.opengl.util.getRawFileContent
import com.example.opengl.util.toFloatBuffer
import com.example.opengl.obj.Constants.BYTES_PER_FLOAT
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRenderer(var context: Context) : GLSurfaceView.Renderer {

    companion object {
        var tableVerticesWithTriangles = floatArrayOf( //两个三角形和三角形的颜色分量
                0f, 0f, 1f, 1f, 1f,
                -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.5f, 0.7f, 0.7f, 0.7f,

                0.5f, 0.5f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.5f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.5f, 0.7f, 0.7f, 0.7f//,  //两条直线和直线的颜色分量

//                -0.5f, 0f, 1f, 0f, 0f,
//                0.5f, 0f, 1f, 0f, 0f,  //两个顶点和顶点的颜色分量
//
//                0f, -0.25f, 0f, 0f, 1f,
//                0f, 0.25f, 1f, 0f, 0f
        )


    }



    private val A_POSITION = "a_Position"

    private var aPositionLocation = 0

    private val POSITION_COMPONENT_COUNT = 2

    private val COLOR_COMPONENT_COUNT=3;

    private  val A_COLOR = "a_Color";
    private var aColorLocation: Int = 0

    private val STRIDE=(POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;


    val vertexData: FloatBuffer = tableVerticesWithTriangles.toFloatBuffer()

    init {
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 清屏，设置黑色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // 加载GLSL代码
        val vertexShaderSource: String? =  context.getRawFileContent(R.raw.simple_vertex_shader)
        val fragmentShaderSource: String? = context.getRawFileContent(R.raw.simple_fragment_shader)

        Log.d("MyRenderer", "vertex:$vertexShaderSource")
        Log.d("MyRenderer", "fragment:$fragmentShaderSource")

        // 创建着色器
        val vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource!!)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource!!)

        // 创建GL程序，并链接着色器
        val program = ShaderHelper.linkProgram(vertexShader, fragmentShader)
        ShaderHelper.validateProgram(program)

        // 将程序添加OpenGL
        GLES20.glUseProgram(program)


        // 修改GLSL中的变量值
        this.aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        this.vertexData.position(0)
        //
        GLES20.glVertexAttribPointer(aPositionLocation, //GLSL的属性
                POSITION_COMPONENT_COUNT, // 属性分量的大小
                GLES20.GL_FLOAT,
                false,
                STRIDE, // 步长，也就是距离多少取下一个分量，数组的个数*4
                this.vertexData)
        GLES20.glEnableVertexAttribArray(aPositionLocation)


        // 颜色也是一个
        this.aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);
        this.vertexData.position(POSITION_COMPONENT_COUNT);
        GLES20.glVertexAttribPointer(aColorLocation,
                COLOR_COMPONENT_COUNT,
                GLES20.GL_FLOAT,
                false,
                STRIDE,
                this.vertexData);
        GLES20.glEnableVertexAttribArray(aColorLocation);



    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // 设置渲染窗口的大小
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // 绘制顶点
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6)
//        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);
//        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);
//        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);
    }

}