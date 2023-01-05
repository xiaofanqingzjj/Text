package com.example.surfaceview

import android.app.Activity
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView

class TestSurfaceView : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        val surface = Surface();
//
        val sss = SurfaceTexture(0);
        sss.updateTexImage()
        sss.setOnFrameAvailableListener {

        }

        val surfaceView = SurfaceView(this);

        val ts = TextureView(this);

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback2 {
            override fun surfaceCreated(holder: SurfaceHolder) {
                val canvas = holder?.lockCanvas()


                holder?.unlockCanvasAndPost(canvas);
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }

            override fun surfaceRedrawNeeded(holder: SurfaceHolder) {
            }

        })

        setContentView(surfaceView)
    }
}