package com.bedrock.module_base.dialog

import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager


/**
 * 基础样式对话框，带有Dialog的动画和背后变暗的效果
 * @author fortunexiao
 */
abstract class BaseStyleDialog : BaseFragmentDialog() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 设置窗口样式
        dialog?.window?.run {

            val windowWidth = context.resources.displayMetrics.widthPixels
            val width = (windowWidth * 0.8).toInt()
            setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

            // 默认使用Dialog的弹出动画
            attributes.windowAnimations = android.R.style.Animation_Dialog

            // 背景变灰
            val flags = attributes.flags
            attributes.flags = flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
            attributes.dimAmount = 0.7f
        }
    }

    override fun getTheme(): Int {
        // 去掉Dialog默认的样式
        return android.R.style.Theme_Panel
    }
}