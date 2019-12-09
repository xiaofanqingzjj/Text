package com.ch.animdemo.demo.transition

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ch.animdemo.R
import com.ch.animdemo.demo.transition.imageviewer.ImageViewerActivity2

class WX2Activity : AppCompatActivity() {


    var fragment: WX2Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        fragment = WX2Fragment()
        supportFragmentManager.beginTransaction().add(R.id.container, fragment!!).commitAllowingStateLoss()
    }


    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            val exitPos = data.getIntExtra(Img2Activity.IMG_CURRENT_POSITION, -1)

            val backView = fragment?.getExitView(exitPos)
            backView?.run {
                ImageViewerActivity2.setAnimBackView(this@WX2Activity, backView)
            }
        }
    }



}
