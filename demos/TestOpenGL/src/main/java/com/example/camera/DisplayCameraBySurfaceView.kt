package com.example.camera

import android.app.Activity
import android.hardware.Camera
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView

class DisplayCameraBySurfaceView : Activity() {

    companion object {
        const val TAG = "CameraSurfaceView"
    }

    lateinit var surfaceView: SurfaceView

    var camera: Camera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        surfaceView = SurfaceView(this)

        surfaceView.holder.addCallback(object: SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder?) {
//                holder.lockCanvas()
                if (holder != null) {
                    camera = CameraHelper.openAndPreview(holder);
                    holder.lockHardwareCanvas()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                camera?.stopPreview()
                camera?.release()
            }

        })



        setContentView(surfaceView)

    }
}