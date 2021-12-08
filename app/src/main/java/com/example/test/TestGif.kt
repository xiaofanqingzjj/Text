package com.example.test

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bedrock.module_base.SimpleFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_gif.*
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


//        GifDrawable

        Glide
                .with(context!!)
//                .applyDefaultRequestOptions()

                .asDrawable()

                .apply(RequestOptions.bitmapTransform(object : Transformation<Bitmap> {
                    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
                    }

                    override fun transform(context: Context, resource: Resource<Bitmap>, outWidth: Int, outHeight: Int): Resource<Bitmap> {
                        Log.d(TAG, "transform: resource size:${resource.size}")

                        return resource
                    }

                }).apply {
                    override(100, 100)
                })


//                .transition()
                .load("https://bookmomentcon-1251775003.image.myqcloud.com/58183739/107/4f9db00fc1045b0477d95423f060f422.gif")


                .into(iv)

    }

}