package com.example.test.layout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.StaticLayout
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import android.util.AttributeSet
import android.view.View
import androidx.core.text.set
import com.fortunexiao.tktx.platforms.dp2px

class TestLayout(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {


    var text: CharSequence = "这是一段文本这是一段文本这是一段文本这是一段文本这是一段文本这是一段文本这是一段文本这是一段文本"
    val paint = TextPaint()

    init {

        val span = SpannableStringBuilder(text)
        span.setSpan( TextAppearanceSpan(null, 0, 10.dp2px(context), null, null ),0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        text = span



        paint.textSize = 14.dp2px(context).toFloat()
        paint.color = Color.RED

//        val text = "aaa"
//        val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, 100.dp2px(context) )
////        layout.build().draw()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        StaticLayout.Builder.obtain(text, 0, text.length, paint, width).build().draw(canvas)

    }

}