package com.bedrock.module_base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment


/**
 * 一个快捷创建Fragment的类
 *
 * 用来创建简单的界面有奇效
 *
 * @author fortune
 */
open class SimpleFragment(var contentViewId: Int = 0, var onViewCreated:((contentView: View)->Unit)? = null) : Fragment() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(contentViewId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewCreated?.invoke(view)

    }

    fun setContentView(@LayoutRes contentViewId: Int) {
        this.contentViewId = contentViewId
    }

    fun <T : View?> findViewById(@IdRes id: Int): T? {
        return view?.findViewById<T>(id)
    }



}