package com.example.testpermission.ad

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.util.AttributeSet


/**
 * 一个自定义的AppBarLayout
 *
 * 主要重写了父类的dispatchOffsetUpdates，父类的该方法是私有的
 *
 * @author fortunexiao
 */
class MyAppBarLayout(context: Context, attrs: AttributeSet? = null) : AppBarLayout(context, attrs) {

    private var mListeners: MutableList<OnOffsetChangedListener> = mutableListOf()

    override fun addOnOffsetChangedListener(listener: OnOffsetChangedListener) {
        super.addOnOffsetChangedListener(listener)
        mListeners.add(listener)
    }

    override fun removeOnOffsetChangedListener(listener: OnOffsetChangedListener?) {
        super.removeOnOffsetChangedListener(listener)
        mListeners.remove(listener)
    }



    fun dispatchOffsetUpdates(offset: Int) {
        mListeners.map {
            it.onOffsetChanged(this, offset)
        }
    }
}