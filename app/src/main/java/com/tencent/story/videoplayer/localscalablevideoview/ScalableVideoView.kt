package com.tencent.story.videoplayer.localscalablevideoview

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.support.annotation.RawRes
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.widget.MediaController
import android.widget.VideoView
import com.example.test.R
import com.tencent.story.videoplayer.localscalablevideoview.controller.MediaController2
import java.io.FileDescriptor
import java.io.IOException
import java.lang.IllegalStateException

/**
 * 一个可以支持scaleType的本地视频播放器
 *
 * @author fortunexiao
 */
class ScalableVideoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : TextureView(context, attrs, defStyle),
        MediaController.MediaPlayerControl, MediaController2.MediaPlayerControl {
 
    companion object {
        const val TAG = "ScalableVideoView"
    }

    private var mMediaPlayer: MediaPlayer? = null
    private var mScalableType = ScalableType.CENTER_INSIDE



    /**
     * 视频的高度
     */
    val videoHeight: Int
        get() = mMediaPlayer?.videoHeight ?: 0

    /**
     * 视频的宽度
     */
    val videoWidth: Int
        get() = mMediaPlayer?.videoWidth ?: 0

    /**
     * 是否是循环播放
     */
    var isLooping: Boolean
        get() = mMediaPlayer?.isLooping ?: false
        set(looping) {
            mMediaPlayer?.isLooping = looping
        }

    private var mCurrentBufferPercentage: Int = 0

    private val mBufferingUpdateListener = MediaPlayer.OnBufferingUpdateListener { mp, percent ->
        mCurrentBufferPercentage = percent
    }


    private val onVideoSizeChangeListener = object : MediaPlayer.OnVideoSizeChangedListener {
        override fun onVideoSizeChanged(mp: MediaPlayer?, width: Int, height: Int) {
            // 根据设置的scaleType缩放视频
            scaleVideoSize(width, height)
        }
    }

    private val surfaceTextureListener = object : SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
            return false
        }

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            // 把当前的surfaceTexture作为多媒体播放器的显示界面
            Log.d(TAG, "onSurfaceTextureAvailable w:$width, h:$height")

            val surface = Surface(surfaceTexture)
            mMediaPlayer?.setSurface(surface)
        }

    }


    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.scaleStyle, 0, 0)
            val scaleType = a!!.getInt(R.styleable.scaleStyle_scalableType, ScalableType.CENTER_INSIDE.ordinal)
            mScalableType = ScalableType.values()[scaleType]
            a.recycle()
        }

        val vv : VideoView


        // 创建多媒体播放器
        mMediaPlayer = MediaPlayer()
        mMediaPlayer?.setOnVideoSizeChangedListener(onVideoSizeChangeListener)
        mMediaPlayer?.setOnBufferingUpdateListener(mBufferingUpdateListener)

        // 监听SurfaceTexture的创建状态
        setSurfaceTextureListener(surfaceTextureListener)
//        surfaceTextureListener = this
    }

//    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
//        // 把当前的surfaceTexture作为多媒体播放器的显示界面
//        Log.d(TAG, "onSurfaceTextureAvailable w:$width, h:$height")
//
//        val surface = Surface(surfaceTexture)
//        mMediaPlayer.setSurface(surface)
//    }
//
//    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
//
//    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
//        Log.d(TAG, "onSurfaceTextureDestroyed")
//        return false
//    }
//
//    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
//        Log.e(TAG, "onSurfaceTextureUpdated")
//    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (isPlaying) {
            stop()
        }
        release()
    }

//    override fun onVideoSizeChanged(mp: MediaPlayer, width: Int, height: Int) {
//        // 根据设置的scaleType缩放视频
//        scaleVideoSize(width, height)
//    }

    private fun scaleVideoSize(videoWidth: Int, videoHeight: Int) {
        if (videoWidth == 0 || videoHeight == 0) {
            return
        }

        val viewSize = Size(width, height)
        val videoSize = Size(videoWidth, videoHeight)

        val scaleManager = ScaleManager(viewSize, videoSize)
        val matrix = scaleManager.getScaleMatrix(mScalableType)
        if (matrix != null) {
            setTransform(matrix)
        }
    }


    /**
     * 设置缩放类型
     */
    fun setScalableType(scalableType: ScalableType) {
        mScalableType = scalableType
        scaleVideoSize(videoWidth, videoHeight)
    }

