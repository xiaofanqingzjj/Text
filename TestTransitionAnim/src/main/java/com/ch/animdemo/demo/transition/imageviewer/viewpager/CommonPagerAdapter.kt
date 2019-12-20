package com.tencent.story.common.widget.followviewpager

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.ch.animdemo.demo.transition.imageviewer.viewpager.ViewPagerAdapter

/**
 * 一个通用的ViewPager
 *
 * @author fortunexiao
 */
class CommonPagerAdapter<T>(
        var data: List<T>,
        var itemViewLayoutId: Int,
        var bindData: (View.(pos: Int, data: T) -> Unit)) : ViewPagerAdapter<View>() {

    companion object {
        const val TAG = "CommonPagerAdapter"
    }


    override fun createView(container: ViewGroup, convertView: View?, position: Int): View {
        var cv = convertView
        if (cv == null) {
            cv = LayoutInflater.from(container.context).inflate(itemViewLayoutId, container, false)
        }
        return cv!!
    }

    override fun getItemPosition(`object`: Any): Int {
        return newPos(`object`)
    }

    override fun getCount(): Int {
        return data.size
    }


    override fun bindView(view: View, position: Int) {
        if (position < data.size) {
            val data = data[position]
            view.bindData(position, data)
            view.tag = data
        }
    }

    /**
     * 新位置
     */
    protected open fun newPos(obj: Any): Int {
        val itemData = (obj as? View)?.tag
        var newPos = data.indexOf(itemData)
        if (newPos == -1) {
            newPos = PagerAdapter.POSITION_NONE
        }
        Log.d(TAG, "newPos:$newPos")
        return newPos
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


