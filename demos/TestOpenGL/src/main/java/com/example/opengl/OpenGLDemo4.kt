package com.example.opengl

import android.app.Activity
import android.opengl.GLSurfaceView
import android.os.Bundle
import com.example.opengl.base.BaseGLActivity
import com.example.opengl.base.MGLSurfaceView
import com.example.opengl.glsurfaceview.MyBaseGLSurfaceView

/**
 * 把纹理贴在三角形上
 */
class OpenGLDemo4 : Activity() {

    lateinit var gsv: MyBaseGLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_demo4)
//        gsv = MyBaseGLSurfaceView(this)
//        setContentView(gsv)

        gsv = findViewById<MyBaseGLSurfaceView>(R.id.gsv)
        gsv.setEGLContextClientVersion(2)
        gsv.setRenderer(MyRender4(this))
    }

    override fun onPause() {
        super.onPause()
        gsv.onPause()
    }

    override fun onResume() {
        super.onResume()
        gsv.onResume()
    }


//
//    override fun createSurfaceView(): MyBaseGLSurfaceView {
//        val glSurfaceView = MGLSurfaceView(this)
//        glSurfaceView.setRenderer(MyRender4(this))
////        glSurfaceView.renderMode = MyBaseGLSurfaceView.RENDERMODE_WHEN_DIRTY;
//        return glSurfaceView
//    }



}