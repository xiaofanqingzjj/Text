package com.example.test.input

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan

import java.lang.Exception
import java.util.ArrayList
import java.util.regex.Pattern

/**
 *
 * 内部表情解析器
 *
 * @author fortunexiao
 */
object EmojiParsar {

    private val FACE_PATTERN = Pattern.compile("\\[{1}([\u4e00-\u9fa5]*)\\]{1}") // 匹配“[中文字符]”，比如“[你好啊]”，捕获的是[]之间的部分

    // =======================================================================================


    /**
     * 解析带表情(比如[色]、[你好]等鞥)的文本，把表情解析成ImageSpan并返回SpannableStringBuilder
     * @param context 上下文
     * @param content 文本
     * @param emojiSize 可以设置想要的表情大小，如果不传就是默认大小
     * @return 返回解析后的SpannableStringBuilder
     */
    @JvmOverloads fun parse(context: Context, content: CharSequence?, emojiSize: Int = -1): CharSequence? {

        // 先简单做下判断
        if (content.isNullOrEmpty() || !content.contains("[")) {
            return content
        }


        val matcher = FACE_PATTERN.matcher(content)

        val builder = SpannableStringBuilder(content)
        while (matcher.find()) {

            val name = "[${matcher.group(1)}]"



            val drawable = EmojiUtil.getEmojiDrawable(context, name, emojiSize) ?: continue
            val span = EmojiImageSpan(drawable, ImageSpan.ALIGN_BASELINE)

            try {
                builder.setSpan(span, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } catch (e: Exception) {
                // ignore
            }
        }
        return builder
    }


    class FaceElement {
        var index: Int = 0
        var code: String? = null
        var startPosition: Int = 0
        var endPosition: Int = 0
    }

    fun parseFace(context: Context, sb: StringBuilder): ArrayList<FaceElement> {
        val faceElements = ArrayList<FaceElement>()

        val m = FACE_PATTERN.matcher(sb)

        while (m.find()) {
            val start = m.start()
            val end = m.end()
            val emotionCode = m.group()

            val index = EmojiUtil.indexOfEmoji(context, "[${m.group(1)}]")

            if (index != -1) {
                val element = FaceElement().apply {
                    this.startPosition = start
                    this.endPosition = end
                    this.code = emotionCode
                    this.index = index
                }

                faceElements.add(element)
            }
        }
        return faceElements
    }

}
