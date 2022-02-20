package com.example.opengl

import android.opengl.GLSurfaceView
import android.os.Bundle
import com.example.opengl.base.BaseGLActivity
import com.example.opengl.glsurfaceview.MyBaseGLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 * 绘制一个简单的三角形
 */
class OpenGLDemo1 : BaseGLActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val glSurfaceView = GLSurfaceView(this);
//        glSurfaceView.setEGLContextClientVersion(2)
//        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
//        glSurfaceView.setRenderer(object : GLSurfaceView.Renderer {
//            override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
//            }
//
//            override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
//            }
//
//            override fun onDrawFrame(gl: GL10?) {
//            }
//        })
//        setContentView(glSurfaceView)
    }

    override fun createSurfaceView(): MyBaseGLSurfaceView {
        return MyGLSurfaceView(this)
    }
}
