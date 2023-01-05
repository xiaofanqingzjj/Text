package com.example.test.medieplayer

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout


/**
 *
 */
class MyVideoPlayer(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {


    private var mVideoUri: String? = null

    init {

    }


    fun setDataSource(url: String) {

    }


    fun start() {

        val textureView = TextureView(context)
        addView(textureView, LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))




    }

    private val surfaceTextureListener = object :TextureView.SurfaceTextureListener{

        override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
        }

        override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {
        }

        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
            return  false;
        }

        override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
        }
    }
}