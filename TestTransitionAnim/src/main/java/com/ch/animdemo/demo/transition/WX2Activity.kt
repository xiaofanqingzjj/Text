package com.ch.animdemo.demo.transition

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.SharedElementCallback
import androidx.core.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ch.animdemo.R
import com.ch.animdemo.demo.transition.imageviewer.ImageViewerActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

import java.util.ArrayList
import java.util.Objects

import butterknife.BindView
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.activity_wx2.*

class WX2Activity : AppCompatActivity() {


    var fragment: WX2Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        fragment = WX2Fragment()
        supportFragmentManager.beginTransaction().add(R.id.container, fragment).commitAllowingStateLoss()
    }


    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            val exitPos = data.getIntExtra(Img2Activity.IMG_CURRENT_POSITION, -1)

            val backView = fragment?.getExitView(exitPos)
            backView?.run {
                ImageViewerActivity.setAnimBackView(this@WX2Activity, backView)
            }
        }
    }



}
