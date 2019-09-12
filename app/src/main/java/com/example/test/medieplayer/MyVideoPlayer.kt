package com.example.test.medieplayer

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.dialog_menu_buttom_item_menu.view.*


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
        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
            return false
        }

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        }
    }
}