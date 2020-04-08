package com.example.test.html.richtext

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.StaticLayout
import android.text.style.ReplacementSpan

/**
 * 一个替换文本绘制的span
 *
 * @author fortunexiao
 */
open class ReplaceTextSpan(var mText: String?): ReplacementSpan() {


    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        return mText?.run {
            paint.measureText(mText).toInt()
        } ?: 0
    }

    override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        if (!mText.isNullOrEmpty()) {
//            StaticLayout.Builder.obtain()
            canvas.drawText(mText!!, x, y.toFloat(), paint)
        }
    }
}