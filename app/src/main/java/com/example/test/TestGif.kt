package com.example.test

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.service.autofill.Transformation
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bedrock.module_base.SimpleFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.example.test.glideconfig.GlideApp
import com.example.test.glideconfig.MyAppGlideModule
import com.github.penfeizhou.animation.FrameAnimationDrawable
import com.github.penfeizhou.animation.apng.APNGDrawable
import kotlinx.android.synthetic.main.fragment_gif.*
import kotlinx.android.synthetic.main.fragment_test_gif.*
import java.security.MessageDigest

class TestGif : SimpleFragment() {

    companion object {
        const val TAG = "TestGif"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_gif)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        iv.setImageDrawable(context.getDrawable())

        Glide.with(this)
            .load("")

            .transform()
            .into(iv)

//        BitmapDrawable



        var dd:ColorDrawable;

         var df: GifDrawable;

        // apng
        GlideApp.with(requireContext())
            .load("https://im5.ezgif.com/tmp/ezgif-5-d8b0dab63a.png")

            .into(iv1);

        val rr: APNGDrawable

        // gif
        GlideApp.with(requireContext()).load(R.drawable.test_gif).into(iv2);

        //webp
        Glide.with(this).load(R.drawable.loading).into(iv3)

        // frameANIM

        (iv4.drawable as AnimationDrawable).start()

//        Glide
    }

}