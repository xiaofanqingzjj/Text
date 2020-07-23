package com.example.test.record

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import com.bedrock.module_base.SimpleFragment
import com.example.test.R
import com.fortunexiao.tktx.runUIThread
import kotlinx.android.synthetic.main.fragment_test_record.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class TestRecord : SimpleFragment() {

    companion object {
        const val TAG = "TestRecord"
    }


    private val mediaPlayerManager = MediaPlayerManager()

    private val mFiles = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_test_record)


    }

    private var mAdapter: BaseAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_record.setOnClickListener {

            //初始化录音

            //初始化录音
            val fileName = SimpleDateFormat("yyyyMMddhhmmss").format(Date())
            AudioRecordManager.startRecord(FileUtil.getPcmFileAbsolutePath(fileName))
        }

        btn_stop_record.setOnClickListener {
            AudioRecordManager.stopRecord()

            runUIThread(100) {
                refreshList()
            }
        }


        mAdapter = MyAdapter(context!!, mFiles)
        lv.adapter = mAdapter

        lv.setOnItemClickListener { parent, view, position, id ->
            val file = mAdapter?.getItem(position) as? File
            if (file != null) {
                mediaPlayerManager.playAudio(file.absolutePath, isLoop = true)
            }
        }

        refreshList()
    }

    private fun refreshList() {
        mFiles.clear()

        mFiles.addAll(FileUtil.getPcmFiles())

        (lv.adapter as BaseAdapter).notifyDataSetChanged()
    }

    class MyAdapter(context: Context, file: List<File>) : ArrayAdapter<File>(context, 0, file) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var cv = convertView
            if (cv == null) {
                cv = View.inflate(context, android.R.layout.simple_list_item_2, null)
            }

            cv?.findViewById<TextView>(android.R.id.text1)?.text = getItem(position)?.name
            cv?.findViewById<TextView>(android.R.id.text2)?.text = "${formatSize(getItem(position)?.length() ?: 0)}"

            return cv!!;
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        mediaPlayerManager?.release()
    }
}