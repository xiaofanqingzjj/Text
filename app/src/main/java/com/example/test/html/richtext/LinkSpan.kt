package com.example.test.html.richtext

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import com.example.test.R


/**
 * 超链接的span
 *
 * @author fortune
 */
class LinkSpan(context: Context, title: String?, url: String?) : BaseLinkSpan(title, url) {

    var linkDrawable = context.getDrawable(R.drawable.ic_link)

    init {
        linkDrawable?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }
    }

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        val textSize = super.getSize(paint, text, start, end, fm)
        return textSize + (linkDrawable?.bounds?.right ?: 0)
    }


    override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        linkDrawable?.run {
            canvas.save()
            // 绘制图片
            val r = this.bounds
            val transY = top + (bottom - top - r.height()) / 2
            canvas.translate(x, transY.toFloat())
            this.draw(canvas)
            canvas.restore()
        }


        if (!mText.isNullOrEmpty()) {
            canvas.drawText(mText!!, x + (linkDrawable?.bounds?.right ?: 0), y.toFloat(), paint)
        }
    }

}