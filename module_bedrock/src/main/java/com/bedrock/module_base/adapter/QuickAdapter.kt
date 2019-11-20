package com.bedrock.module_base.adapter

import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView


/**
 * 一个快捷的创建Adapter的类
 *
 * @author fortune
 */
open class QuickAdapter<T>

        /**
         *
         * @param context
         * @param data 数据列表
         * @param itemLayoutId ItemLayoutId
         * @param bindData 数据绑定到ItemView上
         */
        constructor(context: Context, data: List<T>,  private var itemLayoutId: Int, private var bindData: ((data: T, itemView: View?)->Unit)? = null) : BaseViewTypeAdapter<T>(context, data) {

    init {
        setViewTypeViewHolderHook(object : BaseViewTypeAdapter.AbsViewTypeViewHolderHook<T>() {
            override fun onViewHoldPostCreate(holder: ViewTypeViewHolder<T>?) {
                holder?.setContentView(itemLayoutId)
            }

            override fun onViewHolderBindData(holder: ViewTypeViewHolder<T>?, data: T) {
                bindData(data, holder)
            }
        })
    }


    open fun bindData(data: T, viewHolder: ViewTypeViewHolder<T>?) {
        bindData?.invoke(data, viewHolder?.itemView)
    }



    fun getViewByPosition(recyclerView: RecyclerView?, position: Int, @IdRes viewId: Int): View? {


        return recyclerView?.findViewHolderForLayoutPosition(position)?.itemView?.findViewById(viewId)
    }
}