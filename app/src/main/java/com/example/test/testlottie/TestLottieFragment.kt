package com.example.test.testlottie

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bedrock.module_base.util.ScreenUtil
import com.bedrock.module_base.util.toPx
import com.fortunexiao.tktx.runUIThread
import com.example.test.R
import kotlinx.android.synthetic.main.fragment_test_lottie.*

class TestLottieFragment : Fragment() {

    companion object {
        const val  TAG = "TestLottieFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_test_lottie, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        com.fortunexiao.tktx.runUIThread(500) {
            setupEntrancePosition()
        }


    }

    private fun setupEntrancePosition() {

        val context = context ?: return

        val entrance = lav_luck_bag_entrance

        val screenHeight = ScreenUtil.resolution(context).height


        val bottomPos = getEntranceBottomPos(entrance)
        val entranceHeight = 60f.toPx(context)
        val tabBarHeight = 55f.toPx(context)

        Log.d(TAG, "size:$screenHeight, bottomPos:$bottomPos, entranceHeight:$entranceHeight, tabBarHeight:$tabBarHeight")

        if (bottomPos > (screenHeight - tabBarHeight)) { //
            val marginTop = screenHeight - tabBarHeight - entranceHeight - 80f.toPx(context)

            Log.d(TAG, "marginTop:$marginTop")
            (entrance.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = marginTop//100f.toPx(context)
            entrance.requestLayout()
        }

    }

    private fun getEntranceBottomPos(entranceView: View): Int {
        val pos = IntArray(2)
        entranceView.getLocationInWindow(pos)
        return pos[1] + entranceView.height
    }
}