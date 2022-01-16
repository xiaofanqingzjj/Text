package com.example.opengl.base

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent


/**
 * 一个简单的GLSurfaceView Demo
 */
class MGLSurfaceView(context: Context) : GLSurfaceView(context) {

    interface TouchAble {
        fun onTouchEvent(e: MotionEvent): Boolean
    }

    private var renderer: Renderer? = null

    init {
        setEGLContextClientVersion(2)
    }

    override fun setRenderer(renderer: Renderer?) {
        super.setRenderer(renderer)
        this.renderer = renderer;
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val r = renderer
        if (r is TouchAble) {
            return return r.onTouchEvent(e);
        }
        return super.onTouchEvent(e);

    }
}
