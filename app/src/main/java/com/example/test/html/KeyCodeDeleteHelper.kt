package com.example.test.html

import android.text.Selection
import android.text.Spannable
import android.util.Log
import com.example.test.html.richtext.BaseLinkSpan


/**
 *
 * @author fortunexiao
 */
internal class KeyCodeDeleteHelper private constructor(){

    companion object {

        const val TAG = "KeyCodeDeleteHelper"

        fun onDelDown(text: Spannable): Boolean {
            val selectionStart = Selection.getSelectionStart(text)
            val selectionEnd = Selection.getSelectionEnd(text)

            Log.d(TAG, "selectionStart:$selectionStart, selectionEnd:$selectionEnd")

            // 当前的光标前面是否是一个span，如果是则选择span，这样删除的时候会把整个span删除

            val myClickSpans = text.getSpans(selectionStart, selectionEnd, BaseLinkSpan::class.java)
            val currentEndSpan = myClickSpans.firstOrNull {
                text.getSpanEnd(it) == selectionStart
            }

            // 光标前面有span
            currentEndSpan?.run {
                if (selectionStart == selectionEnd) { // 当前是闪烁光标，不是选择光标，则做选择操作
                    val start = text.getSpanStart(this)
                    val end = text.getSpanEnd(this)
                    Selection.setSelection(text, start, end)
                }
            }

            return false
        }
    }
}