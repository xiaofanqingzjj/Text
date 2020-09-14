package com.bedrock.module_base.recycleradapter

import android.content.Context
import android.view.View
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
constructor(context: Context, data: List<T>,  var itemLayoutId: Int, var bindData: ((position: Int, data: T, itemView: View?)->Unit)? = null) : BaseViewTypeAdapter<T>(context, data) {


    var itemViewCreated: ((itemView: View?, holder: BaseViewTypeAdapter.ViewTypeViewHolder<T>)->Unit)? = null

    constructor(context: Context,
                data: List<T>,
                itemLayoutId: Int,
                itemViewCreated: ((itemView: View?, holder: BaseViewTypeAdapter.ViewTypeViewHolder<T>)->Unit)? = null,
                bindData: ((position: Int, data: T, itemView: View?) -> Unit)?) :  this(context, data, itemLayoutId, bindData) {
        this.itemViewCreated = itemViewCreated

    }

    init {
        setViewTypeViewHolderHook(object : BaseViewTypeAdapter.AbsViewTypeViewHolderHook<T>() {
            override fun onViewHoldPostCreate(holder: ViewTypeViewHolder<T>?) {
                holder?.run {
                    setContentView(itemLayoutId)
                    itemViewCreated?.invoke(holder?.itemView, holder!!)

                }
            }

            override fun onViewHolderBindData(holder: ViewTypeViewHolder<T>?, data: T) {
                bindData(data, holder)
            }
        })
    }


    open fun bindData(data: T, viewHolder: ViewTypeViewHolder<T>?) {
        bindData?.invoke(viewHolder?.currentBindPosition ?: 0, data, viewHolder?.itemView)
    }


    fun getViewByPosition(recyclerView: RecyclerView?, position: Int, @IdRes viewId: Int): View? {


        return recyclerView?.findViewHolderForLayoutPosition(position)?.itemView?.findViewById(viewId)
    }
}


/**
 * 快速给RecyclerView设置一组数据
 *
 * @param data 数据了列表
 * @param itemLayoutId itemId
 * @param bindData 绑定数据
 */
fun <T> RecyclerView.quickAdapter(data: List<T>, @LayoutRes itemLayoutId: Int, orientation: Int = LinearLayoutManager.VERTICAL, bindData: ((position: Int, data: T, itemView: View?)->Unit)? = null): QuickAdapter<T> {
    return this.quickAdapter(data, itemLayoutId, orientation, null, bindData)
}

fun <T> RecyclerView.quickAdapter(data: List<T>,
                                  @LayoutRes itemLayoutId: Int,
                                  orientation: Int = LinearLayoutManager.VERTICAL,
                                  creatorView:((itemView: View?,  holder: BaseViewTypeAdapter.ViewTypeViewHolder<T>)->Unit)? = null,
                                  bindData: ((position: Int, data: T, itemView: View?)->Unit)? = null): QuickAdapter<T> {

    val adapter = QuickAdapter(
            context = context,
            data = data,
            itemLayoutId = itemLayoutId,
            itemViewCreated = creatorView,
            bindData = bindData)

    layoutManager = object : LinearLayoutManager(context) {

        init {
            this.orientation = orientation

        }

        override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
            return RecyclerView.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    this.adapter = adapter

    return adapter
}