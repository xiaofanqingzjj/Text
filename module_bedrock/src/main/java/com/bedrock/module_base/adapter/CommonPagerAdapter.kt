package com.bedrock.module_base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * 一个通用的ViewPager
 *
 * @author fortunexiao
 */
open class CommonPagerAdapter<T>(var data: List<T>, var itemViewLayoutId: Int, var bindData: View.(pos: Int, data: T) -> Unit) : ViewPagerAdapter<View>() {

    override fun getCount(): Int {
        return data.size
    }

    override fun createView(container: ViewGroup, convertView: View?, position: Int): View {
        return LayoutInflater.from(container.context).inflate(itemViewLayoutId, container, false)
    }


    override fun bindView(view: View, position: Int) {
        view.bindData(position, data[position])
    }

    fun currentView(position: Int): View? {
        var view: View? = null
        instantiatedViews.map {
            if (it.value == position) {
                view = it.key
            }
        }
        return view
    }

}