package com.example.opengl

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import com.example.opengl.glsurfaceview.MyBaseGLSurfaceView
import com.example.opengl.program.ColorShaderProgram
import com.example.opengl.util.TextureHelper
import com.example.opengl.program.TextureShaderProgram
import com.example.opengl.obj.Mallet
import com.example.opengl.obj.Table
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class MyRenderer3(var context: Context) : MyBaseGLSurfaceView.Renderer {


    private val projectionMatrix = FloatArray(16)

    private val modelMatrix = FloatArray(16)


    private lateinit var table: Table
    private lateinit var mallet: Mallet


    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgram


    private var texture = 0

    init {

    }


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        table = Table()

        mallet = Mallet()


        // 创建纹理着色器
        textureProgram = TextureShaderProgram()

        colorProgram = ColorShaderProgram(context)

        texture = TextureHelper.loadTexture(context, R.drawable.timg)
        Log.d("MyRenderer3", "loadTexture:$texture")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // 应用程序
        this.textureProgram.useProgram();

        this.textureProgram.setMatrix(projectionMatrix)
        this.textureProgram.setTexture(texture)

//        //
//        this.textureProgram.setUniforms(this.projectionMatrix, this.texture);

        this.table.bindData(this.textureProgram);

        this.table.draw();



//        this.colorProgram.useProgram();
//        this.colorProgram.setUniforms(this.projectionMatrix)
//        this.table.bindData(this.colorProgram);
//        this.table.draw();
//
//        this.mallet.bindData(this.colorProgram);
//
//        this.mallet.draw();
    }

}