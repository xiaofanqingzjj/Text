package com.ch.animdemo.demo.transition

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ch.animdemo.R
import com.ch.animdemo.demo.transition.imageviewer.ImageViewerActivity2
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.activity_wx2.*
import java.util.*

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
        list.add("http://img1.imgtn.bdimg.com/it/u=4206294871,879077254&fm=26&gp=0.jpg")
        list.add("http://img1.imgtn.bdimg.com/it/u=1901690610,3955011377&fm=200&gp=0.jpg")
        list.add("http://img3.imgtn.bdimg.com/it/u=1546158593,2358526642&fm=200&gp=0.jpg")
        list.add("http://img0.imgtn.bdimg.com/it/u=3792909229,2321547963&fm=200&gp=0.jpg")
        list.add("http://img4.imgtn.bdimg.com/it/u=1621655683,865218969&fm=200&gp=0.jpg")
        list.add("http://img5.imgtn.bdimg.com/it/u=551944592,1654216059&fm=26&gp=0.jpg")
        list.add("http://img1.imgtn.bdimg.com/it/u=2550323596,2167297465&fm=200&gp=0.jpg")
        list.add("http://img4.imgtn.bdimg.com/it/u=952962361,1269259737&fm=26&gp=0.jpg")
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1573973055383&di=328cc5ac025755ec16e63b560b1baa59&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F43efd35d1e9cadc6d8ff5cdc5faccec06f1082bb4efc4-o8K27E_fw658")
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1574178712873&di=3ccd28777ee4c8a88bbdc0027b4949a7&imgtype=0&src=http%3A%2F%2Fwx2.sinaimg.cn%2Forj360%2F006vctjzly1g6opgjmgk7j30hs50nnjx.jpg")

        wxAdapter = WxAdapter(list)

        rvWx2?.layoutManager = GridLayoutManager(context, 3)
        rvWx2?.adapter = wxAdapter
        wxAdapter?.bindToRecyclerView(rvWx2)


        wxAdapter?.setOnItemClickListener { adapter, view, position ->

            val iv = view.findViewById<ImageView>(R.id.iv_wx_img)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                val images = list.map {
                    ImageViewerActivity2.Image().apply {
                        thumbUrl = it
                        url = it
                    }
                } as ArrayList<ImageViewerActivity2.Image>

                ImageViewerActivity2.Companion.show(activity!!, images, position, iv)
            }
        }
    }





    fun getExitView(position: Int): View? {
        if (position == -1) {
            return null
        }

        return wxAdapter?.getViewByPosition(position, R.id.iv_wx_img)
    }


    internal inner class WxAdapter(list: List<String>) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_wx2, list) {
        override fun convert(helper: BaseViewHolder, item: String) {
            val iv_img = helper.getView<ImageView>(R.id.iv_wx_img)
            Glide.with(mContext).load(item).apply(RequestOptions().centerCrop()).into(iv_img)
        }
    }
}
