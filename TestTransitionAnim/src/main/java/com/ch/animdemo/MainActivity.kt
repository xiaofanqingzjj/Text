package com.ch.animdemo

import android.os.Bundle
import com.bedrock.module_base.MenuActivity
import com.ch.animdemo.`object`.ObjectActivity
import com.ch.animdemo.base.BaseActivity
import com.ch.animdemo.demo.DemoActivity
import com.ch.animdemo.testscalegesturedetector.TestScaleGestureDecectorFragment
import com.ch.animdemo.value.ValueActivity


/**
 *
 */
class MainActivity : MenuActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addMenu("基础动画", BaseActivity::class.java)
        addMenu("属性动画", ValueActivity::class.java)
        addMenu("Object Animator", ObjectActivity::class.java)
        addMenu("Demo", DemoActivity::class.java)


        addMenuByFragment("Scale Gesture Detector", TestScaleGestureDecectorFragment::class.java)

    }
}
