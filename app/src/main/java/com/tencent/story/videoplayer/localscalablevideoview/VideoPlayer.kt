package com.tencent.story.videoplayer.localscalablevideoview

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.VideoView
import com.tencent.story.videoplayer.localscalablevideoview.ScalableVideoView


/**
 *
 *
 * @author fortunexiao
 */
class VideoPlayer(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {


    private var mPlayer: ScalableVideoView

    init {
        mPlayer = ScalableVideoView(context)

        val vv:VideoView
    }



    // https://cdn.story.qq.com/file/test_20190307144032493.mp4


}