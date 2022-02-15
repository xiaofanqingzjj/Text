package com.example.test.test_float_comment

import android.content.Context
import androidx.fragment.app.FragmentActivity


/**
 * 弹出回复面板
 *
 */
object InputPanelHelper {

    /**
     * 在底部弹出一个输入面板
     *
     *
     */
    @JvmOverloads fun showInputPanel(context: Context,
                       hint: String? = null,
                       draftKey: String? = null,
                     publishText: String? = null,
                     supportEmoji: Boolean = true,
                       onPublish: ((content: String?)->Unit)? = null,
                       onCancel: (()->Unit)? = null) {

        (context as? FragmentActivity)?.run {
            SimpleInputPanelContainer2(context).apply {

                this.hint = hint

                this.onPublishListener = onPublish
                this.onCancel = onCancel

                this.draftKey = draftKey

                this.publishText = publishText
                this.supportEmojiInput = supportEmoji

                // 数据上报相关
                this.clickEmojiEventId = 50712
                this.clickEmojiModuleId = 2070

                this.clickPulishEventId = 50713
                this.clickPublishModuleId = 2070

            }.show()
        }


    }

}