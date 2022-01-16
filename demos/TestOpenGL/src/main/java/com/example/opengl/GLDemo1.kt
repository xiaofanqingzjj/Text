package com.example.opengl

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.opengl.base.BaseGLActivity

class GLDemo1 : BaseGLActivity() {

    override fun createSurfaceView(): GLSurfaceView {
        return MyGLSurfaceView(this)
    }
}
