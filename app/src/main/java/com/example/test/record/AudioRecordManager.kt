package com.example.test.record

import android.media.MediaRecorder
import android.util.Log
import java.io.File
import java.lang.Exception


/**
 * 录音管理器
 *
 *
 */
object AudioRecordManager {

    const val TAG = "AudioRecordManager"

    private var mMediaRecord: MediaRecorder? = null

    private var isRecording = false

    /**
     * 开始录音
     *
     * @param savePath 保存录音的位置
     */
    fun startRecord(savePath: File) : Boolean{

        Log.d(TAG, "startRecord:$savePath, isRecording:$isRecording, record:$mMediaRecord")

        try {
            if (isRecording) {
                Log.d(TAG, "current isRecording ")
                return false
            }

            if (mMediaRecord == null) {
                mMediaRecord = createRecord()
            }

            mMediaRecord?.run {
                setOutputFile(savePath.absolutePath)
                prepare()
                start()
            }

            isRecording = true

            return true
        } catch (e: Exception) {
            Log.e(TAG, "startRecord error", e)
            return false
        }
    }

    fun stopRecord() {
        try {
            mMediaRecord?.stop()
            mMediaRecord?.release()
        } catch (e: Exception) {
            Log.e(TAG, "stopRecord error", e)
        }
        mMediaRecord = null
        isRecording = false
    }

    private fun createRecord(): MediaRecorder {
        return  MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        }
    }
}

