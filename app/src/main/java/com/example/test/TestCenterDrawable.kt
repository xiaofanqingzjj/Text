package com.example.test

import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bedrock.module_base.SimpleFragment
import kotlinx.android.synthetic.main.fragment_test_center_drawable.*

class TestCenterDrawable : SimpleFragment() {

    companion object {
        const val TAG = "TestDrawerLayout"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_test_center_drawable)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val bundle: Bundle;
        val intent: Intent


        iv1.setImageDrawable(get())
        iv2.setImageDrawable(get())
        iv3.setImageDrawable(get())

        iv4.setImageDrawable(get())

//        iv4.setImageDrawable(MyColorDrawable(Color.RED))


    }

    fun get(): MyDrawable {
        return MyDrawable(context?.getDrawable(R.drawable.ic_default_img_stub) as BitmapDrawable)
    }

}


class MyColorDrawable(color: Int) : ColorDrawable(color) {

    companion object {
        const val TAG = "MyColorDrawable"
    }

    override fun setBounds(bounds: Rect) {
        super.setBounds(bounds)
        Log.d(TAG, "setBounds:$bounds")

    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)

        Log.d(TAG, "setBounds:$left, $top, $right, $bottom")
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        Log.d(TAG, "draw:$bounds")
    }
}


class MyDrawable(private val bitmapDrawable: BitmapDrawable): Drawable() {

    companion object {
        const val TAG = "MyDrawable"
    }


    init {
        bitmapDrawable.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }
    }

    override fun draw(canvas: Canvas) {
        Log.d(TAG, "draw:${bounds}, dRec:${bitmapDrawable?.bounds}")

        canvas.save()

        val dw = this.bounds.width()
        val dh = this.bounds.height()

        val iw = this.bitmapDrawable.bounds.width()
        val ih = this.bitmapDrawable.bounds.height()

        val scaleX = dw.toFloat() / iw.toFloat()
        val scaleY = dh.toFloat() / ih.toFloat()

        var scale =  if (scaleX < scaleY) scaleX else scaleY
        scale = if (scale < 1) scale else 1f

        val pw = iw * scale
        val ph = ih * scale

        val xTrans = (dw - pw) / 2
        val yTrans = (dh - ph)

        Log.d(TAG, "scale:$scale")
        canvas.translate(xTrans, yTrans)
        canvas.scale(scale, scale)

        bitmapDrawable.draw(canvas)

        canvas.restore()
    }

//    override fun getIntrinsicHeight(): Int {
//        return bitmapDrawable.intrinsicHeight
//    }
//
//    override fun getIntrinsicWidth(): Int {
//        return bitmapDrawable.intrinsicWidth
//    }

    override fun setAlpha(alpha: Int) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

}

