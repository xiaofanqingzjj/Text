package com.example.opengl

import android.app.Activity
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.TextureView

/**
 * 使用TextureView渲染open GL
 */
class OpenGLDemo5 : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TextureView内部会创建一个SurfaceTexture，SurfaceTexture可以消费图像数据流生成纹理
        // TextureView会把纹理合并到当前的View树上

        // TextureView内部会创建一个SurfaceTexture，SurfaceTexture可以消费图像数据流生成纹理
        // TextureView会把纹理合并到当前的View树上
        val mTextureView = TextureView(this)
        mTextureView.surfaceTextureListener = surfaceTextureListener

        setContentView(mTextureView)
    }

    private val surfaceTextureListener  = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {


        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return true;
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        }

    }

}