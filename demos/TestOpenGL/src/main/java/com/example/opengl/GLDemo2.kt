package com.example.opengl

import android.opengl.GLSurfaceView
import com.example.opengl.base.BaseGLActivity
import com.example.opengl.base.MGLSurfaceView


class GLDemo2 : BaseGLActivity() {
    override fun createSurfaceView(): GLSurfaceView {
        val glSurfaceView = MGLSurfaceView(this)
        glSurfaceView.setRenderer(MyRenderer(this))
        return glSurfaceView
    }

}