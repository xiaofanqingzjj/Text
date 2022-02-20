package com.example.opengl

import android.Manifest
import android.os.Bundle
import com.bedrock.module_base.MenuActivity
import com.bedrock.permissionrequestor.PermissionsRequestor
import com.example.camera.DisplayCameraBySurfaceTextureOpenGL
import com.example.camera.DisplayCameraBySurfaceView
import com.example.camera.DisplayCameraByTextureView
import com.example.opengl.base.SimpleThreadController

class MainActivity : MenuActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addMenu("Demo1", OpenGLDemo1::class.java)

        addMenu("Demo2", OpenGLDemo2::class.java)

        addMenu("Demo3", OpenGLDemo3::class.java)

        addMenu("Demo4", OpenGLDemo4::class.java)

        addMenu("CameraPreviewByTextureView", DisplayCameraByTextureView::class.java)

        addMenu("CameraPreviewBySurfaceTexture", DisplayCameraBySurfaceTextureOpenGL::class.java)

        addMenu("CameraPreviewBySurfaceView", DisplayCameraBySurfaceView::class.java)

        val c = SimpleThreadController()
        addMenu("Start") {
            c.start()
        }

        addMenu("Pause") {
            c.parse()
        }
        addMenu("Resume") {
            c.resume()
        }


        // 读取相机权限
        val permissionsRequestor = PermissionsRequestor(this)
        permissionsRequestor.request(arrayOf(Manifest.permission.CAMERA))
    }

}
