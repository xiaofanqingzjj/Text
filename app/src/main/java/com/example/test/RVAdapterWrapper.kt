package com.example.test

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter

/**
 * 一个把BaseAdapter转换成RecyclerViewAdapter的包装器
 */
class RVAdapterWrapper(var orgAdapter: BaseAdapter) : Adapter<RVAdapterWrapper.RVWrapperVH>() {

    private var parent: ViewGroup? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVWrapperVH {
        this.parent = parent
        val view = orgAdapter.getView(0, null, parent);
        return RVWrapperVH(view)
    }

    override fun onBindViewHolder(holder: RVWrapperVH, position: Int) {
        orgAdapter.getView(position, holder.itemView, parent)
    }

    override fun getItemCount(): Int {
        return orgAdapter.count
    }

    class RVWrapperVH(var view: View) : RecyclerView.ViewHolder(view)
}