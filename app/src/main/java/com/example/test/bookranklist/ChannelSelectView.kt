package com.example.test.bookranklist

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bedrock.module_base.recycleradapter.QuickAdapter
import com.bedrock.module_base.recycleradapter.quickAdapter
import com.example.test.R
import kotlinx.android.synthetic.main.rv_channel_item.view.*


/**
 *
 */
class ChannelSelectView(context: Context, attributeSet: AttributeSet? = null) : RecyclerView(context, attributeSet) {

    private val mData = mutableListOf<Channel>()
    private var mAdapter: QuickAdapter<Channel>



    init {
        horizontal()

        mAdapter = quickAdapter(mData,
                itemLayoutId = R.layout.rv_channel_item,
                orientation = HORIZONTAL,
                creatorView = {itemView, holder ->
                    itemView?.setOnClickListener {
                        selectChannel(holder.currentBindData)
                    }
                },
                bindData = {position, data, itemView ->  
                    itemView?.run {
                        tv_name.text = data.name
                        if (data.isSelected) {
//                            anim(tv_name, 1.4f)
                            tv_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
                        } else {
//                            anim(tv_name, 1f)
                            tv_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
                        }
                    }
                })
    }

    private fun anim(view: View, targetScale: Float) {
        AnimatorSet().apply {
            playTogether(*mutableListOf<ObjectAnimator>().apply {
                addAll(scale(view, targetScale))
            }.toTypedArray())
            duration = 150
        }.start()
    }

    /**
     * 返回做缩放动画的集合
     */
    private fun scale(v: View, vararg values: Float): List<ObjectAnimator> {
        return mutableListOf<ObjectAnimator>().apply {
            add(ObjectAnimator.ofFloat(v, View.SCALE_X, *values))
            add(ObjectAnimator.ofFloat(v, View.SCALE_Y, *values))
        }
    }


    var onChannelChange: ((channel: Channel)->Unit)? = null

    private fun selectChannel(channel: Channel) {
        var current: Channel? = null
        var currentIndex: Int = -1
        mData.forEachIndexed { index, ch ->
            if (ch.isSelected) {
                current = ch
                currentIndex = index
            }
        }

//        val current = mData.firstOrNull {it.isSelected}

        if (current != channel) {
            mData.forEach {
                it.isSelected = false
            }

            channel.isSelected = true
            val selectIndex = mData.indexOfFirst {
                it.isSelected
            }

            mAdapter.notifyItemChanged(currentIndex)
            mAdapter.notifyItemChanged(selectIndex)

            onChannelChange?.invoke(channel)
        }
    }

    fun setData(list: MutableList<Channel>, initSelectChannel: Int = 0) {
        mData.clear()
        mData.addAll(list)

        if (initSelectChannel != 0) {
            mData.forEach {
                it.isSelected = false
            }

            mData.firstOrNull {
                it.id == initSelectChannel
            }?.isSelected = true
        }

        mAdapter.notifyDataSetChanged()
    }
}