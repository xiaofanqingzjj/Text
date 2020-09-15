package com.tencent.widget.fontview

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.tencent.widget.R

/**
 * 可以设置第三方字体的TextView
 *
 * @author  fortunexiao
 */
class FontTextView : AppCompatTextView {
    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }


    private fun init(attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }
        val a = context.obtainStyledAttributes(attrs, R.styleable.FontTextView)
        val fontPath = a.getString(R.styleable.FontTextView_fontName)
        setFont(fontPath)
        a.recycle()
    }

    fun setFont(fontPath: String?) {
        if (TextUtils.isEmpty(fontPath)) {
            return
        }
        setFont(this, fontPath!!)
    }

    companion object {
        const val TAG = "FontTextView"

        /**
         * 可以给TextView设置第三方字体
         * @param textView TextView
         * @param fontPath 字体Path，在asset目录下的path
         */
        fun setFont(textView: TextView, fontPath: String) {
            try {
                val customFont = Typeface.createFromAsset(textView.context.assets, fontPath)
                textView.typeface = customFont
            } catch (e: Exception) {
                Log.e(TAG, "setFont error:" + e.message)
            }
        }
    }
}