package com.example.testpermission

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.testpermission.ad.AutoHideHeaderLayout
import com.example.testpermission.ad.HomeHeadAd
import com.example.testpermission.ad.HomeHeadAdView
import com.example.testpermission.ad.MyHeader

class TestHome : FragmentActivity() {


    companion object {
        const val TAG = "TestHome"
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_test_home)

        val stickyLayout = findViewById<AutoHideHeaderLayout>(R.id.sticky_layout)

        val recyclerView: MyRecyclerView = findViewById<MyRecyclerView>(R.id.recycler_view)
        val myHeader = findViewById<MyHeader>(R.id.my_header)
//
//        val headBgView = findViewById<HomeHeadAdView>(R.id.home_head_ad_view)
//
//        app_bar_layout.apply {
//
//            addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
//
//                Log.d(TAG, "onOffsetChange:${appBarLayout.totalScrollRange}, offset:$verticalOffset")
//
//                headBgView.setCurrentScrollPos(verticalOffset)
//
//
//            })
//
////            addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener)
////            addOnOffsetChangedListener { appBarLayout, verticalOffset ->
////                val currentScrollPos = Math.abs(verticalOffset)
////                val maxScrollLength = appBarLayout.height - context.resources.getDimension(R.dimen.book_base_title_bar_height).toInt()
////                if (currentScrollPos >= maxScrollLength - 4) { // 某些手机滑不到最高点，这里用4个像素做容错
////                    changeMode(true)
////                    if (!isHeadClosed) {
////                        mBookHeadInfoFragment?.paseVideo()
////                    }
////
////                    isHeadClosed = true
////                } else if (currentScrollPos > 0){ // 防止初始调用
////                    changeMode(false)
////                    if (isHeadClosed) {
////                        mBookHeadInfoFragment?.resumeVideo()
////                    }
////
////                    isHeadClosed = false
////                }
////            }
//        }
//
//
////        stickyLayout?.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
////        }
//
//
//        headBgView.setData(mutableListOf<HomeHeadAd>().apply {
//            add(HomeHeadAd())
//            add(HomeHeadAd())
//            add(HomeHeadAd())
//        })

//        CoordinatorLayout






//        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//
////            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
////                super.onScrolled(recyclerView, dx, dy)
////
////            }
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//
////                val mrv = recyclerView as MyRecyclerView
////                headBgView.setCurrentScrollPos(mrv.contentScrollY)
//            }
//        })


        recyclerView.layoutManager = object : LinearLayoutManager(this) {
            override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {

                return RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }

        val adapter = MyAdapter(this, testData(), recyclerView, stickyLayout)

        recyclerView.adapter = adapter
    }

    class Item {

        var type: Int = 0

        var adList: MutableList<HomeHeadAd>? = null

    }


    fun testData(): MutableList<Item> {
        return mutableListOf<Item>().apply {


            var item = Item().apply {
                adList = mutableListOf<HomeHeadAd>().apply {
                    add(HomeHeadAd())
                    add(HomeHeadAd())
                    add(HomeHeadAd())
                }
            }
            item.type = 2
            add(item)

            for (i in 0 until 13) {
                add(Item())
            }

            item = Item()
            item.type = 1
            add(item)




            for (i in 0 until 10) {
                add(Item())
            }

//            add(item)

            for (i in 0 until 10) {
                add(Item())
            }
        }
    }

    class MyAdapter(context: Context, data: List<Item>, var recyclerView: MyRecyclerView, var myHeader: AutoHideHeaderLayout) : BaseViewTypeAdapter<Item>(context, data) {

        init {

            addViewType(2, HeaderVH::class.java)
            addViewType(1, MyAdVH::class.java)
            addViewType(0, MyVH::class.java)

        }

        override fun onViewHolderCreated(holder: ViewTypeViewHolder<*>?) {
            super.onViewHolderCreated(holder)

            if (holder is MyAdVH) {
                holder.recyclerView = recyclerView
            }

            if (holder is HeaderVH) {
                holder.recyclerView = recyclerView
                holder.myHeader = myHeader
            }
        }


        override fun getItemViewType(position: Int): Int {
            return getItem(position).type
        }



        class HeaderVH : ViewTypeViewHolder<Item>() {

            lateinit var hha: HomeHeadAdView

            var recyclerView: MyRecyclerView? = null
            var myHeader: AutoHideHeaderLayout? = null

