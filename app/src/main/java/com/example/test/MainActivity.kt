package com.example.test

import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.VideoView
import com.tencent.story.videoplayer.localscalablevideoview.ScalableVideoView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        video_player?.run {

//            val v1 = "http://yoyo.qq.com/file/test_20180809195957.mp4"
//            val v2 = "https://cdn.story.qq.com/file/test_20190307144032493.mp4"
            val v2 = "https://cdn.story.qq.com/file/20190722203324539.mp4"
            setDataSource(v2)

            isLooping = true


            prepareAsync(MediaPlayer.OnPreparedListener {
                start()
            })
        }

//        media_controller.setAnchorView(video_player)
//        media_controller.setMediaPlayer(video_player)

        media_controller.setMediaPlayer(video_player)

//        attachMediaController(media_controlle2, video_player)



        val v: VideoView

        val drawable: GradientDrawable = resources.getDrawable(R.drawable.discover_top_ad_bg_card_bg) as GradientDrawable



//
//        btn_download.setOnClickListener {
//            btn_download.setState(DownloadButtom.STATE_DOWNLOADING)
//            ObjectAnimator.ofFloat(btn_download, DownloadButtom.PROGRESS, 0f, 100f).setDuration(51000).start()
//        }
//
//
//        btn.setOnClickListener {
//            ObjectAnimator.ofFloat(btn_download, DownloadButtom.PROGRESS, 0f, 100f).setDuration(5000).start()
//        }
//
//
//        btn2.setOnClickListener {
//            val i = Intent(this@MainActivity, WebViewActivity::class.java).apply {
//                putExtra(WebViewActivity.URL, "https://ue.qq.com/mur/?a=survey&b=24769&c=1&d=2bffe9ab79fd7e36e56b65c59166848a")
//            }
//            startActivity(i)
//
//        }
//
//        btn3.setOnClickListener {
//            startActivity(Intent(this, SomeLeakActivity::class.java))
//        }


//        btn1.setOnClickListener {
////            btn2.visibility = View.VISIBLE
//            val m = MenuDialog.Menu()
//            m.text = "aaaa"
//            val ms = ArrayList<MenuDialog.Menu>()
//            ms.add(m)
//            MenuDialog.show(supportFragmentManager, ms, true)
//
//        }
//
//        btn2.setOnClickListener {
//            btn2.visibility = View.GONE
//        }
//
//        window.navigationBarColor = Color.RED



    }

    private fun attachMediaController(mediaController: MediaController, playerView: ScalableVideoView) {
        mediaController.setMediaPlayer(playerView)
        val anchorView = if (playerView.parent is View) {
            this.parent as View?
        } else {
            playerView
        }
        mediaController.setAnchorView(anchorView)
        mediaController.isEnabled = true
    }
}
