package com.example.test.slidedrawer

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bedrock.module_base.SimpleEmptyFragment
import com.bedrock.module_base.SimpleFragment
import com.example.test.R
import kotlinx.android.synthetic.main.fragment_slide_menu.*

class TestSlideMenu : SimpleFragment() {

    private var slideLayout: SlideLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_slide_menu)



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        slide_layout.thumbView = thumb

        slideLayout = slide_layout.apply {
            isInitOpen = true
            enableSlide = false
        }

//
        createTabs(view_pager, mutableListOf<Tab>().apply {
            add(Tab.create("嗷嗷", SimpleEmptyFragment()))
            add(Tab.create("嗷嗷", SomeListFragment()))
        })

        view_pager?.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == 0) {
                    slideLayout?.enableSlide = false
                    slideLayout?.open(true)
                } else {
                    slideLayout?.enableSlide = true
                    slideLayout?.open(false)
                }
            }
        })
    }






    /**
     * 创建tab
     */
    fun createTabs(viewPager: ViewPager, tabList: List<Tab>) {
        val mPagerAdapter = TabPagerAdapter(childFragmentManager, tabList)
        viewPager?.adapter = mPagerAdapter
//        mTabIndicator?.setViewPager(mViewPager)
    }



    /**
     * Tab对象
     */
    class Tab(var title: String? = null, var fragment: Fragment ) {
        companion object {
            fun create(title: String? = null, fragment: Fragment): Tab {
                return Tab(title, fragment)
            }
        }
    }




    /**
     * PagerAdapter
     */
    class TabPagerAdapter(fm: FragmentManager, var tabs: List<Tab>) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return tabs[position].fragment
        }

        override fun getCount(): Int {
            return tabs.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return tabs[position].title
        }


        var mCurrentFragment: Fragment? = null

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            super.setPrimaryItem(container, position, `object`)
            mCurrentFragment = `object` as Fragment
        }
    }
}