            override fun onCreate() {
                super.onCreate()

                setContentView(R.layout.rv_item_ad_header)
                hha = findViewById(R.id.hha)

                hha.initScrollValue = (-DensityUtil.dip2px(context, 200f))


            }

            override fun onPostCreate() {
                super.onPostCreate()

                recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        val contentY = (recyclerView as MyRecyclerView).contentScrollY
                        Log.d(TAG, "onScroll:$contentY")

                        hha.setCurrentScrollPos(-contentY)

                    }
                })

                myHeader?.onScrollListener = object : AutoHideHeaderLayout.OnScrollListener {
                    override fun onScroll(currentScrollLength: Int, maxScrollLength: Int) {
                        Log.e(TAG, "onOffsetChanged:$currentScrollLength,max:$maxScrollLength")

                        hha.setCurrentScrollPos((currentScrollLength))
                    }
                }

//                myHeader?.addOnOffsetChangedListener(object : MyHeader.OnOffsetChangedListener{
//                    override fun onOffsetChanged(verticalOffset: Int, maxOffset: Int) {
//                        Log.e(TAG, "onOffsetChanged:$verticalOffset,max:$maxOffset")
//
//                        hha.setCurrentScrollPos(verticalOffset)
//                    }
//
//                })
            }

            override fun bindData(position: Int, data: Item?) {

                data?.adList?.run {
                    hha.setData(this)
                }

            }

        }

        /**
         * header
         */
        class MyAdVH : ViewTypeViewHolder<Item>() {

            companion object {
                const val TAG = "MyAdVH"
            }

            var recyclerView: MyRecyclerView? = null

            private lateinit var ivRole: View
            private lateinit var ivDecor: View
            private lateinit var ivBg: View


            override fun onCreate() {
                super.onCreate()
                setContentView(R.layout.rv_item_home_ad)

                ivRole = findViewById(R.id.iv_role)
                ivDecor = findViewById(R.id.iv_decor)
                ivBg = findViewById(R.id.iv_bg)

            }

            override fun onPostCreate() {
                super.onPostCreate()


                recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        val rv = recyclerView as? MyRecyclerView

                        scrollBy(ivRole, dy * 0.8.toFloat(), min = -DensityUtil.dip2px(context, 588f).toFloat(), max =  0f)
                        scrollBy(ivDecor, dy * 1.toFloat(), min = -DensityUtil.dip2px(context, 588f).toFloat(), max =  0f)
//                        val fistItem = firstVisibleItemView(recyclerView)
//                        Log.d(TAG, "onScrolled ${dy * 0.5f}, role1 Pos: ${role1.y}, recyleable:${isRecyclable}, firstVisibleItem:${fistItem?.first}, yPos:${fistItem?.second?.y}, recyclerViewScrollY:${rv?.contentScrollY}")
                    }
                })

//                recyclerView?.

            }

            override fun bindData(position: Int, data: Item?) {
            }

        }


        class MyVH : ViewTypeViewHolder<Item>() {

            private lateinit var text: TextView

            override fun onCreate() {
                super.onCreate()

                setContentView(R.layout.rv_item_test)

                text = findViewById(R.id.text)
            }

            override fun bindData(position: Int, data: Item?) {

                text.text = "item:$position"
            }

        }

    }
}


private fun scrollBy(view: View, dy: Float, min: Float = 0f, max: Float = Float.MAX_VALUE): Boolean {
    val nDy = clamp(dy, view.translationY, min, max)
//    Log.d("TestHome", "nDy:$nDy")
    return if (nDy != 0f) {
        view.translationY = view.translationY + nDy
        true
    } else false
}

private fun clamp(dy: Float, y: Float, min: Float, max: Float): Float {
    val newPos = y + dy
    if (newPos in min..max) {
        return dy
    }
    if (newPos < min) {
        return min - y
    }
    if (newPos > max) {
        return max - y
    }
    return 0f
}


fun firstVisibleItemView(recyclerView: RecyclerView?): Pair<Int, View?>? {
    return recyclerView?.run {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is LinearLayoutManager) {
            val firstItemPosition = layoutManager.findFirstVisibleItemPosition()
            val firstItemView = layoutManager.findViewByPosition(firstItemPosition)
            Pair(firstItemPosition, firstItemView)
        } else {
            null
        }
    }
}