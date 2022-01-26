package com.example.camera

import android.app.Activity
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import com.example.opengl.base.MGLSurfaceView
import com.example.opengl.util.ShaderHelper
import com.example.opengl.util.toFloatBuffer
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 * 使用SurfaceTexture渲染相机数据
 *
 * 使用SurfaceTexture接收相机数据, 然后通过OpenGL渲染SurfaceTexture中的纹理
 *
 */
class DisplayCameraBySurfaceTextureOpenGL : Activity() {

    lateinit var glSurfaceView: GLSurfaceView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glSurfaceView = MGLSurfaceView(this);

        // 使用OpenGL ES 2.0
        glSurfaceView.setEGLContextClientVersion(2)

        glSurfaceView.setRenderer(MyRenderer(glSurfaceView));
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY



        setContentView(glSurfaceView)
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

    /**
     * 渲染器
     */
    class MyRenderer(var glSurfaceView: GLSurfaceView) : GLSurfaceView.Renderer {

        companion object {
            const val VERTEXT_SHADER = """
                attribute vec2 inputTextureCoordinate;
                attribute vec4 a_Position;
                varying   vec2 textureCoordinate;
                
                void main()
                 {
                     gl_Position = a_Position;
                     textureCoordinate = inputTextureCoordinate;
                 }

            """
            const val FRAGMENT_SHADER = """
                #extension GL_OES_EGL_image_external : require
                precision mediump float;
                // 外部纹理是如何关联到这个纹理上的？
                uniform samplerExternalOES videoTex;
                varying vec2 textureCoordinate;
                
                void main()
                {
                    vec4 tc = texture2D(videoTex, textureCoordinate);
                    //float color = tc.r * 0.3 + tc.g * 0.59 + tc.b * 0.11;
                    gl_FragColor = vec4(tc.r,tc.g,tc.b,1.0);
                }

            """

            private val mPosCoordinate = floatArrayOf(
                    -1f, -1f,
                    -1f, 1f,
                    1f, -1f,
                    1f, 1f)

            private val mTexCoordinateBackRight = floatArrayOf(
                    1f, 1f,
                    0f, 1f,
                    1f, 0f,
                    0f, 0f) //顺时针转90并沿Y轴翻转  后摄像头正确，前摄像头上下颠倒


            /**
             * 创建一个OpenGL纹理对象返回纹理句柄
             * @return
             */
            fun createOESTextureObject(): Int {
                val tex = IntArray(1)
                //生成一个纹理
                GLES20.glGenTextures(1, tex, 0)

                //将此纹理绑定到外部纹理上
                GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0])

                //设置纹理过滤参数
                GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST.toFloat())
                GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
                GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE.toFloat())
                GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE.toFloat())

                //解除纹理绑定
                GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
                return tex[0]
            }
        }

        // 创建OpenGL程序
        var program : Int = 0

        // 顶点数据Buffer
        private val mPosBuffer: FloatBuffer = mPosCoordinate.toFloatBuffer()
        private val mTexBuffer: FloatBuffer = mTexCoordinateBackRight.toFloatBuffer();

        private lateinit var mCamera: Camera

        private var surfaceTexture: SurfaceTexture? = null

        init {

        }

        var position = 0;
        var textureCoods = 0

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            // 清屏幕
            GLES20.glClearColor(1f, 0f, 0f, 0f)

            // 创建程序
            program = ShaderHelper.buildProgram(VERTEXT_SHADER, FRAGMENT_SHADER)
            GLES20.glUseProgram(program)

            // 渲染顶点,把顶点数据设置到GLSL脚本的a_Position属性
             position = GLES20.glGetAttribLocation(program, "a_Position")
            // 在使用之前一定要记得position到0
            mPosBuffer.position(0)
            GLES20.glVertexAttribPointer(
                    position,
                    2,
                    GLES20.GL_FLOAT,
                    false,
                    0,
                    mPosBuffer
            )


            // 设置纹理渲染坐标
             textureCoods = GLES20.glGetAttribLocation(program, "inputTextureCoordinate")
            mTexBuffer.position(0)
            GLES20.glVertexAttribPointer(
                    textureCoods,
                    2,
                    GLES20.GL_FLOAT,
                    false,
                    0,
                    mTexBuffer
            )

            GLES20.glEnableVertexAttribArray(position)
            GLES20.glEnableVertexAttribArray(textureCoods)



            // 创建纹理对象
            val textureId = createOESTextureObject()
            // 通过纹理对象创建SurfaceTexture
            surfaceTexture = SurfaceTexture(textureId)

            // 监听SurfacdeTexture的帧数据
            surfaceTexture?.setOnFrameAvailableListener {
                // 监听到帧后更新纹理
                glSurfaceView.requestRender()
            }



            CameraHelper.openAndPreview(surfaceTexture!!)

//            // 获取相机对象，并设置参数
//            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
//            mCamera.setDisplayOrientation(90)
//
//            val parameters = mCamera.getParameters()
//            parameters["orientation"] = "portrait"
//            parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
//            parameters.setPreviewSize(1280, 720)
//            mCamera.setParameters(parameters)
//
//            // 开始读取相机里的数据
//            // SurfaceTexture对象可以把相机里的每一帧Buffer数据转换成纹理数据
//
//            // 开始读取相机里的数据
//            // SurfaceTexture对象可以把相机里的每一帧Buffer数据转换成纹理数据
//            try {
//                mCamera.setPreviewTexture(surfaceTexture)
//                mCamera.startPreview()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }


        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            //
            GLES20.glViewport(0, 0, width, height)
        }

        override fun onDrawFrame(gl: GL10?) {

            //todo 执行渲染工作
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

            surfaceTexture?.updateTexImage()
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mPosCoordinate.size / 2)
        }
    }

}

