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


    override fun createSurfaceView(): MyBaseGLSurfaceView {
        return MyGLSurfaceView(this)
    }
}
