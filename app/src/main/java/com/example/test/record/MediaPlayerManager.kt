package com.example.test.record

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.fortunexiao.tktx.async
import java.lang.Exception


/**
 * 音频播放管理器
 *
 * @author fortunexiao
 */
class MediaPlayerManager {

    companion object {
        const val TAG = "MediaPlayerManager"
    }


    private val mMediaPlayers: HashMap<Int, MyMediaPlayer> = HashMap();

    private val handler = Handler(Looper.getMainLooper())

    private fun ui(r:()->Unit) {
        handler.post {
            r.invoke()
        }
    }

    private inline fun secureRun(run: ()->Unit) {
        try {
            run()
        } catch (e: Exception) {
            // ignore
        }
    }

    /**
     * 播放音频
     *
     * @param path 播放的音频地址
     * @param channel 播放频道，同一个频道后面的播放会覆盖前面的播放
     * @param action 0表示播放，1表示暂停， 2表示停止
     * @param isLoop 是否循环播放
     * @param callback 回调，code 0 表示播放成功，-1表示播放失败，1表示播放结束
     *
     */
    fun playAudio(path: String?,
                  channel: Int = 0,
                  action: Int = 0,
                  isLoop: Boolean = false,
                  callback: ((code: Int, msg: String?)->Unit)? = null) {

        Log.d(TAG, "play:$path, channel:$channel, action:$action, isLoop:$isLoop")

        // uri不合法
        if (path.isNullOrEmpty()) {
            ui {
                callback?.invoke(-1, "uri is invalidate")
            }
            return
        }

        try {
            var mediaPlayer = mMediaPlayers[channel]

            when (action) {
                0 -> { // 播放
                    if (mediaPlayer == null) {
                        mediaPlayer = MyMediaPlayer().apply {
                            setAudioStreamType(AudioManager.STREAM_MUSIC);

                            // 注册相关回调

                            // prepared回调
                            setOnPreparedListener(MediaPlayer.OnPreparedListener { mp ->
                                // 同步成功开始播放音频
                                Log.e(TAG, "onPrepared begin play")
                                secureRun {
                                    mp?.start()
                                }

                                // 播放成功
                                ui {
                                    callback?.invoke(0, "play success")
                                }
                            })

                            // 播放结束回调
                            setOnCompletionListener {
                                Log.d(TAG, "play complete")

                                ui {
                                    callback?.invoke(1, "play complete")
                                }

                                // 播放完之后，释放当前的播放器
                                secureRun {
                                    it.release()
                                    mMediaPlayers.remove(channel)
                                }
                            }

                            // buffer更新
                            setOnBufferingUpdateListener { mp, percent ->
                                Log.d(TAG, "on buffer update:$percent")
                            }
                        }

                        mMediaPlayers[channel] = mediaPlayer
                    }

                    mediaPlayer.run {
                        if (path == currentPlayPath) { // 如果url当前已经在播放了
                            if (!isPlaying) { // 如果在播了， 则不管
                                if (isPaused) {
                                    secureRun {
                                        start()
                                    }

                                    // 继续播放成功
                                    ui {
                                        callback?.invoke(0, "resume success")
                                    }

                                } else {
                                    secureRun { prepareAsync()  }
                                }
                            }
                        } else {
                            // 停止之前的播放
                            Log.e(TAG, "reset org play:$currentPlayPath")
                            reset()

                            // 设置新的播放源
                            setDataSource(path)

                            // 开始同步
                            secureRun { mediaPlayer.prepareAsync() }

                            Log.d(TAG, "prepareAsync")
                        }
                        // 是否循环
                        isLooping = isLoop
                    }

                }
                1 -> { // 暂停
                    mediaPlayer?.pause()

                    ui {
                        callback?.invoke(0, "pause success")
                    }
                }
                2 -> { // 停止
                    mediaPlayer?.stop()

                    ui {
                        callback?.invoke(0, "stop success")
                    }
                }
            }
        } catch (e: Throwable) {
            ui {
                callback?.invoke(-1, "play error: ${e.message}")
            }
            Log.e(TAG, "play error", e)
        }
    }



    /**
     * 释放所有的播放
     */
    fun release() {
        async {

            try {
                mMediaPlayers.forEach {
                    it.value.stop()
                    it.value.release()
                }
                mMediaPlayers.clear()
            } catch (e: Exception) {
                // ignore
            }
        }
    }


    class MyMediaPlayer: MediaPlayer() {

        var currentPlayPath: String? = null

        var isPaused = false

        override fun setDataSource(path: String?) {
            super.setDataSource(path)
            currentPlayPath = path
        }

        override fun pause() {
            super.pause()
            isPaused = true
        }

        override fun start() {
            super.start()
            isPaused = false
        }
    }

}