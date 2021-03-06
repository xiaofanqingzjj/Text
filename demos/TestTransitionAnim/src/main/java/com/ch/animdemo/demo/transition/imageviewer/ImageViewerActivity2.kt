package com.ch.animdemo.demo.transition.imageviewer

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.Transition
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.SharedElementCallback
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import com.bedrock.module_base.dialog.MenuDialog
import com.fortunexiao.tktx.runUIThread
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BaseTarget
import com.bumptech.glide.request.target.SizeReadyCallback
import com.ch.animdemo.R
import com.ch.animdemo.demo.transition.imageviewer.viewpager.FollowViewPager
import com.ch.animdemo.phoneView.PhotoView
import com.tencent.story.common.widget.followviewpager.CommonPagerAdapter
import kotlinx.android.synthetic.main.activity_image_viewer2.*
import kotlinx.android.synthetic.main.rv_item_image_viewer.view.*
import java.io.File
import java.io.Serializable


/**
 * 图片预览界面
 *
 * @author fortunexiao
 */
class ImageViewerActivity2 : FragmentActivity() {

    companion object {

        const val TRANSITION_NAME  = "share"

        const val TAG = "ImageViewer##"

        const val PARAM_IMAGES = "PARAM_IMAGES"
        const val PARAM_INIT_POSITION = "PARAM_INIT_POSITION"
        const val PARAM_IMG_CURRENT_POSITION = "PARAM_IMG_CURRENT_POSITION"
        const val PARAM_FEED_KEY = "PARAM_FEED_KEY"



        /**
         * 预览一张图片
         *
         * @param context 上下文
         * @param images 要预览的图片列表
         * @param initPicIndex 初始选中图片
         * @param thumbImageView 小图
         * @param feedKey Feed Key
         */
        fun show(context: Activity, images: ArrayList<Image>, initPicIndex: Int = 0,  thumbImageView: ImageView? = null, feedKey: String? = null) {

            val intent = Intent(context, ImageViewerActivity2::class.java).apply {
                putExtra(PARAM_IMAGES, images)
                putExtra(PARAM_INIT_POSITION, initPicIndex)
                putExtra(PARAM_FEED_KEY, feedKey)
            }


            var options: ActivityOptions? = null
            if (thumbImageView != null) {
                options = ActivityOptions.makeSceneTransitionAnimation(context, thumbImageView, TRANSITION_NAME)
            }

            context.startActivity(intent, options?.toBundle())
        }


        /**
         * 把View设置为返回时缩小的目标动画
         */
        fun setAnimBackView(activity: FragmentActivity, backView: View) {

            activity.postponeEnterTransition()
            ActivityCompat.setExitSharedElementCallback(activity, object : SharedElementCallback() {

                override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {

                    names?.clear()
                    sharedElements?.clear()

                    val transitionName = ViewCompat.getTransitionName(backView)
                    Log.d(TAG, "onMapSharedElements:$transitionName, backView:$backView")
                    names?.add(transitionName!!)
                    sharedElements?.put(transitionName!!, backView)

                    // 清除回调
                    activity.setExitSharedElementCallback(object : SharedElementCallback() {

                    })
                }
            })

            com.fortunexiao.tktx.runUIThread(0) {
                activity.startPostponedEnterTransition()
            }
        }
    }

    private var mImgList: ArrayList<Image> = ArrayList()

    private var currentPosition: Int = 0

    private var enterPosition: Int = 0

    private var feedKey: String? = null

    private var tvIndicator: TextView? =null
    private var mViewPager: FollowViewPager? = null
    private var ivBg: ImageView? = null

    private var ivAnimImageView: ImageView? = null


    // 屏幕高宽
    private var sw = 0
    private var sh = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //设置状态栏颜色
//        StatusBarUtil.transparencyBar(this)


        getScreenSize()

        setContentView(R.layout.activity_image_viewer2)


        // 延迟共享动画的执行
        postponeEnterTransition()

        intent?.run {
            val list = intent.getSerializableExtra(PARAM_IMAGES) as ArrayList<Image>
            mImgList.addAll(list)

            enterPosition = intent.getIntExtra(PARAM_INIT_POSITION, 0)
            feedKey = intent.getStringExtra(PARAM_FEED_KEY)
            currentPosition = enterPosition
        }

        ivAnimImageView = iv_anim_image
        tvIndicator = tv_indicator

        mViewPager = view_pager
        mViewPager?.adapter = mImageAdapter

        ivBg = iv_bg


