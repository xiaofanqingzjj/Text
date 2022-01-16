package com.example.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import android.view.MotionEvent
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 * 一个简单的GLSurfaceView Demo
 */
class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private var render: MyRender

    init {
        setEGLContextClientVersion(2)

        render = MyRender()
        setRenderer(render)

        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    private  val TOUCH_SCALE_FACTOR: Float = 180.0f / 320f
    private var previousX: Float = 0f
    private var previousY: Float = 0f

    override fun onTouchEvent(e: MotionEvent): Boolean {

        val x: Float = e.x
        val y: Float = e.y

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {

                var dx: Float = x - previousX
                var dy: Float = y - previousY

                // reverse direction of rotation above the mid-line
                if (y > height / 2) {
                    dx *= -1
                }

                // reverse direction of rotation to left of the mid-line
                if (x < width / 2) {
                    dy *= -1
                }

                render.angle += (dx + dy) * TOUCH_SCALE_FACTOR
                requestRender()
            }
        }

        previousX = x
        previousY = y
        return true
    }

}


class  MyRender : GLSurfaceView.Renderer {

    lateinit var triangle: Triangle
    lateinit var  square: Square


    @Volatile
    var angle: Float = 0f

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    // 定义投影
    // 结果
    private val  mMVPMatrix =  FloatArray(16)

    // 投影
    private val  mProjectionMatrix =  FloatArray(16)

    // 相机
    private val  mViewMatrix =  FloatArray(16)

    private val mRotationMatrix = FloatArray(16)

    init {

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0F, 0F, 0F, 1F)
        triangle = Triangle()
        square = Square()
    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {


        GLES20.glViewport(0, 0, width, height)

        //
        val ratio = width.toFloat() / height.toFloat()

        // 这个投影矩阵被应用于对象坐标在onDrawFrame（）方法中
        //
        // 创建投影矩阵
        Matrix.frustumM(mProjectionMatrix, // 接收投影的矩阵
                0, // 变换投影的起始位置
                -ratio,  // 相对观测点近面的左距离
                ratio, // 相对观测点近面的右距离
                -1f,
                1f,
                1f, // 相对观测点近面距离
                7f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        val scratch = FloatArray(16)




        // Set the camera position (View matrix)
        // 相机视图矩阵
        Matrix.setLookAtM(mViewMatrix,
                0,
                0f,
                0f,
                2.5f, // 眼睛的位置
                0f,
                0f,
                0f,
                0f,
                1.0f,
                0.0f);

        // Calculate the projection and view transformation
        // 投影矩阵和相机矩阵相乘
        Matrix.multiplyMM(mMVPMatrix,
                0,
                mProjectionMatrix,
                0,
                mViewMatrix,
                0);



        // 创建一个旋转矩阵
//        val time = SystemClock.uptimeMillis() % 4000L;
//        val angle = 0.090f * ( time);
//        Log.d("MyRender", "angle:$angle")
        Matrix.setRotateM(mRotationMatrix, 0, angle, 0f, 0f, -1.0f);

        // 将旋转矩阵与投影和相机视图组合在一起
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        // Draw shape
        // 把矩阵给绘制
        triangle.draw(scratch);

    }

}