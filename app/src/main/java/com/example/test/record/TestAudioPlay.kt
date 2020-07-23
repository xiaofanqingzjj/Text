package com.example.test.record

import android.os.Bundle
import android.util.Log
import android.view.View
import com.bedrock.module_base.SimpleFragment
import com.example.test.R
import kotlinx.android.synthetic.main.fragment_audio_play.*


class TestAudioPlay : SimpleFragment() {

    companion object {
        const val TAG = "TestRecord"
    }


    private val mediaPlayerManager = MediaPlayerManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_audio_play)


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        btn_record.setOnClickListener {
//            // https://github.com/xiaofanqingzjj/Text/blob/master/fxsv.mp3
//            // https://raw.githubusercontent.com/xiaofanqingzjj/Text/master/fxsv.mp3?token=ABDBP6E7SMVTPDKPK63FTL267VUAI
//
////            MediaPlayerManager.play("https://dl.espressif.com/dl/audio/ff-16b-2c-44100hz.aac", 1, false)
////            MediaPlayerManager.play("http://www.bigrockmusic.com/mp3/track_02.mp3", 1, false)
////            MediaPlayerManager.play("https://cdn.story.qq.com/web2/audio/fxsv.amr", 1, false)
//            MediaPlayerManager.playAudio("https://cdn.story.qq.com/web2/audio/fxsv.mp3", 1, false)
//        }

        btn_1.setOnClickListener {
            mediaPlayerManager.playAudio(
                    path = "https://cdn.story.qq.com/web2/audio/fxsv.amr",
                    channel = 1,
                    action =  0,
                    callback = {code, msg ->
                        Log.d(TAG, "play callback:$code, msg:$msg" )
                    })
        }

        btn_2.setOnClickListener {
            mediaPlayerManager.playAudio(
                    path = "https://cdn.story.qq.com/web2/audio/fxsv.amr",
                    channel = 1,
                    action =  1,
                    callback = {code, msg ->
                        Log.d(TAG, "play callback:$code, msg:$msg" )
                    })
        }

        btn_3.setOnClickListener {
            mediaPlayerManager.playAudio(
                    path = "https://cdn.story.qq.com/web2/audio/fxsv.amr",
                    channel = 1,
                    action =  2,
                    callback = {code, msg ->
                        Log.d(TAG, "play callback:$code, msg:$msg" )
                    })
        }

        btn_4.setOnClickListener {
            mediaPlayerManager.playAudio(
                    path = "https://cdn.story.qq.com/web2/audio/fxsv.mp3",
                    channel = 2,
                    action =  0,
                    callback = {code, msg ->
                        Log.d(TAG, "play callback:$code, msg:$msg" )
                    })
        }

        btn_5.setOnClickListener {
            mediaPlayerManager.playAudio(
                    path = "https://cdn.story.qq.com/web2/audio/fxsv.mp3",
                    channel = 2,
                    action =  1,
                    callback = {code, msg ->
                        Log.d(TAG, "play callback:$code, msg:$msg" )
                    })
        }

        btn_6.setOnClickListener {
            mediaPlayerManager.playAudio(
                    path = "https://cdn.story.qq.com/web2/audio/fxsv.mp3",
                    channel = 2,
                    action =  2,
                    callback = {code, msg ->
                        Log.d(TAG, "play callback:$code, msg:$msg" )
                    })
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayerManager?.release()
    }
}