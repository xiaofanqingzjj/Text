package com.example.test

import android.content.Context
import android.text.Editable
import android.text.Selection
import android.text.TextUtils
import android.text.TextUtils.TruncateAt
import android.text.method.ArrowKeyMovementMethod
import android.text.method.MovementMethod
import android.util.AttributeSet
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.EditText
import android.widget.TextView


open class EditText @JvmOverloads constructor(context: Context?,
                                              attrs: AttributeSet? = null,
                                              defStyleAttr: Int = 0, defStyleRes: Int = 0) :
        TextView(context,
                attrs,
                defStyleAttr,
                defStyleRes) {

    override fun getFreezesText(): Boolean {
        return true
    }

    override fun getDefaultEditable(): Boolean {
        return true
    }

    override fun getDefaultMovementMethod(): MovementMethod {
        return ArrowKeyMovementMethod.getInstance()
    }

    override fun getText(): Editable {
        return super.getText() as Editable
    }

    override fun setText(text: CharSequence, type: BufferType) {
        super.setText(text, BufferType.EDITABLE)
    }

    fun setSelection(start: Int, stop: Int) {
        Selection.setSelection(text, start, stop)
    }

    fun setSelection(index: Int) {
        Selection.setSelection(text, index)
    }

    fun selectAll() {
        Selection.selectAll(text)
    }

    /**
     * Convenience for [Selection.extendSelection].
     */
    fun extendSelection(index: Int) {
        Selection.extendSelection(text, index)
    }

    /**
     * Causes words in the text that are longer than the view's width to be ellipsized instead of
     * broken in the middle. [ TextUtils.TruncateAt#MARQUEE][TextUtils.TruncateAt.MARQUEE] is not supported.
     *
     * @param ellipsis Type of ellipsis to be applied.
     * @throws IllegalArgumentException When the value of `ellipsis` parameter is
     * [TextUtils.TruncateAt.MARQUEE].
     * @see TextView.setEllipsize
     */
    override fun setEllipsize(ellipsis: TruncateAt) {
        require(ellipsis != TruncateAt.MARQUEE) {
            ("EditText cannot use the ellipsize mode "
                    + "TextUtils.TruncateAt.MARQUEE")
        }
        super.setEllipsize(ellipsis)
    }

    override fun getAccessibilityClassName(): CharSequence {
        return EditText::class.java.name
    }

    /** @hide
     */
    protected fun supportsAutoSizeText(): Boolean {
        return false
    }

    /** @hide
     */
    fun onInitializeAccessibilityNodeInfoInternal(info: AccessibilityNodeInfo) {
//        super.onInitializeAccessibilityNodeInfoInternal(info)
        if (isEnabled) {
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_TEXT)
        }
    }
}
