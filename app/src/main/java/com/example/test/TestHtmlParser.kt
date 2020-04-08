package com.example.test

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import com.bedrock.module_base.SimpleFragment
import com.example.test.html.*
import kotlinx.android.synthetic.main.fragment_test_html_parser.*

class TestHtmlParser : SimpleFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_test_html_parser)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txt = "Now you can <b>browse</b> privately, 中文<a href='http://www.qq.com'>这是一段链接</a> 拉拉啊拉啊， <p> and other people who use this device won’t see your activity. However, downloads and bookmarks will be saved. Learn more\n" +
                "Chrome <tt>won’t</tt> save the following information:\n" +
                "<ul> ul Your browsing history</ul>" +
                "<li> li Cookies and site data</li>" +
                "* Information entered in forms\n" +
                "Your activity might still be visible to:\n" +
                "<em>Websites you visit</em><br>" +
                "* Your employer or school\n" +
                "* Your <at userId='11'>@internet</at> service " +
                "<a href='http://www.qq.com'>腾讯</a>"

//        val txt = "adasfsad"

//        text1.movementMethod = LinkMovementMethod.getInstance()
//        text1.setOnTouchListener(ClickT)
        text1.text = RichTextHelper.buildSpannedText(txt, context!!)
        text1.setOnTouchListener(ClickableSpanTouchListener)

        // 1.调用该方法支持富文本输入
//        edit_text.supportRichInput()

        btn_add_link.setOnClickListener {

            // 2.添加链接
            edit_text.appendLink("百度度度度度度度度度度度度度度度度度度度度度度度度度度度度度度度度度度度度度度度度度度度度度", "http://www.baidu.com")
        }
        btn_add_at.setOnClickListener {

            // 3.添加@人
            edit_text.appendAt("大哥", 11)
        }

        btn_gettext.setOnClickListener {

            // 4.根据输入的内容转化成html标签
            val text = edit_text.getPrimitiveText()

            text1.text = text
            text2.text = RichTextParser.parse(context!!, txt)
        }
    }


}