        val totalSize = mImgList.size
        if (totalSize > 1) {
            tvIndicator?.visibility = View.VISIBLE
            tvIndicator?.text = "${enterPosition + 1}/$totalSize"
        } else {
            tvIndicator?.visibility = View.GONE
        }

        mViewPager?.setCurrentItem(enterPosition, false)

        mViewPager?.addOnPageChangeListener(object : FollowViewPager.SimpleOnPageChangeListener(){
            override fun onPageSelected(position: Int) {

                currentPosition = position

                val totalSize = mImgList.size
                if (currentPosition >= totalSize) {
                    currentPosition = if (totalSize > 0) totalSize - 1 else totalSize
                }
                tvIndicator?.text = "${currentPosition + 1}/${totalSize}"
            }
        })


        window?.sharedElementEnterTransition?.run {


            addListener(sharedElementTransitionListener)
        }

        // 设置动画属性
        setupScaleAnim()

    }

    private fun getScreenSize() {
        val outMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(outMetrics)
        sw = outMetrics.widthPixels
        sh = outMetrics.heightPixels
    }


    private val sharedElementTransitionListener = object : TransitionListenerStub() {
        override fun onTransitionEnd(transition: Transition?) {
            super.onTransitionEnd(transition)

            Log.d(TAG, "onTransitionEnd")

            com.fortunexiao.tktx.runUIThread {
                mViewPager?.visibility = View.VISIBLE
                ivAnimImageView?.visibility = View.INVISIBLE
            }
        }
    }


    /**
     * 放大动画
     */
    private fun setupScaleAnim() {
        val animImage = mImgList[enterPosition]
        loadImage(this, ivAnimImageView!!, null, animImage, true)
    }


    /**
     * 关闭界面
     */
    override fun finishAfterTransition() {

        // 只保留当前显示的ImageView的transitionName
        mImageAdapter.instantiatedViews?.map {
            it.key?.iv_img?.transitionName = null
        }
        mImageAdapter.currentView(currentPosition)?.iv_img?.transitionName = TRANSITION_NAME

        // 把结束的位置告诉前一个界面，方便实现缩小动画
        val intent = Intent()
        if (enterPosition == currentPosition) {
            //没有改变
            intent.putExtra(PARAM_IMG_CURRENT_POSITION, -1)
        } else {
            // 把结束的位置告诉前面的界面
            intent.putExtra(PARAM_IMG_CURRENT_POSITION, currentPosition)
            intent.putExtra(PARAM_FEED_KEY, feedKey)
        }
        setResult(Activity.RESULT_OK, intent)
        super.finishAfterTransition()
    }

    /**
     * ViewPager Adapter
     *
     */
    private val mImageAdapter =  CommonPagerAdapter<Image>(mImgList,
            R.layout.rv_item_image_viewer,
            bindData = {pos, data ->
                val dragDismissView = drag_dismiss_view


                dragDismissView?.run {
                    onDismiss = {
                        finishAfterTransition()
                    }

                    onScrollChangeListener = {top: Int, maxRange: Int ->
                        var bgAlpha = (maxRange - top).toFloat() / maxRange.toFloat()
                        if (bgAlpha < 0) bgAlpha = 0f
                        ivBg?.alpha = bgAlpha
                    }
                }

                val imageView = iv_img
                val progressBar = progress_bar

                // 点击照片关闭当前界面
                imageView.setOnPhotoTapListener { view, x, y ->
                    finishAfterTransition()
                }

                imageView.setOnLongClickListener {
                    true
                }

                // 加载图片
                loadImage(this@ImageViewerActivity2, imageView, progressBar, data, false)
            })



    private fun setupImageInitScale(photoImageView: PhotoView?, imageWith: Int, imageHeight: Int) {
        if (photoImageView == null) return
        if (imageWith == 0 || imageHeight == 0 || sw == 0 || sh == 0) return

        com.fortunexiao.tktx.runUIThread {
            if (imageHeight / imageWith > sh / sw) { // 图片宽高比比屏幕宽高比小，放大图片到屏幕的宽度
                val baseScale = photoImageView.attacher.baseScale // 图片本来的scale
                if (baseScale != 0f) {
                    val targetScale = sw.toFloat() / imageWith.toFloat() // 图片两端对齐屏幕时应该要放大的系数
                    val initScale = targetScale / baseScale  // 除以图片本来要放大的系数

                    if (initScale > photoImageView.maximumScale) { // 如果超出了PhotoImageView最大系数，则修改最大系数
                        photoImageView.maximumScale = initScale
                    }
                    Log.d(TAG, "initScale:$initScale")
                    photoImageView.setScale(initScale, 0f, 0f, false)
                }
            }
        }

    }





    /**
     * 下载图片
     */
    private fun loadImage(context: Context, imageView: ImageView, progressBar: ProgressBar?, image: Image, isAnim: Boolean) {
        image.run {
//            progressBar?.visibility = View.VISIBLE

            // 先加载thumb
            if (thumbUrl != null) {
                loadImageInner(
                        url = thumbUrl,
                        imageView = imageView,
                        progressBar = null,
                        isOrgImage = false,
                        l = object : ImageListenerStub() {
                            override fun onImageLoaded(p0: String?, p1: Drawable?) {
                                if (!isAnim) {
                                    loadImageInner(
                                            url = url,
                                            imageView = imageView,
                                            progressBar = progressBar,
                                            isOrgImage = true)
                                } else {
                                    com.fortunexiao.tktx.runUIThread(100) {
                                        (context as Activity).startPostponedEnterTransition()
                                    }
                                    (context as Activity).startPostponedEnterTransition()
                                }
                            }
                        })
            } else {
                loadImageInner(
                        url = url,
                        imageView = imageView,
                        progressBar = progressBar,
                        isOrgImage =  true)
            }

        }
    }

    private var mSaveImagePath = "/sdcard/aa"

    private fun showSaveMenu(image: Image) {
        MenuDialog.show(supportFragmentManager, MenuDialog.Menu("保存图片", onClick = {
           Glide.with(this).asFile().into(object: BaseTarget<File>(){
               override fun getSize(cb: SizeReadyCallback) {
               }

               override fun removeCallback(cb: SizeReadyCallback) {
               }

               override fun onResourceReady(resource: File, transition: com.bumptech.glide.request.transition.Transition<in File>?) {

               }

           })


        }))
    }




    private fun setupSuitableScale(imageView: ImageView, image: Image) {


        // 判断是否竖长图

        val imageOrgShouldWidth = image.width * imageView.height / image.height
    }


    private fun loadImageInner(url: String?,
                               imageView: ImageView,
                               progressBar: ProgressBar?,
                               isOrgImage: Boolean = false,
                               l: ImageListenerStub? = null) {

        Log.d(TAG, "beginLoad:$url")
        Glide.with(this)
                .load(url)
                .into(object : AnimableTarget(imageView) {

                    override fun onResourceReady(resource: Drawable, transition: com.bumptech.glide.request.transition.Transition<in Drawable>?) {
                        super.onResourceReady(resource, transition)
                        progressBar?.visibility = View.GONE
                        l?.onImageLoaded(url, resource)


                        if (isOrgImage) {
                            setupImageInitScale(imageView as? PhotoView, resource.intrinsicWidth, resource.intrinsicHeight)
                        }
                    }

//                    override fun setResource(resource: Drawable?) {
//
//                        if (resource is BitmapDrawable) {
//                            val bitmap = resource.bitmap
//                            bitmap?.run {
//                                if (height > width && height / width > imageView.height / imageView.height) { // 只有竖图，且图片比屏幕更长，才需要调整
//
//                                    val imageOrgShouldWidth = width * imageView.height / imageView.width
//                                }
//                            }
//                        } else {
//                            super.setResource(resource)
//                        }
//                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        progressBar?.visibility = View.GONE
                    }
                })
    }


    /**
     * 预览的图片项
     */
    data class Image(
            /**
             * 小图URL
             */
            var thumbUrl: String? = null,

            /**
             * 原图url
             */
            var url: String? = null,

            /**
             * 图片的大小
             */
            var width: Int = 0,
            var height: Int = 0

    ) : Serializable




    private abstract class ImageListenerStub {
        open fun onImageStarted(p0: String?) {
        }

        open fun onImageFailed(p0: String?, p1: java.lang.Exception?) {
        }

        open fun onImageLoaded(p0: String?, p1: Drawable?) {

        }

        open fun onImageProgress(p0: String?, p1: Float) {
        }
    }

    private abstract class TransitionListenerStub: Transition.TransitionListener {
        override fun onTransitionEnd(transition: Transition?) {
        }

        override fun onTransitionResume(transition: Transition?) {
        }

        override fun onTransitionPause(transition: Transition?) {
        }

        override fun onTransitionCancel(transition: Transition?) {
        }

        override fun onTransitionStart(transition: Transition?) {
        }

    }


}
