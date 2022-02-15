package com.example.test.test_float_comment

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import android.view.*
import com.bedrock.module_base.dialog.BaseFragmentDialog


/**
 * 一个通用到输入组件
 *
 * @author fortune
 */
class SimpleInputPanelContainer2() : BaseFragmentDialog() {

    companion object {
        const val TAG = "SimpleInputPanel"
    }

    private var mContext: Context? = null

    @SuppressLint("ValidFragment")
    constructor(context: Context) : this() {
        this.mContext = context
    }

    var inputPanel: InputPanel? = null

    var onCancel: (()->Unit)? = null
//    var onDismiss: (()->Unit)? = null

    var onPublishListener: ((text: String?)->Unit)? = null

    /**
     * 是否点击了发表
     */
    private var isClickPublish = false

    var hint: String? = null
    var draftKey: String? = null

    var publishText: String? = null
    var supportEmojiInput: Boolean = true

    // 数据上报相关
    var clickEmojiEventId: Long = 0
    var clickEmojiModuleId: Long = 0

    var clickPulishEventId: Long = 0
    var clickPublishModuleId: Long = 0

    init {
//
//        mInputPanel.onPublishListener = {
//            onPublishListener?.invoke(it)
//            isClickPublish = true
//            dismiss()
//        }
//
//        setContentView(mInputPanel)
//
//        setupWindowStyle()
//        setCanceledOnTouchOutside(true)

//        setOnCancelListener {
//            Log.d(TAG, "onCancel")
//            onCancel?.invoke()
//        }
//
//        setOnDismissListener {
//            Log.d(TAG, "onDismiss")
//
//            if (!isClickPublish) {
//                onCancel?.invoke()
//            }
//        }


    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        // 设置窗口样式
        dialog?.window?.run {


            val lp = attributes
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            lp.gravity = Gravity.BOTTOM
//            attributes = lp
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

            // 背景变灰

//            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
////            addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//
            val flags = attributes.flags
            attributes.flags = flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
//
//            if (Build.VERSION.SDK_INT > 18) {
//                attributes.flags =  attributes.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//            }
            attributes.dimAmount = 0.7f


//            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }

//        dialog?.setOnDismissListener {
//            inputPanel?.release()
//        }


    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        inputPanel?.release()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inputPanel = InputPanel(context!!).apply {
            // 数据上报相关
            this.clickEmojiEventId = this@SimpleInputPanelContainer2.clickEmojiEventId
            this.clickEmojiModuleId = this@SimpleInputPanelContainer2.clickEmojiModuleId

            this.clickPublishEventId = this@SimpleInputPanelContainer2.clickPulishEventId
            this.clickPublishModuleId = this@SimpleInputPanelContainer2.clickPublishModuleId

            onPublishListener = {
                this@SimpleInputPanelContainer2.onPublishListener?.invoke(it)
                isClickPublish = true
                dismiss()
            }

            onDismiss = {
                dismiss()
                onSaveDraft()
            }

            this.hint = this@SimpleInputPanelContainer2.hint

            this.draftKey = this@SimpleInputPanelContainer2.draftKey

            this.publishText = this@SimpleInputPanelContainer2.publishText
            this.supportEmojiInput = this@SimpleInputPanelContainer2.supportEmojiInput

            restoreDraft()
        }


        return inputPanel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun getTheme(): Int {
        return android.R.style.Theme_Material_Panel
    }

    fun show() {
        (mContext as? FragmentActivity)?.supportFragmentManager?.let {
            show(it, "InputPanel")
        }
    }



//    private fun setupWindowStyle() {
//        window?.run {
//            val lp = attributes
//            lp.width = WindowManager.LayoutParams.MATCH_PARENT
//            lp.height = WindowManager.LayoutParams.MATCH_PARENT
//            lp.gravity = Gravity.BOTTOM
//            attributes = lp
//            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
//        }
//    }

}
