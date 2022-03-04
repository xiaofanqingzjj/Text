package com.example.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.example.opengl.glsurfaceview.MyBaseGLSurfaceView
import com.example.opengl.program.TextureShaderProgram
import com.example.opengl.util.TextureHelper
import com.example.opengl.util.toFloatBuffer
import com.example.opengl.util.toShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 * 渲染一张纹理图片在界面上
 */
class MyRender4(var context: Context) : MyBaseGLSurfaceView.Renderer {

    companion object {
        const val TAG = "MyRender4"
    }


    private lateinit var textureProgram: TextureShaderProgram
    private var textureId: Int = 0

    //相机矩阵
    private var mViewMatrix = FloatArray(16)

    //投影矩阵
    private var mProjectMatrix = FloatArray(16)

    //最终变换矩阵
    private var mMVPMatrix = FloatArray(16)

    private val image = Image()

    // 动画帧的纹理id
    private val frameTextures = mutableListOf<Int>()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f);

        // 纹理着色程序
        textureProgram = TextureShaderProgram()


        // 加载纹理
//        textureId = TextureHelper.loadTexture(context, R.drawable.heart0);
        loadFramesTexture()
    }

    private fun loadFramesTexture() {

         val t1 = TextureHelper.loadTexture(context, R.drawable.heart0);
        val t2 = TextureHelper.loadTexture(context, R.drawable.heart1);
        val t3 = TextureHelper.loadTexture(context, R.drawable.heart2);
        val t4 = TextureHelper.loadTexture(context, R.drawable.heart3);
        val t5 = TextureHelper.loadTexture(context, R.drawable.heart4);
        val t6 = TextureHelper.loadTexture(context, R.drawable.heart5);

        frameTextures.add(t1);
        frameTextures.add(t2);
        frameTextures.add(t3);
        frameTextures.add(t4);
        frameTextures.add(t5);
        frameTextures.add(t6);



    }



    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        //图片的宽高
        val w = 580f
        val h = 472f

        // 图片宽高比
        val sWH = w / h

        // 屏幕的宽高比
        val sWidthHeight = width.toFloat() / height.toFloat()


        var left: Float
        var right: Float
        var top: Float
        var bottom: Float

        if (width > height) { // 横图
            if (sWH > sWidthHeight) {
                left = -sWidthHeight * sWH
                right = sWidthHeight * sWH
                bottom = -1f
                top = 1f
            } else {
                left = -sWidthHeight / sWH
                right = sWidthHeight / sWH
                bottom = -1f
                top = 1f
            }
        } else {
            if (sWH > sWidthHeight) {
                left = -1f
                right = 1f
                bottom = -1 / sWidthHeight * sWH
                top = 1 / sWidthHeight * sWH
            } else {
                left = -1f
                right = 1f
                bottom = -sWH / sWidthHeight
                top = sWH / sWidthHeight

            }
        }

        Matrix.orthoM(mProjectMatrix, 0, left,
            right,
            bottom, top, -1f, 1f);


        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f,
                -1.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    var currentIndex = 0;

    fun nextFrame(): Int {
        currentIndex ++;
        if (currentIndex >= frameTextures.size) {
            currentIndex = 0
        }
        return currentIndex
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        textureProgram.useProgram()

        // 设置相机视图
        textureProgram.setMatrix(mMVPMatrix)


        // 设置要渲染的纹理
        textureProgram.setTexture(frameTextures[nextFrame()])

        // 设置顶点数据和纹理坐标数据
        image.bind(textureProgram)

        // 绘制
        image.draw()

//        Sync.sync(5)
        Thread.sleep(100)
    }

}

/**
 * 表示界面上的一张图片
 */
class Image {
    companion object {
        /**
         * 顶点坐标
         * (x,y,z)
         */
        private val POSITION_VERTEX = floatArrayOf(
                0f, 0f, 0f,     //顶点坐标V0
                1f, 1f, 0f,     //顶点坐标V1
                -1f, 1f, 0f,    //顶点坐标V2
                -1f, -1f, 0f,   //顶点坐标V3
                1f, -1f, 0f     //顶点坐标V4
        )

        /**
         * 纹理坐标
         * (s,t)
         */
        private val TEX_VERTEX = floatArrayOf(
                0.5f, 0.5f, //纹理坐标V0
                1f, 0f,     //纹理坐标V1
                0f, 0f,     //纹理坐标V2
                0f, 1.0f,   //纹理坐标V3
                1f, 1.0f    //纹理坐标V4
        )

        /**
         * 绘制顺序索引
         */
        private val VERTEX_INDEX = shortArrayOf(
                0, 1, 2,  //V0,V1,V2 三个顶点组成一个三角形
                0, 2, 3,  //V0,V2,V3 三个顶点组成一个三角形
                0, 3, 4,  //V0,V3,V4 三个顶点组成一个三角形
                0, 4, 1   //V0,V4,V1 三个顶点组成一个三角形
        )
    }

    private val vertexBuffer = POSITION_VERTEX.toFloatBuffer()
    private val textureCoodsBuffer = TEX_VERTEX.toFloatBuffer()
    private val vertexIndexBuffer = VERTEX_INDEX.toShortBuffer()


    fun bind(program: TextureShaderProgram) {
        program.setVertexPosition(vertexBuffer,
                0,
                3,
                0
        )
        program.setTextureCoords(textureCoodsBuffer, 0, 2, 0)
    }

    fun draw() {

        vertexIndexBuffer.position(0)

        // 绘制
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,
                VERTEX_INDEX.size,
                GLES20.GL_UNSIGNED_SHORT,
                vertexIndexBuffer); //指定每个三角形顶点在顶点数据的下标

//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 3)

//        GLES20.glDisableVertexAttribArray(position)
//        GLES20.glDisableVertexAttribArray(colorPos)

    }
}