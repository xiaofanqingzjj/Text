package com.bedrock.module_base.recycleradapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
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
        constructor(context: Context, data: List<T>, @LayoutRes private var itemLayoutId: Int, private var bindData: ((data: T, itemView: View?)->Unit)? = null) : BaseViewTypeAdapter<T>(context, data) {

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


/**
 * 快速给RecyclerView设置一组数据
 */
fun RecyclerView.quickAdapter(data: List<Any>, @LayoutRes itemLayoutId: Int, bindData: ((data: Any, itemView: View?)->Unit)? = null): RecyclerView.Adapter<*>? {

    this.adapter = QuickAdapter(
        context = context,
        data = data,
        itemLayoutId = itemLayoutId,
        bindData = bindData)

    layoutManager = object : LinearLayoutManager(context) {
        override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
            return RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    return this.adapter
}