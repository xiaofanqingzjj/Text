package com.example.test.input

import android.content.Context
import android.graphics.drawable.Drawable
import java.lang.Exception


/**
 * 表情相关的工具类
 *
 * @author fortune
 */
object EmojiUtil {



    /**
     * 根据表情文案获取表情图片
     *
     * @param context
     * @param name 表情文案，如[微笑]
     * @return
     */
    @JvmOverloads fun getEmojiDrawable(context: Context, name: String, size: Int = -1): Drawable? {

        val allEmojis = EmojiGenerator.getInstance(context).allEmojiList

        val emoji = allEmojis.firstOrNull {
            it.f_showName == name
        } ?: return null

        val drawable = try {
            context.resources.getDrawable(emoji.f_resId)
        } catch (e: Exception) {
            null
        } ?: return null

        val emojiSize = if (size == -1) {
            defaultFaceSize(context)
        } else size

        drawable.setBounds(0, 0, emojiSize, emojiSize)

        return drawable
    }

    fun indexOfEmoji(context: Context, name: String?): Int {
        val allEmojis = EmojiGenerator.getInstance(context).allEmojiList
        return allEmojis.indexOfFirst {
            it.f_showName == name
        }
    }


    private fun defaultFaceSize(context: Context): Int {
        return context.resources.getDimension(0).toInt()
    }

}
