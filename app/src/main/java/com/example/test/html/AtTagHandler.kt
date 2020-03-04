package com.example.test.html

import android.text.Editable
import com.example.test.html.richtext.AtSpan
import org.xml.sax.Attributes
import java.lang.Exception


/**
 * @人标签处理器<at userId='11'>userName</at>
 *
 * @author fortunexiao
 */
class AtTagHandler : BaseTagHandler() {

    override fun handleTag(opening: Boolean, tag: String, output: Editable?, attributes: Attributes?): Boolean {
        if ("at" == tag) {

            if (opening) {
                val userId = try {
                    attributes?.getValue("", "userId")?.toLong() ?: 0L
                } catch (e: Exception) {
                    0L
                }

                val hrefObj = AtSpan("", userId)
                start(output, hrefObj)


            } else {
                val h = getLast(output, AtSpan::class.java)
                h?.run {
                    setSpanFromMark(output, h, h)
                }
            }
            return true
        }
        return false
    }


}