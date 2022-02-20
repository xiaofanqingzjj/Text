package com.example.camera

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.view.SurfaceHolder
import java.io.IOException

object CameraHelper {

    fun initCamera(): Camera {
        val mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
        mCamera.setDisplayOrientation(90)

        val parameters = mCamera.parameters
        parameters.apply {
            this["orientation"] = "portrait"
            focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
//            setPreviewSize( 720, 1280)
        }
        mCamera.parameters = parameters
        return mCamera

    }

    /**
     * 打开摄像头并开始预览数据
     */
    fun openAndPreview(surfaceTexture: SurfaceTexture): Camera {
        // 获取相机对象，并设置参数
        val mCamera = initCamera()

        try {
            mCamera.setPreviewTexture(surfaceTexture)
            mCamera.startPreview()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return mCamera
    }

    
    fun openAndPreview(surfaceTexture: SurfaceHolder): Camera {
        // 获取相机对象，并设置参数
        val mCamera = initCamera();
        // 开始读取相机里的数据
        // SurfaceTexture对象可以把相机里的每一帧Buffer数据转换成纹理数据

        // 开始读取相机里的数据
        // SurfaceTexture对象可以把相机里的每一帧Buffer数据转换成纹理数据
        try {
            mCamera.setPreviewDisplay(surfaceTexture)
            mCamera.startPreview()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return mCamera;
    }

}