package com.ch.animdemo.demo.transition.imageviewer

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startPostponedEnterTransition
import androidx.core.app.FragmentActivity
import androidx.core.app.SharedElementCallback
import androidx.core.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ch.animdemo.R
import com.ch.animdemo.demo.transition.Img2Activity
import com.example.module_base.adapter.QuickAdapter
import kotlinx.android.synthetic.main.activity_image_viewer.*
import kotlinx.android.synthetic.main.item_img.view.*
import java.io.Serializable
import java.util.*


/**
 * 图片预览界面
 *
 *
 */
class ImageViewerActivity : FragmentActivity() {


    companion object {

//        const val PARAMS_IMGS = "PARAMS_IMGS"
        const val PARAM_INIT_POSITION = "PARAM_INIT_POSITION"
        const val PARAM_IMG_CURRENT_POSITION = "PARAM_IMG_CURRENT_POSITION"

        // 一个简单到内存缓存器
        private var mSimpleCache: ArrayList<Image>? = null

        fun show(context: Activity, images: ArrayList<Image>, initPicIndex: Int,  thumbImageView: ImageView? = null) {

            val intent = Intent(context, ImageViewerActivity::class.java).apply {
//                putExtra(PARAMS_IMGS, images)
                putExtra(Img2Activity.IMG_POSITION, initPicIndex)
            }


            var options: ActivityOptions? = null
            if (thumbImageView != null) {
                options = ActivityOptions.makeSceneTransitionAnimation(context, thumbImageView, "share")

                if (images[initPicIndex].thumb == null) {
                    images[initPicIndex].thumb = imageViewBitmap(thumbImageView)
                }
            }


            mSimpleCache = images

            context.startActivity(intent, options?.toBundle())
        }

        fun imageViewBitmap(imageView: ImageView): Bitmap? {
            return (imageView.drawable as? BitmapDrawable)?.bitmap
        }

        /**
         * 把View设置为返回时缩小的目标动画
         */
        fun setAnimBackView(activity: FragmentActivity, backView: View) {

            ActivityCompat.setExitSharedElementCallback(activity, object : SharedElementCallback() {

                override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {

                    names?.clear()
                    sharedElements?.clear()

                    val transitionName = ViewCompat.getTransitionName(backView)

                    names?.add(transitionName)
                    sharedElements?.put(transitionName, backView)

                    // 清除回调
                    activity.setExitSharedElementCallback(object : SharedElementCallback() {

                    })
                }
            })
        }
    }



    private var imgList: ArrayList<Image>? = null

    private var currentPosition: Int = 0
    private var enterPosition: Int = 0

    private var mRecyclerView: RecyclerView? = null

    private var snapHelper: PagerSnapHelper? = null

    private var imgAdapter: ImageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setTheme(android.R.style.Theme_Material_Light_NoActionBar)



        setContentView(R.layout.activity_image_viewer)

        // 延迟共享动画的执行
        postponeEnterTransition()

        intent?.run {
//            imgList = intent.getSerializableExtra(PARAMS_IMGS) as ArrayList<Image>

            enterPosition = intent.getIntExtra(PARAM_INIT_POSITION, 0)
            currentPosition = enterPosition
        }


        imgList = mSimpleCache

        snapHelper = object : PagerSnapHelper() {
            override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int): Int {
                currentPosition = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
                return currentPosition
            }
        }

        mRecyclerView = recycler_view

        snapHelper?.attachToRecyclerView(mRecyclerView)

        imgAdapter = ImageAdapter(this, imgList!!)

        mRecyclerView?.layoutManager = LinearLayoutManager(this@ImageViewerActivity, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerView?.adapter = imgAdapter

//        imgAdapter?.bindToRecyclerView(mRecyclerView)

        mRecyclerView?.scrollToPosition(enterPosition)


        window?.sharedElementEnterTransition?.run {
        }



    }

    override fun onDestroy() {
        super.onDestroy()

        mSimpleCache = null
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (enterPosition != currentPosition) {
            //滑动过，需要刷新

            val exitView = imgAdapter?.getViewByPosition(mRecyclerView, currentPosition, R.id.iv_img)


            ActivityCompat.setEnterSharedElementCallback(this, object: SharedElementCallback() {

                override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {
                    super.onMapSharedElements(names, sharedElements)

                    names?.clear();
                    sharedElements?.clear();

                    names?.add(ViewCompat.getTransitionName(exitView));

                    if (exitView != null) {
                        sharedElements?.put(ViewCompat.getTransitionName(exitView), exitView);
                    }
                }

            });
        }

    }
//        @Override
//        public void onBackPressed() {
//            super.onBackPressed();
//
//        }

    override fun finishAfterTransition() {
        val intent = Intent()
        if (enterPosition == currentPosition) {
            //没有改变
            intent.putExtra(PARAM_IMG_CURRENT_POSITION, -1)
        } else {
            // 把结束的位置告诉前面的界面
            intent.putExtra(PARAM_IMG_CURRENT_POSITION, currentPosition)
        }
        setResult(Activity.RESULT_OK, intent)
        super.finishAfterTransition()
    }




    class ImageAdapter(context: Context, images: ArrayList<Image>): QuickAdapter<Image> (context, images, R.layout.rv_item_image_viewer) {

        companion object {
            const val TAG = "ImageAdapter"
        }

        val mHandler = Handler(Looper.getMainLooper())

        override fun bindData(data: Image, viewHolder: ViewTypeViewHolder<Image>?) {


            viewHolder?.itemView?.run {

                Log.d(TAG, "bindData pos:${viewHolder.currentBindPosition}, image:${data}")

                if (data.thumb != null) {
                    iv_img.setImageBitmap(data.thumb)
                }


                Glide.with(context)
                        .load(data.url)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                                return false
                            }

                            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {

                                //图片加载完成的回调中，启动过渡动画
//
                                return false
                            }
                        })
                        .into(iv_img)


                mHandler.postDelayed({
                    (context as Activity).startPostponedEnterTransition()
                }, 100)

            }

        }
    }


    /**
     * 预览的图片项
     */
    data class Image(
            var thumb: Bitmap? = null,
            var thumbUrl: String? = null,
            var url: String? = null,

            var width: Int = 0,
            var height: Int = 0

    ) : Serializable {


    }

}
