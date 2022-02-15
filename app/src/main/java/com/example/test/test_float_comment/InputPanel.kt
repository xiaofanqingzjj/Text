package com.example.test.test_float_comment

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.eclipsesource.v8.Releasable
import com.example.test.R


/**
 * 输入框
 *
 * @author fortunexiao
 */
class InputPanel @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) : FrameLayout(context, attributeSet), Releasable {


    companion object {
        const val TAG = "InputPanel"
    }


    private var maxInputLength = 200

    private var llContent: ViewGroup? = null

    private var mEditText: EditText? = null

    private var mCbEmoji: ImageView? = null
    private var mBtnSend: TextView? = null

    private var mEmojiPanelContainer: View? = null
//    private var mEmojiPanel: EmojiPanel? = null


//    private var softKeyboardStateHelper: SoftKeyboardStateHelper


    var onDismiss: (()->Unit)? = null

    /**
     * 点击发表监听器
     */
    var onPublishListener: ((text: String?)->Unit)? = null

    var isClickSwitch : Boolean = false

    var hint: String? = null
        set(value) {
            field = value
            mEditText?.hint = value
        }

    var publishText: String? = null
        set(value) {
            field = value
            mBtnSend?.text = if (!publishText.isNullOrEmpty()) publishText else "发布"
        }

    var supportEmojiInput: Boolean = true
        set(value) {
            field = value
//            if (supportEmojiInput) {
            mCbEmoji?.visibility = if (supportEmojiInput) View.VISIBLE else View.GONE
//            }
        }

    var draftKey: String? = null

    // 数据上报相关
    var clickEmojiEventId: Long = 0
    var clickEmojiModuleId: Long = 0

    var clickPublishEventId: Long = 0
    var clickPublishModuleId: Long = 0


    private val onClickListener: View.OnClickListener  = View.OnClickListener {
        when (it.id) {
            R.id.cb_emoji -> {
                switchKeyboardAndEmoji()
                isClickSwitch = true
            }
            R.id.btn_send -> {

                val text = mEditText?.text?.toString() ?: ""

                if (text.length > maxInputLength) {
//                    TGTToast.showToast("评论最长为200字")
                } else {
                    onPublishListener?.invoke(mEditText?.text.toString())
//                    MtaHelper.trackCustomKVEvent(clickPublishEventId, clickPublishModuleId)
                }
            }
            R.id.ll_content -> onDismiss?.invoke()
        }
    }

//
//    private val onEmojiSelectListener: OnEmojiSelectListener = object : OnEmojiSelectListener {
//        override fun OnEmojiSelect(emoji: Emoji?) {
//            emoji?.run {
//                Log.d(TAG, "onEmojiSelect:$this")
//                mEditText?.insertEmoji(this)
//            }
//        }
//
//        override fun OnEmojiDelete() {
//            mEditText?.sendDelEvent()
//        }
//
//    }
//
//    private var softKeyBoardListener: SoftKeyBoardListener


    init {
        LayoutInflater.from(context).inflate(R.layout.simple_input_panel, this)
        bindViews()

//        softKeyboardStateHelper = SoftKeyboardStateHelper(getActivity(context)!!.window.decorView)
//        softKeyboardStateHelper.addSoftKeyboardStateListener()

        if (!hint.isNullOrEmpty()) {
            mEditText?.hint = hint
        }

        if (!publishText.isNullOrEmpty()) {
            mBtnSend?.text = publishText
        }

        mCbEmoji?.visibility = if (supportEmojiInput) View.VISIBLE else View.GONE

//        softKeyBoardListener = SoftKeyBoardListener(getActivity(context)!!);
//        softKeyBoardListener.setOnSoftKeyBoardChangeListener(object : SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
//            override fun keyBoardShow(height: Int) {
//                Log.d(TAG, "keyBoardShow:$height")
//                ajustEmojiPanelHeight(height)
//
//                showEmojiPanel(false)
//            }
//
//            override fun keyBoardHide(height: Int) {
//                Log.d(TAG, "keyBoardHide:$height")
//
//                if (isClickSwitch) {
//                    showEmojiPanel(true)
//                    isClickSwitch = false
//                } else { // 键盘关闭的时候直接关闭输入框
//                    onDismiss?.invoke()
//                }
//            }
//        })

        mEditText?.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                mBtnSend?.isEnabled = s?.toString()?.length ?: 0 > 0
            }
        })

        // 草稿
//        restoreDraft()
    }



    private fun bindViews() {
        llContent = findViewById(R.id.ll_content)
        llContent?.setOnClickListener(onClickListener)

        mEditText = findViewById(R.id.text_input)
        mEditText?.setOnClickListener(onClickListener)


        mCbEmoji = findViewById(R.id.cb_emoji)
        mCbEmoji?.setOnClickListener(onClickListener)

        mBtnSend = findViewById(R.id.btn_send)
        mBtnSend?.setOnClickListener(onClickListener)
        mBtnSend?.isEnabled = false

        mEmojiPanelContainer = findViewById(R.id.fl_emoji_container)

//        mEmojiPanel = findViewById(R.id.emoji_panel)

//        mEmojiPanel?.registerOnEmojiSelectListener(onEmojiSelectListener)

    }

    fun restoreDraft() {
        draftKey ?: return

//        val draft = SimpleCommentDraftManager.getContent(draftKey) ?: return
//        mEditText?.run {
//            setText(draft)
//            setSelection(draft.length)
//        }
    }

    fun onSaveDraft() {
//        draftKey ?: return
//        val text = mEditText?.text?.toString() ?: return
//        SimpleCommentDraftManager.putContent(draftKey, text)
    }


//    private fun ajustEmojiPanelHeight(keyBoardHeight: Int) {
//        runUIThread {
//            mEmojiPanelContainer?.layoutParams?.run {
//                if (height != keyBoardHeight) {
//                    height = keyBoardHeight
//                    mEmojiPanelContainer?.requestLayout()
//                }
//            }
//        }
//
//    }

    private fun showEmojiPanel(show: Boolean) {
        if (show) {
            mEmojiPanelContainer?.visibility = View.VISIBLE
        } else {
            mEmojiPanelContainer?.visibility = View.GONE
        }
    }


    /**
     * 输入法和表情之间的相互切换
     */
    private fun switchKeyboardAndEmoji() {
        if (rootView != null && KeyboardUtil.isKeyBoardShown(rootView)) { //输入法切换到表情
//            mCbEmoji?.setImageResource(R.drawable.ic_input_keyboard)
            KeyboardUtil.hideKeybord(mEditText) // 隐藏键盘，监听器检测到键盘关闭会显示表情

//            MtaHelper.trackCustomKVEvent(clickEmojiEventId, clickEmojiModuleId)
        } else { //表情切换到输入法
//            mCbEmoji?.setImageResource(R.drawable.ic_input_face)
            KeyboardUtil.showKeybord(mEditText)


        }
    }



    override fun release() {

//        softKeyBoardListener?.release()
    }
}