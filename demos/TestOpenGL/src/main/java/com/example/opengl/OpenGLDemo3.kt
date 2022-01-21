package com.example.opengl

import android.opengl.GLSurfaceView
import com.example.opengl.base.BaseGLActivity
import com.example.opengl.base.MGLSurfaceView

/**
 * 把纹理贴在三角形上
 */
class OpenGLDemo3 : BaseGLActivity() {
    override fun createSurfaceView(): GLSurfaceView {
        val glSurfaceView = MGLSurfaceView(this)
        glSurfaceView.setRenderer(MyRenderer3(this))
        return glSurfaceView
    }

}