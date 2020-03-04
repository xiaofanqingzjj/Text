package com.example.test

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bedrock.module_base.SimpleFragment
import com.bedrock.module_base.recycleradapter.BaseViewTypeAdapter
import kotlinx.android.synthetic.main.fragment_test_view_type_adapter.*

class TestViewTypeAdapter : SimpleFragment() {


    var data: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_test_view_type_adapter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val data = mutableListOf<Bean>()

        data.add(Bean())
        data.add(Bean())
        data.add(Bean())
        data.add(Bean())
        data.add(Bean())
        data.add(Bean())
        data.add(Bean())
        data.add(Bean())
        data.add(Bean())
        data.add(Bean())
        data.add(Bean())

        data.add(Bean(type = 1))
        data.add(Bean())
        data.add(Bean())
        data.add(Bean())
        data.add(Bean())
        data.add(Bean())
        data.add(Bean())
        data.add(Bean())
        data.add(Bean())
        data.add(Bean())
        data.add(Bean())
        data.add(Bean())
        data.add(Bean())
        data.add(Bean())





        prv.layoutManager = object : LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false) {
            override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
                return RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }


        prv.adapter = MyAdapter(context!!, data)


    }

    class Bean(var title: String? = null, var type: Int = 0)

    class MyAdapter(context: Context, data: List<Bean>) : BaseViewTypeAdapter<Bean>(context, data) {


        init {
            addViewType(0, VH1::class.java)
            addViewType(1, VH2::class.java)
        }

        override fun getItemViewType(position: Int): Int {
            return getItem(position).type
        }

        class VH1 : BaseViewTypeAdapter.ViewTypeViewHolder<Bean>() {

            override fun onCreate() {
                super.onCreate()
                setContentView(R.layout.fragment_test_view_type_adapter_item1)
            }

            override fun bindData(position: Int, data: Bean?) {


            }

        }

        class VH2 : BaseViewTypeAdapter.ViewTypeViewHolder<Bean>() {

            companion object {
                const val TAG = "VH2"
            }

            override fun onCreate() {
                super.onCreate()
                setContentView(R.layout.fragment_test_view_type_adapter_item2)
            }


            override fun onAttachToWindow() {
                super.onAttachToWindow()
                Log.d(TAG, "onAttachToWindow")
            }

            override fun onDetachFromWindow() {
                super.onDetachFromWindow()
                Log.e(TAG, "onDetachFromWindow")
            }


            override fun bindData(position: Int, data: Bean?) {


            }

        }


    }
}