//    /**
//     * 准备
//     */
//    @JvmOverloads
//    fun prepare(listener: MediaPlayer.OnPreparedListener? = null) {
//        mMediaPlayer?.setOnPreparedListener(listener)
//
//        try {
//            mMediaPlayer?.prepareAsync()
//        } catch (e: java.lang.IllegalStateException) {
//            Log.w(TAG, "prepare:${e.message}", e)
//        } catch (e: IOException) {
//            Log.w(TAG, "prepare:${e.message}", e)
//        }
//    }


    /**
     * 监听视频开始播放
     */
    @JvmOverloads
    fun prepareAsync(listener: MediaPlayer.OnPreparedListener? = null) {

        mMediaPlayer?.setOnPreparedListener(listener)

        try {
            mMediaPlayer?.prepareAsync()
        } catch (e: java.lang.IllegalStateException) {
            Log.w(TAG, "prepareAsync:${e.message}", e)
        }
    }

    /**
     * 错误监听
     */
    fun setOnErrorListener(listener: MediaPlayer.OnErrorListener?) {
        mMediaPlayer?.setOnErrorListener(listener)
    }

    /**
     * 播放完成监听
     */
    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener?) {
        mMediaPlayer?.setOnCompletionListener(MediaPlayer.OnCompletionListener {
            Log.w(TAG, "OnCompletion")
            listener?.onCompletion(it)
        })
    }

    /**
     * 监听一些信息
     *
     */
    fun setOnInfoListener(listener: MediaPlayer.OnInfoListener?) {
        mMediaPlayer?.setOnInfoListener(listener)
    }



    /**
     * 设置音量
     */
    fun setVolume(leftVolume: Float, rightVolume: Float) {
        mMediaPlayer?.setVolume(leftVolume, rightVolume)
    }



    /**
     * 停止
     */
    fun stop() {
        try {
            mMediaPlayer?.stop()
        } catch (e: java.lang.IllegalStateException) {
            Log.d(TAG, "stop Exception:${e.message}", e)
        }
//        mMediaPlayer.stop()
    }

    /**
     * 重置
     */
    fun reset() {
        try {
            mMediaPlayer?.reset()
        } catch (e: java.lang.IllegalStateException) {
            Log.d(TAG, "reset Exception:${e.message}", e)
        }
//        try {
//            mMediaPlayer.reset()
//        } catch (e: Exception) {
//        }
    }

    /**
     * 释放
     */
    fun release() {
        reset()

        try {
            mMediaPlayer?.release()
        } catch (e: java.lang.IllegalStateException) {
            Log.d(TAG, "release Exception:${e.message}", e)
        }
//        mMediaPlayer.release()
    }


    // 设置播放视频的方法

    /**
     * 播放raw目录下的视频
     */
    @Throws(IOException::class)
    fun setRawData(@RawRes id: Int) {
        val afd = resources.openRawResourceFd(id)
        setDataSource(afd)
    }

    /**
     * 播放assets目录下的视频
     */
    @Throws(IOException::class)
    fun setAssetData(assetName: String) {
        val manager = context.assets
        val afd = manager.openFd(assetName)
        setDataSource(afd)
    }

    @Throws(IOException::class)
    private fun setDataSource(afd: AssetFileDescriptor) {
        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        afd.close()
    }

    fun setDataSource(path: String) {
        try {
            mMediaPlayer?.setDataSource(path)
        } catch (e: Exception) {

        }

    }

    fun setDataSource(context: Context, uri: Uri, headers: Map<String, String>?) {
        try {
            mMediaPlayer?.setDataSource(context, uri, headers)
        } catch (e: Exception) {

        }
    }

    fun setDataSource(context: Context, uri: Uri) {
        try {
            mMediaPlayer?.setDataSource(context, uri)
        } catch (e: Exception) {

        }
    }

    fun setDataSource(fd: FileDescriptor, offset: Long, length: Long) {
        try {
            mMediaPlayer?.setDataSource(fd, offset, length)
        } catch (e: Exception) {

        }
    }

    fun setDataSource(fd: FileDescriptor) {
        try {
            mMediaPlayer?.setDataSource(fd)
        } catch (e: Exception) {

        }
    }


    /**
     * 开始
     */
    override fun start() {
        try {
            mMediaPlayer?.start()
        } catch (e: java.lang.IllegalStateException) {
            Log.d(TAG, "start Exception:${e.message}", e)
        }

//        mMediaPlayer.start()
    }


    /**
     * 暂停
     */
    override fun pause() {
        try {
            mMediaPlayer?.pause()
        } catch (e: java.lang.IllegalStateException) {
            Log.d(TAG, "pause Exception:${e.message}", e)
        }
    }

    /**
     * 拖动
     */
    override fun seekTo(msec: Int) {
        mMediaPlayer?.seekTo(msec)
    }

    override fun isPlaying(): Boolean {
        return mMediaPlayer?.isPlaying ?: false
    }


    override fun canSeekForward(): Boolean {
        return true
    }



    override fun getDuration(): Int {
        return mMediaPlayer?.duration ?: 0
    }


    override fun getBufferPercentage(): Int {
        return mCurrentBufferPercentage
    }

    override fun getCurrentPosition(): Int {
        return try {
            mMediaPlayer?.currentPosition ?: 0
        } catch (e: IllegalStateException) {
            0
        }
    }

    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun getAudioSessionId(): Int {
        return mMediaPlayer?.audioSessionId ?: 0
    }

    override fun canPause(): Boolean {
        return true
    }

}
