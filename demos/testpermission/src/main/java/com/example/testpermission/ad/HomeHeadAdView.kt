package com.example.testpermission.ad

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.example.testpermission.R
import com.example.testpermission.ad.followviewpager.ViewPager
import com.example.testpermission.ad.followviewpager.ViewPagerAdapter


/**
 *
 * 首页顶部广告栏
 *
 * @author fortune
 */
class HomeHeadAdView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs){

    companion object {
        const val TAG = "HomeHeadAdView"
    }


    private val mCardViewPager: ViewPager
    private val mCardAdapter: CardAdapter


    private val mBgViewPager: ViewPager
    private val mBgAdapter: BgAdapter

    private val dotView: DotView

    private var mData = mutableListOf<HomeHeadAd>()


    init {
        LayoutInflater.from(context).inflate(R.layout.view_home_head_ad, this)

        mCardViewPager = findViewById(R.id.card_view_pager)
        mCardAdapter = CardAdapter(mData)
        mCardViewPager.adapter = mCardAdapter

        mBgViewPager = findViewById(R.id.bg_view_pager)
        mBgAdapter = BgAdapter(mData)
        mBgViewPager.adapter = mBgAdapter


        // 两个ViewPager滑动相互关联
        mCardViewPager.setFollowViewPager(mBgViewPager)
        mBgViewPager.setFollowViewPager(mCardViewPager)

        dotView = findViewById(R.id.dot_view)

        mCardViewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                dotView.selectedDot(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                if (state == 0) {
                    Log.d("MyBehavior", "onPageScrollStateChange:$state")
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        })
    }


    /**
     * 设置数据
     */
    fun setData(data: List<HomeHeadAd>) {
        mData.clear()
        mData.addAll(data)

        mCardAdapter.notifyDataSetChanged()
        mBgAdapter.notifyDataSetChanged()

        dotView.setDotCount(data.size)
    }

    var initScrollValue: Int =0

    /**
     * 设置当前RecyclerView滑动的距离
     *
     */
    fun setCurrentScrollPos(pos: Int) {

        mBgAdapter.setCurrentScrollPos(initScrollValue + pos)
    }



    /**
     * 背景Adapter
     */
    class BgAdapter(var data: List<HomeHeadAd>) : ViewPagerAdapter<View>() {


        private var currentScrollPos: Int = 0

        private var currentInstantiateItems: MutableMap<Int, ItemViewHolder> = mutableMapOf()


        override fun createView(container: ViewGroup, position: Int): View {
            val itemViewHolder = ItemViewHolder(LayoutInflater.from(container.context).inflate(R.layout.vp_item_home_head_ad_bg, container, false))




            itemViewHolder.setCurrentScrollPos(currentScrollPos)
            currentInstantiateItems.put(position, itemViewHolder)

            return itemViewHolder.itemView
        }


        override fun destroyItem(container: ViewGroup, position: Int, key: Any) {
            super.destroyItem(container, position, key)
            currentInstantiateItems.remove(position)
        }

        override fun getCount(): Int {
            return data.size  + 2//Int.MAX_VALUE //data.size
        }

        override fun bindView(view: View, position: Int) {

            val pos = if (position == 0) {
                data.size - 1
            } else if (position == data.size + 1) {
                0
            } else {
                position - 1
            }

//            var pos = position % data.size

            val itemViewHolder = view.tag as ItemViewHolder
            itemViewHolder.bindData(data[pos], currentScrollPos)
        }

        override fun finishUpdate(container: ViewGroup) {
            super.finishUpdate(container)
            val vp = container as ViewPager
            var position = vp.currentItem




            if (position == 0) {
                position = data.size
                vp.setCurrentItem(position,false);
            } else if (position == data.size - 1) {
                position = 1;
                vp.setCurrentItem(position, false);
            }

        }



        /**
         * 设置当前RecyclerView滑动的距离
         */
        fun setCurrentScrollPos(pos: Int) {

            // 里面
            currentInstantiateItems.map {
                it.value.setCurrentScrollPos(pos)
            }
            currentScrollPos = pos
        }


        /**
         * 每一屏的ViewHolder
         */
        private class ItemViewHolder(var itemView: View) {

            val bgView: HomeHeadAdBgView

            init {
                itemView.tag = this
                bgView = itemView as HomeHeadAdBgView
            }

            fun setCurrentScrollPos(pos: Int) {
                bgView.setCurrentScrollPos(pos)
            }

            fun bindData(data: HomeHeadAd, currentScrollPos: Int) {

                //
                setCurrentScrollPos(pos = currentScrollPos)

            }
        }
    }



    /**
     * Card Adapter
     */
    class CardAdapter(var data: List<HomeHeadAd>) : ViewPagerAdapter<View>() {

        override fun getCount(): Int {
            return data.size + 2 //Int.MAX_VALUE //data.size
        }

        override fun createView(container: ViewGroup, position: Int): View {
            val itemViewHolder = ItemViewHolder(LayoutInflater.from(container.context).inflate(R.layout.vp_item_home_head_ad_card, container, false))
            return itemViewHolder.itemView
        }

        override fun bindView(view: View, position: Int) {
            val itemViewHolder = view.tag as ItemViewHolder
            itemViewHolder.bindData(data[position % data.size], position)
        }



        class ItemViewHolder(var itemView: View) {

            val textView: TextView

            init {
                itemView.tag = this
                textView = itemView.findViewById(R.id.text)
            }

            fun bindData(data: HomeHeadAd, position: Int) {
                textView.text = position.toString()
            }

        }

    }

}