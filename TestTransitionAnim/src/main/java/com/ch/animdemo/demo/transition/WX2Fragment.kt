package com.ch.animdemo.demo.transition

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.Fragment
import androidx.core.app.SharedElementCallback
import androidx.core.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class WX2Fragment : Fragment() {


    internal var rvWx2: RecyclerView? = null
    private var wxAdapter: WxAdapter? = null

    private lateinit var list: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_wx2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvWx2 = rv_wx2
        initView()
    }

    private fun initView() {
        list = ArrayList()
        list!!.add("http://img1.imgtn.bdimg.com/it/u=4206294871,879077254&fm=26&gp=0.jpg")
        list!!.add("http://img1.imgtn.bdimg.com/it/u=1901690610,3955011377&fm=200&gp=0.jpg")
        list!!.add("http://img3.imgtn.bdimg.com/it/u=1546158593,2358526642&fm=200&gp=0.jpg")
        list!!.add("http://img0.imgtn.bdimg.com/it/u=3792909229,2321547963&fm=200&gp=0.jpg")
        list!!.add("http://img4.imgtn.bdimg.com/it/u=1621655683,865218969&fm=200&gp=0.jpg")
        list!!.add("http://img5.imgtn.bdimg.com/it/u=4286838121,1364454560&fm=26&gp=0.jpg")
        list!!.add("http://img5.imgtn.bdimg.com/it/u=551944592,1654216059&fm=26&gp=0.jpg")
        list!!.add("http://img1.imgtn.bdimg.com/it/u=2550323596,2167297465&fm=200&gp=0.jpg")
        list!!.add("http://img4.imgtn.bdimg.com/it/u=952962361,1269259737&fm=26&gp=0.jpg")

        wxAdapter = WxAdapter(list)
        rvWx2!!.layoutManager = GridLayoutManager(context, 3)
        rvWx2!!.adapter = wxAdapter
        wxAdapter!!.bindToRecyclerView(rvWx2)


        wxAdapter!!.setOnItemClickListener { adapter, view, position ->
            val iv = view.findViewById<ImageView>(R.id.iv_wx_img)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                share(iv, position)

                //                    ImageViewerActivity.Companion.show(WX2Ac.
                //                    tivity.this, );

                val images = list.map {
                    ImageViewerActivity.Image().apply {
                        url = it
                    }
                } as ArrayList<ImageViewerActivity.Image>

                ImageViewerActivity.Companion.show(activity!!, images, position, iv)
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun share(view: View, position: Int) {
        val intent = Intent(context, Img2Activity::class.java)
        intent.putStringArrayListExtra(Img2Activity.IMG_KEY, list)
        intent.putExtra(Img2Activity.IMG_POSITION, position)

        val bundle = ActivityOptions.makeSceneTransitionAnimation(activity, view, "share").toBundle()

        startActivity(intent, bundle)
    }

//    override fun onActivityReenter(resultCode: Int, data: Intent?) {
//        if (resultCode == Activity.RESULT_OK && data != null) {
//            val exitPos = data.getIntExtra(Img2Activity.IMG_CURRENT_POSITION, -1)
//
//            // 要缩小的View
//            val exitView = getExitView(exitPos)
//
//            if (exitView != null) {
//
//                ActivityCompat.setExitSharedElementCallback(this, object : SharedElementCallback() {
//                    override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {
//                        names!!.clear()
//                        sharedElements!!.clear()
//
//                        names.add(ViewCompat.getTransitionName(exitView))
//                        sharedElements[Objects.requireNonNull(ViewCompat.getTransitionName(exitView))] = exitView
//
//                        setExitSharedElementCallback(object : SharedElementCallback() {
//
//                        })
//                    }
//                })
//            }
//        }
//    }


    fun getExitView(position: Int): View? {
        if (position == -1) {
            return null
        }
        return if (wxAdapter != null) {
            wxAdapter!!.getViewByPosition(position, R.id.iv_wx_img)
        } else null
    }

    internal inner class WxAdapter(list: List<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_wx2, list) {

        override fun convert(helper: BaseViewHolder, item: String) {
            val iv_img = helper.getView<ImageView>(R.id.iv_wx_img)
            Glide.with(mContext).load(item).apply(RequestOptions().centerCrop()).into(iv_img)
        }


    }


}
