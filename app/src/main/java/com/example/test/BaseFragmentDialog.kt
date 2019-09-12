package com.tencent.story.base.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment


/**
 * 对话框基类
 * @author fortunexiao
 */
abstract class BaseFragmentDialog : DialogFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 设置窗口样式
//        dialog.window?.run {
//            StatusBarUtil.setStatusBarMode(null, this, true)
//        }

        // 点击空白关闭窗口
        dialog.setCanceledOnTouchOutside(true)
    }

    // 当Dialog被取消的时候需要移除Fragment，系统默认没有做任何操作？很奇怪
    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        onDismiss(dialog)
    }
}