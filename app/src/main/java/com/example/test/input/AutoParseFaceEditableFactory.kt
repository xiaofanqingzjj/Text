package com.example.test.input

import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder

//自动解析表情
internal class AutoParseFaceEditableFactory : Editable.Factory() {

    companion object {

        private val sInstance = AutoParseFaceEditableFactory()

        fun getInstance(context: Context, emojiSize: Int): AutoParseFaceEditableFactory {
            sInstance.context = context.applicationContext
            sInstance.emojiSize = emojiSize

            return sInstance
        }
    }


    private var emojiSize: Int = 0
    private var context: Context? = null


    override fun newEditable(source: CharSequence): Editable {
        return FaceSpannableStringBuilder(source, emojiSize)
    }


    //face support//
    class FaceSpannableStringBuilder(text: CharSequence, var emojiSize: Int) : SpannableStringBuilder(parseFace(sInstance.context, text, emojiSize)) {


        override fun replace(start: Int, end: Int, charSequence: CharSequence, tbstart: Int, tbend: Int): SpannableStringBuilder {
            val newTb = parse(charSequence)
            return super.replace(start, end, parse(charSequence), tbstart, newTb!!.length)
        }

        private fun parse(charSequence: CharSequence): CharSequence? {
            return parseFace(sInstance.context, charSequence,  emojiSize)
        }

        companion object {

            internal fun parseFace(context: Context?, source: CharSequence, emojiSize: Int): CharSequence? {
                if (source.length <= 2 || context == null) {
                    return source //太短了！！！
                }
                return EmojiParsar.parse(context, source, emojiSize)
            }
        }

    }



}