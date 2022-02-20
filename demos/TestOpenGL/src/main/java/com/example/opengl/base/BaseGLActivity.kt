package com.example.opengl.base

import android.app.Activity
import android.os.Bundle
import com.example.opengl.glsurfaceview.MyBaseGLSurfaceView

/**
 * open gl demo的基类
 */
abstract class BaseGLActivity : Activity() {

    lateinit var glSurfaceView: MyBaseGLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glSurfaceView = createSurfaceView()
        setContentView(glSurfaceView)
    }

    abstract fun createSurfaceView() : MyBaseGLSurfaceView

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }


}