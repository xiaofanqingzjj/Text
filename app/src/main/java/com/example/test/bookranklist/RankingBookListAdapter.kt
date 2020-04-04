package com.example.test.bookranklist

import android.content.Context
import com.bedrock.module_base.recycleradapter.BaseViewTypeAdapter
import com.example.test.R


class RankingBookListAdapter(context: Context, data: List<BookInfo>) : BaseViewTypeAdapter<BookInfo>(context, data) {

    init {

        addViewType(VHRankingBook::class.java)

    }




    class VHRankingBook : BaseViewTypeAdapter.ViewTypeViewHolder<BookInfo>() {

        override fun onCreate() {
            super.onCreate()

            setContentView(R.layout.rv_item_book_ranking_info)
        }

        override fun bindData(position: Int, data: BookInfo?) {
        }

    }



}