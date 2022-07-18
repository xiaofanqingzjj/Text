package com.example.test

import android.content.Context
import android.os.Bundle
import android.text.*
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bedrock.module_base.SimpleFragment
import com.bedrock.module_base.recycleradapter.QuickAdapter
import com.example.test.richtext.IndivisibleTextSpan
import com.example.test.richtext.addSpanWatcher
import com.example.test.richtext.enableIndivisibleTextSpan
import kotlinx.android.synthetic.main.fragment_test_recyclerview.*


class TestRecyclerView : SimpleFragment() {

    companion object {
        const val TAG = "TestRecyclerView";
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_test_recyclerview)

    }



    val list = mutableListOf<String>().apply {
        repeat(10) {
            add("$it")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recycler_view.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

/*
val snapHelper: PagerSnapHelper = PagerSnapHelper()
snapHelper.attachToRecyclerView(recycler_view)
*/

        recycler_view.adapter = QuickAdapter<String>(context!!, list, R.layout.item_card_layout) {position, data, itemView ->
            itemView?.findViewById<TextView>(R.id.label_center)?.text = data
        }


        recycler_view2.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val data = mutableListOf<String>()
        val adapter = RVAdapterWrapper(MyAdapter(context!!, data))
        recycler_view2.adapter =adapter;
        btn_add.setOnClickListener {
            data.add("Text")
            adapter.notifyDataSetChanged()
        }
        btn_remove.setOnClickListener {
            data.removeAt(0)
            adapter.notifyDataSetChanged()
        }



        cb_go_wall.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // 添加话题
                doAddTopicTag("这是一段文本")
            } else {
                // 删除话题
                doRemoveTopicTag()

            }
        }

        // 开启不可分割Span
        et_input.enableIndivisibleTextSpan()
        // 添加Span变化监听器
        et_input.addSpanWatcher(object : SpanWatcher {
            override fun onSpanAdded(text: Spannable?, what: Any?, start: Int, end: Int) {
                Log.d(TAG, "onSpanAdded:$what")
            }

            override fun onSpanRemoved(text: Spannable?, what: Any?, start: Int, end: Int) {
                Log.d(TAG, "onSpanRemoved:$what")
                // 如果手动删除了tag，去掉勾选
                if (what is MyTagSpan) {
                    doRemoveTopicTag()
                }
            }

            override fun onSpanChanged(
                text: Spannable?,
                what: Any?,
                ostart: Int,
                oend: Int,
                nstart: Int,
                nend: Int
            ) {
            }

        })


    }

    fun doAddTopicTag(tag: String?) {
        if (!tag.isNullOrEmpty()) {
            addTopicTag(et_input, tag)
        }
    }

    fun doRemoveTopicTag() {
//        et_input.removeSpan()
        removeTopicTag(et_input)
        cb_go_wall.isChecked = false
    }


    private fun addTopicTag(editText: EditText, tag: String) {
        editText.text.insert(0, MyTagSpan(tag).toSpanString())
    }

    private fun removeTopicTag(editText: EditText) {
        val text = editText.text;
        val myClickSpans = text.getSpans(0 , text.length, MyTagSpan::class.java)
        myClickSpans.forEach {
            val start = text.getSpanStart(it)
            val end = text.getSpanEnd(it)
            Log.d(TAG, "removeTopicTag start:$start, end:$end")
            text.delete(start, end);
        }
    }


    class MyAdapter(var context: Context, var data: List<String>) : BaseAdapter() {
        override fun getCount(): Int {
            return data.size
        }

        override fun getItem(position: Int): Any {
            return data[position];
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var cv = convertView
            if (cv == null) {
                cv  = LayoutInflater.from(context).inflate(R.layout.item_card_layout, parent, false)
            }
            cv?.findViewById<TextView>(R.id.label_center)?.text = "pos:$position, text:${getItem(position)}"

            return cv!!
        }

    }

}








class MyTagSpan(var tagStr: String?) : ForegroundColorSpan(0xffff0000.toInt()), IndivisibleTextSpan {
    fun toSpanString(): CharSequence {
        return SpannableStringBuilder().apply {
            append("#$tagStr#", this@MyTagSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    override fun updateDrawState(textPaint: TextPaint) {
        textPaint.bgColor = 0xff0000ff.toInt();
        super.updateDrawState(textPaint)
    }
}