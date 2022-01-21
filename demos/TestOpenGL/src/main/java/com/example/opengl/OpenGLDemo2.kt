package com.example.opengl

import android.opengl.GLSurfaceView
import com.example.opengl.base.BaseGLActivity
import com.example.opengl.base.MGLSurfaceView


/**
 * 绘制一个简单的图形
 */
class OpenGLDemo2 : BaseGLActivity() {
    override fun createSurfaceView(): GLSurfaceView {
        val glSurfaceView = MGLSurfaceView(this)
        glSurfaceView.setRenderer(MyRenderer2(this))
        return glSurfaceView
    }

}