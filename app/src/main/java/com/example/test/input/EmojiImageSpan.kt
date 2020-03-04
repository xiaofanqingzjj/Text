package com.example.test.input

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan

/**
 * 垂直居中的ImageSpan
 *
 * @author fortunexiao
 *
 */
open class EmojiImageSpan @JvmOverloads constructor(d: Drawable, verticalAlignment: Int, lineSpace: Int = 0) : ImageSpan(d, verticalAlignment) {

    private var lineSpace = lineSpace

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        val d = drawable ?: return 0

        val rect = d.bounds ?: return 0

        val size = rect.right
        if (fm != null) {
            val fmi = paint.fontMetricsInt
            val fontHeight = fmi.bottom - fmi.top
            val drawHeight = rect.bottom - rect.top

            val top = drawHeight / 2 - fontHeight / 4
            val bottom = drawHeight / 2 + fontHeight / 4

            fm.top = -bottom
            fm.ascent = -bottom
            fm.descent = top
            fm.bottom = top
        }

        return size
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        val d = drawable
        if (d != null) {
            canvas.save()
            var transY = 0
            //TODO 富文本表情对齐有点问题换种思路
            val fm = paint.fontMetricsInt
            if (fm != null) {// y+ascent得到文字内容的顶部坐标,y+descent得到文字的底部坐标,顶部坐标+底部坐标/2 =文字中间线的坐标
                transY = (y + fm.descent + (y + fm.ascent)) / 2 - d.bounds.bottom / 2
            } else {
                transY = (bottom - top - d.bounds.bottom) / 2 + top - lineSpace / 2
            }

            canvas.translate(x, transY.toFloat())
            d.draw(canvas)
            canvas.restore()
        }
    }
}
