package com.example.test.html

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ReplacementSpan

/**
 * 一个在行内垂直居中显示图片的ImageSpan
 * @author fortunexiao
 */
internal open class CenterVerticalImageSpan @JvmOverloads constructor(private val drawable: Drawable,
                                                        margin: Int = 0) : ReplacementSpan() {
    companion object {
        internal val TAG = CenterVerticalImageSpan::class.java.simpleName
    }


    @JvmOverloads
    constructor(context: Context, iconId: Int,
                margin: Int = 0) : this(context.resources.getDrawable(iconId),  margin)

    private var margin = 0

    init {
//        this.drawable.setBounds(0, 0, if (width != 0) width else drawable.intrinsicWidth, if (height != 0) height else drawable.intrinsicHeight)

        drawable?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }
        this.margin = margin
    }

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        return drawable.run {
            val fmi = paint.fontMetricsInt
            val orgLineHeight = fmi.descent - fmi.ascent // 原来的行高
            val drawableHeight = bounds.height() // 图片的高度
            val dy = (orgLineHeight - (drawableHeight + margin * 2)) / 2 // 行高和图片高度的差值

            // 修改行高
            fm?.run {
                ascent += dy
                descent -= dy
                top += dy
                bottom -= dy
            }




            bounds.right + margin * 2
        }
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        canvas.save()
        // 绘制图片
        val r = drawable.bounds
        val transY = top + (bottom - top - r.height()) / 2
        canvas.translate(x + margin, transY.toFloat())
        drawable.draw(canvas)
        canvas.restore()
    }
}