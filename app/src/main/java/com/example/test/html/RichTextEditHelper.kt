package com.example.test.html

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import com.example.test.R
import com.example.test.html.richtext.AtSpan
import com.example.test.html.richtext.BaseLinkSpan
import com.example.test.html.richtext.LinkSpan

class RichTextEditHelper  {


    companion object {

    }


}


class OnDelKeyListener: View.OnKeyListener {
    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        return (v as? EditText)?.run {
            if (keyCode == KeyEvent.KEYCODE_DEL && event?.action == KeyEvent.ACTION_DOWN) {
                KeyCodeDeleteHelper.onDelDown(v.text)
            }
            false
        } ?: false
    }
}


private val onDelKeyListener = OnDelKeyListener()

private val onSelectionSpanWatcher = SelectionSpanWatcher<BaseLinkSpan>(BaseLinkSpan::class)


/**
 * 让EditText支持富文本：link和@
 */
fun EditText.supportRichInput() {
    setOnKeyListener(onDelKeyListener)
    setEditableFactory(NoCopySpanEditableFactory(onSelectionSpanWatcher))
}

/**
 * 向输入框添加一个超链接
 */
fun EditText.appendLink(name: String, url: String) {
    val spannableString = SpannableStringBuilder().apply {
        // 添加一个链接小图标
        append(name, LinkSpan(context, name, url), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE )
    }
    text?.insert(selectionStart, spannableString)
}


/**
 * 向输入框添加一个@user标签
 */
fun EditText.appendAt(userName: String, userId: Long) {
    val spannableString = SpannableStringBuilder().apply {
        // 添加一个链接小图标
        append("@$userName", AtSpan(userName, userId), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    text?.insert(selectionStart, spannableString)
}


/**
 * 如果向编辑器添加了【@user】或者[link]的时候，该方法可以获取对应内容的标签文本
 */
fun EditText.getPrimitiveText() : String? {
    val span = SpannableStringBuilder(text)

    return span.apply {
        val spans = getSpans(0, length, BaseLinkSpan::class.java)
        spans?.mapIndexed { index, baseLinkSpan ->

            val start = getSpanStart(baseLinkSpan)
            val end = getSpanEnd(baseLinkSpan)

            var name = subSequence(start, end)

            if (name.isNotEmpty()) {
                when (baseLinkSpan) {
                    is AtSpan -> {
                        val atTag = "<at userId=\"${baseLinkSpan.userId}\">$name</at>"
                        replace(start, end, atTag)
                    }
                    is LinkSpan -> {
                        // 如果是链接，去掉前面的#号
                        name = name.substring(1)
                        val linkTag = "<LINK_PATTERN href=\"${baseLinkSpan.url}\">$name</LINK_PATTERN>"
                        replace(start, end, linkTag)
                    }
                }
            }
        }
    }.toString()
}

