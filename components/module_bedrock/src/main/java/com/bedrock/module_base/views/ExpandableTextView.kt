package com.bedrock.module_base.views

import android.content.Context
import android.graphics.Color
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import com.bedrock.module_base.R


/**
 *
 * 尾部支持展开和收起的TextView
 *
 *
 *  @author fortune
 */
class ExpandableTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : TextView(context, attrs, defStyleAttr) {

    companion object {


    }


    private var mExpendText = "全部"
    private var mCloseText = "收起"

    private var mText  : String? = null

    private var mMaxLines = 3

    private var mCloseSpan: SpannableString
    private var mExpendSpan: SpannableString

    private var mExpendBtnColor: Int = Color.RED

    /**
     * 表示当前是否展开
     */
    private var isExpend = false

    init {


        attrs?.run {
            val a = context.obtainStyledAttributes(this, R.styleable.ExpandableTextView)

            val expendColor = a.getColor(R.styleable.ExpandableTextView_etv_expend_btn_color, -1)
            val expendText = a.getString(R.styleable.ExpandableTextView_etv_expend_text)
            val closeText = a.getString(R.styleable.ExpandableTextView_etv_close_text)

            if (expendColor != -1) {
                mExpendBtnColor = expendColor
            }

            if (!expendText.isNullOrEmpty()) {
                mExpendText = expendText
            }

            if (!closeText.isNullOrEmpty()) {
                mCloseText = closeText
            }

            a.recycle()
        }

        // 点击展开的span
        mExpendSpan = SpannableString(mExpendText).apply {
            setSpan(MyClickSpan(mExpendBtnColor) {
                super@ExpandableTextView.setMaxLines(Int.MAX_VALUE)
                showExpendedState()
            }, 0, mExpendText.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        // 点击收起的span
        mCloseSpan = SpannableString(mCloseText).apply {
            setSpan(MyClickSpan(mExpendBtnColor) {
                super@ExpandableTextView.setMaxLines(mMaxLines)
                showClosedState()
            }, 0, mCloseText.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        movementMethod = LinkMovementMethod.getInstance()

        // 长按会crash
        isLongClickable = false
    }

    override fun setMaxLines(maxLines: Int) {
        mMaxLines = maxLines
        super.setMaxLines(maxLines)
    }

    fun setExpendableText(text: CharSequence?) {
        mText = text.toString()
        setText(mText)
    }

    /**
     * 收起状态
     */
    private fun showClosedState() {
        var text = mText ?: return

        var shouldAddExpendBtn = false

        if (maxLines != -1) {
            val layout = createTextLayout(text)

            if (layout.lineCount > maxLines) { //获取一行显示字符个数，然后截取字符串数
                var end = layout.getLineEnd(maxLines - 1)
                if (end >= text.length) {
                    end = text.length
                }
                text = text.substring(0, end).trim { it <= ' ' } // 收起状态原始文本截取展示的部分


                end = layout.getLineEnd(maxLines - 1)
                if (end >= text.length) {
                    end = text.length
                }
                val showText = text.substring(0, end).trim { it <= ' ' } + "..." + mExpendSpan

                var layout2 = createTextLayout(showText)

                // 对workingText进行-1截取，直到展示行数==最大行数，并且添加 SPAN_CLOSE 后刚好占满最后一行
                while (layout2.lineCount > maxLines) {
                    val lastSpace = text.length - 1
                    if (lastSpace == -1) {
                        break
                    }
                    text = text.substring(0, lastSpace)
                    layout2 = createTextLayout("$text...$mExpendSpan")
                }
                shouldAddExpendBtn = true
                text = "$text..."
            }
        }

        setText(text)

        if (shouldAddExpendBtn) { // 必须使用append，不能在上面使用+连接，否则spannable会无效
            append(mExpendSpan)
        }

        isExpend = false
    }



    /**
     * 展开状态
     */
    private fun showExpendedState() {
        val text = mText ?: return

        val layout1 = createTextLayout(text)
        val layout2 = createTextLayout(text + mCloseText)

        // 展示全部原始内容时 如果 TEXT_CLOSE 需要换行才能显示完整，则直接将TEXT_CLOSE展示在下一行
        if (layout2.lineCount > layout1.lineCount) {
            setText("$mText\n")
        } else {
            setText(mText)
        }

        append(mCloseSpan)
        isExpend = true

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (isExpend) {
            showExpendedState()
        } else {
            showClosedState()
        }
    }

    private fun createTextLayout(text: String?): Layout {
        return StaticLayout(text, paint, width - paddingLeft - paddingRight, Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineSpacingExtra, false)
    }


    internal class MyClickSpan(@ColorInt private val color: Int, private var onClickListener: ((v: View)->Unit)?) : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.color = color
            ds.isUnderlineText = false
        }

        override fun onClick(widget: View) {
            onClickListener?.invoke(widget)
        }
    }
}