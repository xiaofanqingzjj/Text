package com.example.test

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.bedrock.module_base.SimpleFragment
import com.bedrock.module_base.recycleradapter.quickAdapter
import com.bedrock.module_base.views.recyclerviews.PagingRecyclerView
import com.fortunexiao.tktx.runUIThread
import kotlinx.android.synthetic.main.fragment_test_paging_list.*
import kotlinx.android.synthetic.main.fragment_test_paging_list.view.*

class TestPagingList : SimpleFragment() {


    var data: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_test_paging_list)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        prv.quickAdapter(data, android.R.layout.simple_list_item_1) {pos, data, itemView ->
            itemView?.findViewById<TextView>(android.R.id.text1)?.text = "pos:$pos"
        }


        prv.setOnLoadMoreListener(object : PagingRecyclerView.OnTriggerLoadMoreListener {
            override fun onTriggerLoadMore() {
                loadPageData(false)
            }
        })

        loadPageData(true)

        refresh_layout.setOnRefreshListener {
            data.clear()
            loadPageData(true)
        }
    }

    private fun loadPageData(refresh: Boolean) {

        runUIThread(1000) {

            if (refresh) {
                data.clear()
            }

            data.add("a")
            data.add("a")
            data.add("a")
            data.add("a")
            data.add("a")
            data.add("a")
            data.add("a")
            data.add("a")

            prv?.adapter?.notifyDataSetChanged()

            prv?.loadFinish(true, true, true)

            refresh_layout.isRefreshing = false
        }
    }
}