package com.example.test.html.richtext

import android.graphics.Color
import android.text.TextPaint
import android.view.View


/**
 * at人和链接的的基类
 *
 * @author fortunexiao
 */
open class BaseLinkSpan(text: String?, var url: String?) : ReplaceTextSpan(text), ClickableSpan {


    override fun updateDrawState(ds: TextPaint?) {
        super.updateDrawState(ds)

        ds?.color = Color.BLUE
    }


    override fun onClick(v: View) {
    }


}


