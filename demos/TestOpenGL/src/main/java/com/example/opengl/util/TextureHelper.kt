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

    private const val TAG = "TextureHelper";

    /**
     * 获取一张图片作为纹理
     *
     * @param resourceId 图片资源id
     */
    fun loadTexture(context: Context, resourceId: Int): Int {

        // 使用调用glGenTextures创建一个纹理对象，这个函数接收一个数组，会把创建好的纹理id放到数组中
        val textureObjectIds = IntArray(1)
        GLES20.glGenTextures(1, textureObjectIds, 0)
        if (textureObjectIds[0] == 0) {
            Log.w(TAG, "创建纹理失败!")
        }

        // 加载图片数据
        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inScaled = false
        val bitmap: Bitmap? = BitmapFactory.decodeResource(context.resources, resourceId, options)
        if (bitmap == null) {
            Log.w(TAG, "加载位图失败")
            GLES20.glDeleteTextures(1, textureObjectIds, 0)
            return 0
        }

        // 操作纹理之前，要先绑定纹理id，参数：使用二维纹理，第二个参数纹理id
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectIds[0])

        // 设置纹理过滤器
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // 使用工具方法把图片数据加载到纹理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // 图片可以释放了
        bitmap.recycle()

        // 生成mip贴图
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        // 解除纹理绑定
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);

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