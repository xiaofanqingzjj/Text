package com.example.opengl.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import android.util.Log


/**
 * 加载纹理的工具类
 *
 * 来自博客：
 * https://blog.csdn.net/zhaodebbs/article/details/68484538
 */
object TextureHelper {

    const val TAG = "TextureHelper";

    /**
     * 获取一张图片作为纹理
     */
    fun loadTexture(context: Context, resourceId: Int): Int {

        val textureObjectIds = IntArray(1)

        GLES20.glGenTextures(1, textureObjectIds, 0)

        if (textureObjectIds[0] == 0) {
            Log.w(TAG, "创建纹理失败!")
        }

        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inScaled = false
        val bitmap: Bitmap? = BitmapFactory.decodeResource(context.resources, resourceId, options)
        if (bitmap == null) {
            Log.w(TAG, "加载位图失败")
            GLES20.glDeleteTextures(1, textureObjectIds, 0)
            return 0
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectIds[0])

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // 加载位图到纹理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        bitmap.recycle()

        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);

        return textureObjectIds[0]
    }


//    fun buildProgram(vertexShaderSource: String?, fragmentShaderSource: String?): Int {
//        val program: Int
//        val vertexShader: Int = compileVertexShader(vertexShaderSource)
//        val fragmentShader: Int = compileFragmentShader(fragmentShaderSource)
//        program = linkProgram(vertexShader, fragmentShader)
//        validateProgram(program)
//        return program
//    }
}