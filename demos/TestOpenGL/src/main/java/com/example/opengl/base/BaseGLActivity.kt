package com.example.opengl.base

import android.app.Activity
import android.opengl.GLSurfaceView
import android.os.Bundle

/**
 * open gl demo的基类
 */
abstract class BaseGLActivity : Activity() {

    lateinit var glSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glSurfaceView = createSurfaceView()
        setContentView(glSurfaceView)
    }

    abstract fun createSurfaceView() : GLSurfaceView

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }


}