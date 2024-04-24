package com.example.test.record

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import com.bedrock.module_base.SimpleFragment
import com.example.test.R
import java.io.IOException





class TestVideoPlay : SimpleFragment() {

    companion object {
        const val TAG = "TestRecord"
    }


//    private val mediaPlayerManager = MediaPlayerManager()
//    private var surfaceView: SurfaceView? = null
    private var mediaPlayer: MediaPlayer? = null
//    private var surfaceHolder: SurfaceHolder? = null


    private var textureView: TextureView? = null

    private var videoPath = "https://1251775003.vod2.myqcloud.com/0a3b08c1vodcq1251775003/158de02f1397757887411760072/y2mYgJPAyaYA.mp4" // 替换为你的视频路径


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_video_play)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



//        surfaceView = view.findViewById<SurfaceView>(R.id.surface_view)
        textureView = view.findViewById(R.id.texture_view)

        val btnPlay = view.findViewById<View>(R.id.btn_play)
        val btnPause = view.findViewById<View>(R.id.btn_pause)
        val btnStop = view.findViewById<View>(R.id.btn_stop)


//        surfaceHolder = surfaceView!!.holder

        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(videoPath)

//        surfaceHolder?.addCallback(object : SurfaceHolder.Callback {
//            override fun surfaceCreated(holder: SurfaceHolder) {
//                mediaPlayer?.setDisplay(holder)
//            }
//
//            override fun surfaceChanged(
//                holder: SurfaceHolder,
//                format: Int,
//                width: Int,
//                height: Int
//            ) {
//            }
//
//            override fun surfaceDestroyed(holder: SurfaceHolder) {
//                mediaPlayer?.release()
//            }
//        })

        textureView?.surfaceTextureListener = object : SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                mediaPlayer?.setSurface(Surface(surface));
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            }

        }

        btnPlay?.setOnClickListener {
            mediaPlayer?.prepare()
            mediaPlayer?.start()
        }

        btnPause?.setOnClickListener {
            mediaPlayer?.pause()
        }

        btnStop?.setOnClickListener {
            mediaPlayer?.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}