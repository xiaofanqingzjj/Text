package com.bedrock.module_base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bedrock.module_base.views.PageIndicator


/**
 * 提供一个简单的创建Tab的Fragment
 *
 *
 * @author fortunexiao
 */
abstract class BaseTabFragment : Fragment() {

    companion object {
        const val TAG = "BaseTabFragment"
    }


    var mViewPager: ViewPager? = null
    var mTabIndicator: PageIndicator? = null
    var mPagerAdapter: TabPagerAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


    /**
     * 设置ViewPager和tabIndicator
     */
    fun setViews(viewPager: ViewPager?, tabIndicator: PageIndicator?) {
        mViewPager = viewPager
        mTabIndicator = tabIndicator

    }


    /**
     * 创建tab
     */
    fun createTabs(tabList: List<Tab>) {
        mPagerAdapter = TabPagerAdapter(childFragmentManager, tabList)
        mViewPager?.adapter = mPagerAdapter!!
        mTabIndicator?.setViewPager(mViewPager)
    }


    fun currentDisplayFragment(): Fragment? {
        return mViewPager?.run {

            val index = currentItem
            val pages = (adapter as? TabPagerAdapter)?.tabs

            if (pages?.size ?: 0 > index) {
                pages?.get(index)?.fragment
            } else null
        }
    }




    /**
     * Tab对象
     */
    class Tab(var title: String? = null, var fragment: Fragment) {
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