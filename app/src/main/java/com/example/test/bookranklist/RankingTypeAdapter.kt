package com.example.test.bookranklist

import android.content.Context
import com.bedrock.module_base.recycleradapter.QuickAdapter
import com.example.test.R
import kotlinx.android.synthetic.main.fragment_book_ranking_type.view.*


class RankingTypeAdapter(context: Context,
                         data: List<RankType>,
                         selectType: (pos: Int, rankType: RankType)->Unit)

    : QuickAdapter<RankType>(
                context = context,
                data = data,
                itemLayoutId = R.layout.fragment_book_ranking_type,
                itemViewCreated = {itemView, holder ->
                    itemView?.run {
                        tv_rank_type.setOnClickListener {
                            data.forEach {
                                it.isSelected = false
                            }

                            val pos = holder.currentBindPosition
                            val cd = holder.currentBindData

                            cd.isSelected = true
                            holder.adapter.notifyDataSetChanged()


                            selectType(pos, cd)
                        }
                    }
                },
                bindData = { pos, data, itemView ->
                    itemView?.run {
                        tv_rank_type?.run {
                            text = data.name
                            isSelected = data.isSelected
                            paint.isFakeBoldText = data.isSelected
                        }
                    }
                })