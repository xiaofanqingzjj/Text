package com.example.test.html

import android.text.Editable
import android.text.SpannableStringBuilder
import com.example.test.html.richtext.LinkSpan
import org.xml.sax.Attributes


/**
 * html <LINK_PATTERN />标签解析器
 *
 * @author fortunexiao
 */
class LinkTagHandler() : BaseTagHandler() {

    override fun handleTag(opening: Boolean, tag: String, output: Editable?, attributes: Attributes?): Boolean {
        if ("LINK_PATTERN" == tag) {

            if (opening) {
                val href = attributes?.getValue("", "href")
//                val hrefObj = LinkSpan(href)
//                start(output, hrefObj)

                // 添加一个链接小图标
                (output as? SpannableStringBuilder)?.run {
//                    append("-", CenterVerticalImageSpan(context, R.drawable.ic_link), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

            } else {
                val h = getLast(output, LinkSpan::class.java)
                h?.url?.run {
                    setSpanFromMark(output, h, h)
                }
            }
            return true
        }
        return false
    }


}