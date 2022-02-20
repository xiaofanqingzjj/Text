package com.example.opengl

import com.example.opengl.base.BaseGLActivity
import com.example.opengl.base.MGLSurfaceView
import com.example.opengl.glsurfaceview.MyBaseGLSurfaceView


/**
 * 绘制一个简单的图形
 */
class OpenGLDemo2 : BaseGLActivity() {
    override fun createSurfaceView(): MyBaseGLSurfaceView {
        val glSurfaceView = MGLSurfaceView(this)
        glSurfaceView.setRenderer(MyRenderer2(this))
        return glSurfaceView
    }

}