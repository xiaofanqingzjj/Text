package com.example.test.bookranklist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bedrock.module_base.views.recyclerviews.PagingRecyclerView
import com.example.test.R
import com.fortunexiao.tktx.runUIThread
import kotlinx.android.synthetic.main.fragment_book_ranking_list.*

/**
 * 排行榜界面
 *
 * @author fortunexiao
 */
class BookRankingListFragment : Fragment() {



    private var mRankTypeList: MutableList<RankType> = mutableListOf()

    private var mRankTypeAdapter: RankingTypeAdapter? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_book_ranking_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // 排行类型
        rv_ranking_type.vertical()
        mRankTypeAdapter = RankingTypeAdapter(context!!, mRankTypeList) {pos, rankType ->
            changeRankingList(rankType)
        }
        rv_ranking_type.adapter = mRankTypeAdapter
        setupRankTypeList()

        // 排行榜列表
        rv_book_list.vertical()
        mBookListAdapter = RankingBookListAdapter(context!!, mBookList)
        rv_book_list.adapter = mBookListAdapter

        rv_book_list.setOnLoadMoreListener(object : PagingRecyclerView.OnTriggerLoadMoreListener{
            override fun onTriggerLoadMore() {
                requestBookList(false)
            }
        })

    }

    private fun setupRankTypeList() {
        mRankTypeList.addAll(testBookRankType())

        mRankTypeAdapter?.notifyDataSetChanged()

        // 默认选择排行榜的第一个
        selectFirstRankType()
    }

    private fun selectFirstRankType() {
        mRankTypeList.forEach {
            it.isSelected = false
        }
        if (mRankTypeList.size > 0) {
            val first = mRankTypeList[0]
            first.isSelected = true
            changeRankingList(first)
        }
        mRankTypeAdapter?.notifyDataSetChanged()
    }



    private fun testBookRankType(): MutableList<RankType> {
        return mutableListOf<RankType>().apply {
            add(RankType("飙升榜"))
            add(RankType("人气榜"))
            add(RankType("新作榜"))
            add(RankType("完结榜"))
            add(RankType("更新榜"))
            add(RankType("人气榜"))
        }
    }



    private fun changeRankingList(rankType: RankType) {

        requestBookList(true)
    }


    private val mBookList: MutableList<BookInfo> = mutableListOf()
    private var mBookListAdapter: RankingBookListAdapter? = null


    private fun requestBookList(isRefresh: Boolean) {
        runUIThread(100) {

            if (isRefresh) {
                mBookList.clear()
            }


            mBookList.addAll(testBookList())
            mBookListAdapter?.notifyDataSetChanged()

            rv_book_list.loadFinish(pageLoadSuccess = true,
                    hasMore = true,
                    enablePaging = mBookList.isNotEmpty())
        }
    }



    private fun testBookList(): MutableList<BookInfo> {
        return mutableListOf<BookInfo>().apply {
            for (i in 1 .. 10) {
                add(BookInfo())
            }
        }
    }





}