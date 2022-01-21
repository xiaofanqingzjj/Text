package com.example.opengl

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.opengl.base.BaseGLActivity


/**
 * 绘制一个简单的三角形
 */
class OpenGLDemo1 : BaseGLActivity() {

    override fun createSurfaceView(): GLSurfaceView {
        return MyGLSurfaceView(this)
    }
}
