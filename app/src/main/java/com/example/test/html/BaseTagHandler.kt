package com.example.test.html

import android.content.Context
import android.text.Editable
import android.text.Spannable
import android.text.Spanned
import org.xml.sax.Attributes


/**
 * 标签处理器
 *
 * @author fortunexiao
 */
abstract class BaseTagHandler : RichTextHelper.TagHandler {

    companion object {

        /**
         * 在尾部添加一个标记对象
         */
        internal fun start(text: Editable?, mark: Any) {
             text?.run {
                 val len = text.length
                 text.setSpan(mark, len, len, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
             }

        }

        /**
         * 把repl替换掉当前text里类型为kind的标记对象
         */
         internal fun end(text: Editable?, kind: Class<*>, repl: Any) {

            text?.run {
                val obj = getLast<Any>(text, kind as Class<Any>)
                if (obj != null) {

                    // 替换
                    setSpanFromMark(text, obj, repl)
                }
            }

        }


          fun <T> getLast(text: Spanned?, kind: Class<T>): T? {
            /*
         * This knows that the last returned object from getSpans()
         * will be the most recently added.
         */
            val objs = text?.getSpans(0, text.length, kind) ?: return null

            return if (objs.isEmpty()) {
                null
            } else {
                objs[objs.size - 1]
            }
        }


         fun setSpanFromMark(text: Spannable?, mark: Any, vararg spans: Any) {

             text?.run {
                 val where = text.getSpanStart(mark)
                 text.removeSpan(mark)
                 val len = text.length

                 if (where != len) {
                     for (span in spans) {
                         text.setSpan(span, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                     }
                 }
             }

        }
    }

    var startPos: Int = -1

    override fun handleTag(opening: Boolean, tag: String, output: Editable?, attributes: Attributes?): Boolean {
//        if (opening) {
//            start(output, this)
//        }
        return false
    }
}