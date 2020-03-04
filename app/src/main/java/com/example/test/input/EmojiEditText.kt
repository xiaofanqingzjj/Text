package com.example.test.input

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.appcompat.widget.AppCompatEditText
import com.bedrock.module_base.util.DensityUtil
import java.lang.Exception

/**
 * 扩展了EditText，用于插入表情。
 *
 * @author fortunexiao
 */
open class EmojiEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : AppCompatEditText(context, attrs) {

    private var mInputConnection: InputConnection? = null

    private var backPressedListener: BackPressedListener? = null


    private var mAutoParseFaceEditableFactory: AutoParseFaceEditableFactory? = null


    init {

        // 设置这个Factory
        val textSize = textSize.toInt() +  DensityUtil.dip2px(context, 6f)  // 比文字大小加几个像素

        mAutoParseFaceEditableFactory = AutoParseFaceEditableFactory.getInstance(getContext(), textSize)
        setEditableFactory(mAutoParseFaceEditableFactory)

    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        mInputConnection = super.onCreateInputConnection(outAttrs)
        return mInputConnection
    }



    /**
     * 向输入框插入一个表情
     *
     */
    fun insertEmoji(emoji: Emoji, size: Int = -1) {

        // 表情不存在，直接返回
        val faceDrawable = try {
            context.resources.getDrawable(emoji.f_resId)
        } catch (e: Exception){
            return
        }

        val textSize = if (size == -1) textSize.toInt() +  DensityUtil.dip2px(context, 6f) else size  // 比文字大小加几个像素
        faceDrawable.setBounds(0, 0, textSize, textSize)

        // 名字为空，直接返回
        val faceName = emoji.f_showName
        if (faceName.isNullOrEmpty()) {
            return
        }

        val spannableString = SpannableString(faceName)

        val lineSpace = lineSpacingExtra.toInt()
        val imageSpan = EmojiImageSpan(faceDrawable, ImageSpan.ALIGN_BASELINE, lineSpace)

        spannableString.setSpan(imageSpan, 0, faceName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        text?.insert(selectionStart, spannableString)

    }

    /**
     * 模拟点击DEL键
     */
    fun sendDelEvent() {
        val delKeyCode = KeyEvent.KEYCODE_DEL
        mInputConnection?.run {
            sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, delKeyCode))
            sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, delKeyCode))
        }
    }


    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        val result = super.onKeyPreIme(keyCode, event)

        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP ) {
            backPressedListener?.onBackPressed()
        }

        return result
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {

        val text = text!!

        var selStart = selStart
        var selEnd = selEnd
        if (selEnd < selStart) {
            return
        }
        if (selStart <= 0 && selEnd >= text.length) {
            //全选的时候不处理
            super.onSelectionChanged(0, text.length)
            return
        }

        //全不选的时候不处理
        if (selEnd == selStart) {
            super.onSelectionChanged(selStart, selEnd)
            return
        }

        //长按复制文字的时候，有表情的时候Selection需要修复
        try {

            val text = text!!
            var selectionIsFix = false

            val selectionText = text.subSequence(selStart, selEnd)
            val smileys = EmojiParsar.parseFace(context, StringBuilder(selectionText))
            val startPosition: Int
            val endPosition: Int
            if (smileys != null && !smileys.isEmpty()) {
                startPosition = smileys[0].startPosition
                endPosition = smileys[smileys.size - 1].endPosition
            } else {
                startPosition = 0
                endPosition = 0
            }

            if (startPosition == 0 && endPosition == selectionText.length) {
                //如果解析长度等于高亮文字的长度就不处理
                super.onSelectionChanged(selStart, selEnd)
                return
            }

            if (startPosition != 0) {
                //光标前不能解析成文字的字符串
                for (i in selStart downTo 0) {
                    if ("[" == text.subSequence(i, i + 1).toString()) {
                        //TODO 是否要判断fix后的是不是表情？
                        selStart = i
                        selectionIsFix = true
                        break
                    }
                }
            }

            if (endPosition != selectionText.length) {
                //光标后不能解析成文字的字符串
                for (i in selEnd until text.length) {
                    if ("]" == text.subSequence(i, i + 1).toString()) {
                        //TODO 是否要判断fix后的是不是表情？
                        selEnd = i + 1
                        selectionIsFix = true
                        break
                    }
                }
            }
            if (selectionIsFix) {
                setSelection(selStart, selEnd)
                return
            }

        } catch (ex: Exception) {
            //保证不crash
            ex.printStackTrace()
        }

        super.onSelectionChanged(selStart, selEnd)

    }



    fun setBackPressedListener(backPressedListener: BackPressedListener) {
        this.backPressedListener = backPressedListener
    }

    interface BackPressedListener {
        fun onBackPressed()
    }


}
