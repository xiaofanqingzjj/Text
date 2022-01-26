package com.example.camera;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.TextureView;


/**
 * 使用TextureView预览相机内容
 */
public class DisplayCameraByTextureView extends Activity implements TextureView.SurfaceTextureListener {

    private Camera mCamera;
    private TextureView mTextureView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TextureView内部会创建一个SurfaceTexture，SurfaceTexture可以消费图像数据流生成纹理
        // TextureView会把纹理合并到当前的View树上
        mTextureView = new TextureView(this);
        mTextureView.setSurfaceTextureListener(this);

        setContentView(mTextureView);
    }

    /**
     * SurfaceTexture可用
     * SurfaceTexture
     */
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        // 直接通过SurfaceTexture接收头像数据即可，TextureView会负责把纹理合成到View树上
        mCamera = CameraHelper.INSTANCE.openAndPreview(surface);
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, Camera does all the work for us
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's LINK_PATTERN new Camera preview frame
    }
}
