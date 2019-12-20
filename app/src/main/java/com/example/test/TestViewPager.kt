package com.example.test

import android.os.Bundle
import android.view.View
import com.bedrock.module_base.SimpleFragment
import com.bedrock.module_base.viewpageradapter.CommonPagerAdapter
import kotlinx.android.synthetic.main.fragment_view_pager.*
import kotlinx.android.synthetic.main.fragment_view_pager_item.view.*

class TestViewPager : SimpleFragment() {

    companion object {
        const val  TAG = "TestViewPager"
    }

    private val mData: MutableList<Any> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_view_pager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val adapter = CommonPagerAdapter<Any>(
                data = mData,
                itemViewLayoutId = R.layout.fragment_view_pager_item
//                ,
//                getItemPos = {
//                    val data = (it as? View)?.tag
//                    var newPos = mData.indexOf(data)
//                    if (newPos == -1) {
//                        newPos = PagerAdapter.POSITION_NONE
//                    }
//                    Log.d(TAG, "newPos:$newPos, data:$data, mData:$mData")
//                    newPos
//                }
        )
        { pos, data ->
            this.tag = data
            text.text = "pos:$pos"
        }

        view_pager.adapter = adapter

        mData.add(1)
        mData.add(1)
        mData.add(1)
        mData.add(1)

        adapter.notifyDataSetChanged()

        btn.setOnClickListener {

            mData.removeAt(3)
            mData.removeAt(2)
            mData.removeAt(1)

            adapter.notifyDataSetChanged()
        }
    }


    class Item



}