package com.example.test

import android.text.Selection
import android.text.Spanned
import android.text.style.ClickableSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView


/**
 * 一个自定义的MovementMethod
 *
 * 点击的时候不会滚动TextView
 *
 * @author  fortunexiao
 */
object ClickableSpanTouchListener : View.OnTouchListener {


    override fun onTouch(v: View, event: MotionEvent): Boolean {

        val widget = v as? TextView


        if (widget != null) {
            val text = widget.text
            val buffer = text as? Spanned

//            SpannedString
            val action = event.action

            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                var x = event.x.toInt()
                var y = event.y.toInt()

                x -= v.totalPaddingLeft
                y -= v.totalPaddingTop

                x += v.scrollX
                y += v.scrollY


                // 找到用户点击的字符
                val layout = v.layout

                // 找到用户点击的行
                val line = layout.getLineForVertical(y)

                //
                val off = layout.getOffsetForHorizontal(line, x.toFloat())

                Log.d("ClickableSpanTouchListener", "off:$off, length:${buffer?.length}")

                // 如果off等于字符的长度，表示已经是最后一个字符了
                if (off != buffer?.length) {

                    // 根据用户点击的字符找到对应的Span
                    val links = buffer?.getSpans(off, off, ClickableSpan::class.java)

                    if (links?.isNotEmpty() == true) {
                        if (action == MotionEvent.ACTION_UP) {
                            links[0].onClick(v)
                        }
                        return true
                    }
                }
            }
        }


//        val action = event.action
//
//        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
//            var x = event.x.toInt()
//            var y = event.y.toInt()
//
//            x -= widget.getTotalPaddingLeft()
//            y -= widget.getTotalPaddingTop()
//
//            x += widget.getScrollX()
//            y += widget.getScrollY()
//
//            val layout = widget.getLayout()
//            val line = layout.getLineForVertical(y)
//            val off = layout.getOffsetForHorizontal(line, x.toFloat())
//
//            val links = buffer.getSpans(off, off, ClickableSpan::class.java)
//
//            if (links.size != 0) {
//                if (action == MotionEvent.ACTION_UP) {
//                    links[0].onClick(widget)
//                } else if (action == MotionEvent.ACTION_DOWN) {
//                    Selection.setSelection(buffer,
//                            buffer.getSpanStart(links[0]),
//                            buffer.getSpanEnd(links[0]))
//                }
//                return true
//            } else {
//                Selection.removeSelection(buffer)
//            }

        return false
    }

//    companion object {
//
//        val instance: ClickableSpanTouchListener
//            get() {
//                if (sInstance == null) sInstance = ClickableSpanTouchListener()
//                return sInstance!!
//            }
//
//        private var sInstance: ClickableSpanTouchListener? = null
//    }
}