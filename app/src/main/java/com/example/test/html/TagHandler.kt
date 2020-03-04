package com.example.test.html


import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import com.example.test.html.richtext.AtSpan
import com.example.test.html.richtext.LinkSpan
import java.lang.Exception
import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern


object ATagToJson {




    @JvmStatic
    fun main(args: Array<String>) {
        val testStr = "<a href=\"http://www.baidu.com\">百度</a>asdfa<a    href='http://www.google.com'>谷歌</a>"

        val list = TagHandler.parseLinkTag(testStr)

        println(list)
    }





}

object RichTextParser {

    fun parse(context: Context, text: String?): Spanned? {

        if (text.isNullOrEmpty()) return null

        val links = TagHandler.parseLinkTag(text)
        val ats = TagHandler.parseAtTag(text)

        val ssb = SpannableStringBuilder(text)



        links.forEach {

//            ssb.replace(it.start,)
            ssb.setSpan(LinkSpan(context, it.content, it.href), it.start, it.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        ats.forEach {
//            ssb.setSpan(AtTagHandler.AtSpan)
            ssb.setSpan(AtSpan(it.content, it.userId), it.start, it.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return ssb

    }
}


object TagHandler {
    fun <T> parse(text: String, pattern: Pattern, parseMatch: (m: Matcher)->T): List<T> {
        val matcher = pattern.matcher(text)
        val list = ArrayList<T>()
        while (matcher.find()) {
            val tag = parseMatch(matcher)
            list.add(tag)
        }
        return list
    }


    val LINK_PATTERN = Pattern.compile("<a[^>]*href=[\"|'](.*?)[\"|']>([^<]*)</a>")


    val AT_PATTERN = Pattern.compile("<at[^>]*userId=[\"|'](.*?)[\"|']>([^<]*)</at>")


    fun parseLinkTag(text: String): List<LinkTag> {
        return parse(text, LINK_PATTERN) {
            LinkTag().apply {
                href = it.group(1)
                content = it.group(2)
                start = it.start()
                end = it.end()
            }
        }
    }

    fun parseAtTag(text: String): List<AtTag> {
        return parse(text, AT_PATTERN) {
            AtTag().apply {
                userId = try {it.group(1).toLong()} catch (e: Exception) {0L}
                content = it.group(2)
                start = it.start()
                end = it.end()
            }
        }
    }
}

open class BaseTag(
        /**
         * 标签里的内容
         */
        var content: String? = null,

        /**
         * 标签的开始位置
         */
        var start: Int = 0,

        /**
         * 标签的结束位置
         */
        var end: Int = 0)


data class LinkTag(var href: String? = null): BaseTag()
data class AtTag(var userId: Long = 0): BaseTag()


