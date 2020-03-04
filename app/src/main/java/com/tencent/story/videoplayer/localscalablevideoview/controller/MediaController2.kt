package com.tencent.story.videoplayer.localscalablevideoview.controller

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import com.example.test.R
import java.util.*
import android.view.animation.AnimationUtils
import android.view.animation.Animation
import androidx.core.view.ViewCompat.setRotation
import android.widget.FrameLayout
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.view.WindowManager
import android.view.Window.ID_ANDROID_CONTENT
import android.view.ViewGroup




/**
 *
 */
class MediaController2(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    companion object {
        const val TAG = "MediaController2"
    }

    private val sDefaultTimeout = 5000

    private var mBottomBar: View? = null
//    private var mCenterBtn: ImageView? = null
    private var mLeftBtn: ImageView? = null

    private var mProgress: ProgressBar? = null

    private var tvCurTime: TextView? = null
    private var tvDuration: TextView? = null

    private var mPlayer: MediaPlayerControl? = null


    private var mShowing: Boolean = false
    private var mDragging: Boolean = false

    private val onClickListener = OnClickListener { v->
        when(v.id) {
//            R.id.btn_center-> {
//
//            }
            R.id.btn_left -> {
                doPauseResume()
            }
        }
    }

    private var mFormatBuilder: StringBuffer = StringBuffer()
    private var mFormatter: Formatter = Formatter(mFormatBuilder)

    init {


    }


    fun setMediaPlayer(mediaPlayer: MediaPlayerControl) {
        mPlayer = mediaPlayer

        initViews()

//        post(mShowProgress)
    }

    fun show() {
        show(sDefaultTimeout)
    }


    private fun stringForTime(timeMs: Long): String {
        val totalSeconds = timeMs / 1000

        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600

        mFormatBuilder.setLength(0)
        return if (hours > 0) {
            mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            mFormatter.format("%02d:%02d", minutes, seconds).toString()
        }
    }


    private fun initViews() {
        LayoutInflater.from(context).inflate(R.layout.media_controller, this)

        mBottomBar = findViewById(R.id.ll_bottom_bar)
//        mCenterBtn = findViewById(R.id.btn_center)

        mLeftBtn = findViewById(R.id.btn_left)
        mLeftBtn?.setOnClickListener(onClickListener)

        mProgress = findViewById(R.id.seek_bar)

        if (mProgress is SeekBar) {
            (mProgress as SeekBar).run {
                max = 1000
                setOnSeekBarChangeListener(mSeekListener)
            }
        }


        tvCurTime = findViewById(R.id.tv_cur_time)
        tvDuration = findViewById(R.id.tv_duration)

        setOnClickListener {
            show()
        }
    }


    private fun showController() {
        mBottomBar?.visibility = View.VISIBLE

        mLeftBtn?.visibility = View.VISIBLE
    }

    private fun hideController() {
        mBottomBar?.visibility = View.GONE

        if (mPlayer?.isPlaying() == true) {
            mLeftBtn?.visibility = View.GONE
        }
    }

    fun show(timeout: Int) {
        if (!mShowing) {
            setProgress()
            showController()
            mShowing = true
        }

        updatePausePlay()
        startProgressTimer()

        removeCallbacks(mFadeOut)
        if (timeout != 0) { //&& !mAccessibilityManager.isTouchExplorationEnabled()) {
            postDelayed(mFadeOut, timeout.toLong())
        }
    }

    private fun startProgressTimer() {
        post(mShowProgress)
    }

    private fun stopProgressTimer() {
        removeCallbacks(mShowProgress)
    }

    fun isShowing(): Boolean {
        return mShowing
    }

    fun hide() {
        if (mShowing) {

            hideController()

            stopProgressTimer()
            mShowing = false
        }
    }


    private val mFadeOut = Runnable { hide() }

    private val mShowProgress = object : Runnable {
        override fun run() {
            val pos = setProgress()
            if (!mDragging && mShowing && mPlayer?.isPlaying() == true) {
                postDelayed(this, (1000 - pos % 1000).toLong()) // 后面更新快点？
            }
        }
    }


    private fun doPauseResume() {
        mPlayer?.run {
            if (isPlaying()) {
                pause()
            } else {
                start()
            }
        }

        updatePausePlay()
    }

    private fun setProgress(): Int {
        if (mPlayer == null || mDragging) {
            return 0
        }
        val position = mPlayer!!.getCurrentPosition()
        val duration = mPlayer!!.getDuration()

        Log.e(TAG, "setProgress:$position, duration:$duration")

        mProgress?.run {
            if (duration > 0) {
                // use long to avoid overflow
                val pos = 1000L * position / duration
                progress = pos.toInt()
            }

            val percent = mPlayer!!.getBufferPercentage()
            secondaryProgress = percent * 10
        }

        tvCurTime?.text = stringForTime(position.toLong())
        tvDuration?.text = stringForTime(duration.toLong())


        return position
    }

    private var touchStartIsPlayer = false

    private val mSeekListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(bar: SeekBar) {
            show(3600000)
            mDragging = true

            touchStartIsPlayer = mPlayer?.isPlaying() ?: false

            mPlayer?.pause()

            stopProgressTimer()
        }

        override fun onProgressChanged(bar: SeekBar, progress: Int, fromuser: Boolean) {
            if (!fromuser) {
                return
            }

            mPlayer?.run {
                val duration = getDuration().toLong()
                val newPosition = duration * progress / 1000L
                Log.d(TAG, "seekTo:${newPosition.toInt()}, duration:${duration}")
                seekTo(newPosition.toInt())
                tvCurTime?.text = stringForTime(newPosition.toLong())
                tvDuration?.text = stringForTime(duration.toLong())
                Unit
            }
        }

        override fun onStopTrackingTouch(bar: SeekBar) {
            mDragging = false

            setProgress()

            if (touchStartIsPlayer) {
                mPlayer?.start()
            }

            updatePausePlay()

            show(sDefaultTimeout)
        }
    }

    private fun updatePausePlay() {

        if (mPlayer?.isPlaying() == true) {
            mLeftBtn?.setImageResource(R.drawable.ic_video_pause)
        } else {
            mLeftBtn?.setImageResource(R.drawable.ic_video_play)
        }
    }


    override  fun onTouchEvent( event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN-> {
                show(0) // show until hide is called
            }
            MotionEvent.ACTION_UP-> {
                show(sDefaultTimeout) // start timeout
            }
            MotionEvent.ACTION_CANCEL -> {
                hide()
            }
        }
        return true
    }


    /**
     * 小屏窗口切换为全屏播放
     */
    fun startWindowFullscreen() {
        Log.i(TAG, "startWindowFullscreen " + " [" + this.hashCode() + "] ")
//
//        hideSupportActionBar(context)
//        /**activity的content layout */
//        val vp = JCUtils.scanForActivity(context).findViewById(Window.ID_ANDROID_CONTENT) as ViewGroup
//        /**通过id找到播放器自己本身 */
//        val old = vp.findViewById(FULLSCREEN_ID)
//        /**如果在设置全屏之前activity的content layout中本来就存在播放器就先移除 */
//        if (old != null) {
//            vp.removeView(old)
//        }
//        if (textureViewContainer.getChildCount() > 0) {
//            textureViewContainer.removeAllViews()
//        }
//        try {
//            val constructor = this@JCVideoPlayer.getClass().getConstructor(Context::class.java) as Constructor<JCVideoPlayer>
//            val jcVideoPlayer = constructor.newInstance(context)
//            jcVideoPlayer.setId(FULLSCREEN_ID)
//            /**获取屏幕的宽高 */
//            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//            val w = wm.defaultDisplay.width
//            val h = wm.defaultDisplay.height
//            val lp = FrameLayout.LayoutParams(h, w)
//            lp.setMargins((w - h) / 2, -(w - h) / 2, 0, 0)
//            vp.addView(jcVideoPlayer, lp)
//            jcVideoPlayer.setUp(url, JCVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN, objects)
//            jcVideoPlayer.setUiWitStateAndScreen(currentState)
//            /**小窗口和全屏的切换是创建了两个播放器，之间的衔接全靠这个方法 */
//            jcVideoPlayer.addTextureView()
//            jcVideoPlayer.setRotation(90)
//
//            val ra = AnimationUtils.loadAnimation(context, R.anim.start_fullscreen)
//            jcVideoPlayer.setAnimation(ra)
//            /**监听器的替换 */
//            JCVideoPlayerManager.setLastListener(this)
//            JCVideoPlayerManager.setListener(jcVideoPlayer)
//
//
//        } catch (e: InstantiationException) {
//            e.printStackTrace()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

    }


    interface MediaPlayerControl {
        fun start()
        fun pause()
        fun getDuration(): Int
        fun getCurrentPosition(): Int
        fun seekTo(pos: Int)
        fun isPlaying(): Boolean
        fun getBufferPercentage(): Int
        fun canPause(): Boolean
        fun canSeekBackward(): Boolean
        fun canSeekForward(): Boolean

        /**
         * Get the audio session id for the player used by this VideoView. This can be used to
         * apply audio effects to the audio track of LINK_PATTERN video.
         * @return The audio session, or 0 if there was an error.
         */
        fun getAudioSessionId(): Int
    }
}