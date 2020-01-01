package com.bedrock.module_base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_simple_base_recycler_view.*


/**
 * 包含一个RecyclerView和下拉刷新的基类
 * @author fortunexiao
 */
open class SimpleBaseListFragment : Fragment() {

    companion object {
        const val TAG = "BaseWalletRecyclerViewFragment"
    }

    lateinit var mRefreshLayout: SwipeRefreshLayout
    lateinit var mListView: RecyclerView

    var isEnableRefresh = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_simple_base_recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
    }

    private fun bindViews() {
        mRefreshLayout = refresh_layout.apply {
            isEnabled = isEnableRefresh
        }

        mListView = recycler_view.apply {
            layoutManager = object : LinearLayoutManager(activity) {
                override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
                    return RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.WRAP_CONTENT)
                }
            }
        }
        mRefreshLayout.setOnRefreshListener {
            onRefresh()
        }


    }

    fun dismissLoading() {
//        super.dismissLoading()
        mRefreshLayout.isRefreshing = false
    }

    open fun onRefresh() {}
}