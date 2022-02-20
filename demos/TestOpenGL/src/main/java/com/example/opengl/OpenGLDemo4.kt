package com.example.opengl

import com.example.opengl.base.BaseGLActivity
import com.example.opengl.base.MGLSurfaceView
import com.example.opengl.glsurfaceview.MyBaseGLSurfaceView

/**
 * 把纹理贴在三角形上
 */
class OpenGLDemo4 : BaseGLActivity() {
    override fun createSurfaceView(): MyBaseGLSurfaceView {
        val glSurfaceView = MGLSurfaceView(this)
        glSurfaceView.setRenderer(MyRender4(this))
        return glSurfaceView
    }

}