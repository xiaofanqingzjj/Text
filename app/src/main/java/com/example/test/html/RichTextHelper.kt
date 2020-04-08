package com.example.test.html

import android.content.Context
import android.text.Editable
import android.text.Html
import android.text.Spanned
import com.example.test.html.an.HtmlToSpannedConverter
import org.xml.sax.*
import java.util.*


/**
 * 一个自定义的Html解析器
 *
 * 系统的有一个Html.from方法可以把一段Html文本转换成Spanned对象，但是系统的Html解析几个问题：
 *
 * 1.可以使用ImageGetter接口来解析img标签，但是只能读取img标签的src属性，不能读取其他的属性
 * 2.可以使用TagHandler来解析那些系统不支持的标签，但是TabHandler的接口方法只透传了标签名没有透传标签的属性给标签处理器
 * 3.TabHandler只能处理那些系统不处理的标签， 没办法对系统本身支持的标签提供定制化解析
 *
 * HtmlParser扩展自Html.from()方法，使用了新的TagHandler对象来处理标签，该接口透传了标签的属性。
 * HtmlParser的标签处理是由用户先处理，用户不处理的标签才由系统处理。
 *
 * @author fortunexiao
 */
class RichTextHelper @JvmOverloads constructor(var context: Context) : Html.TagHandler, ContentHandler {

    companion object {


        /**
         * 解析html文本
         *
         * @param html 带html标签带文本
         * @param handlers
         */
        fun buildSpannedText(html: CharSequence, context: Context,  vararg handlers: TagHandler?): Spanned {

            // 在Html最前面加一个不支持的标签来替换系统的ContentHandler
            val parser = RichTextHelper(context)
            handlers?.forEach {
                if (it != null) {
                    parser.customHandlers.add(it)
                }
            }

//            return HtmlToSpannedConverter.convert(html.toString(), 0)

            return Html.fromHtml("<inject/>$html", null, parser)
        }

        fun getValue(attributes: Attributes, name: String): String? {
            var i = 0
            val n = attributes.length
            while (i < n) {
                if (name == attributes.getLocalName(i))
                    return attributes.getValue(i)
                i++
            }
            return null
        }
    }


//    val htmlTagHandler = object : Html.TagHandler {
//
//
//        override fun handleTag(opening: Boolean, tag: String?, output: Editable, xmlReader: XMLReader) {
//            // 处理标签
//            if (wrapped == null) {
//                // record result object
//                text = output
//
//                // 记住原来的ContentHandler
//                wrapped = xmlReader.contentHandler
//                // 替换成自己的ContentHandler
//                xmlReader.contentHandler = this
//                // handle endElement() callback for <inject/> tag
//                tagStatus.addLast(java.lang.Boolean.FALSE)
//            }
//        }
//    }
//
//    val contentHandler = object : ContentHandler {
//        override fun endElement(uri: String?, localName: String?, qName: String?) {
//        }
//
//        override fun processingInstruction(target: String?, data: String?) {
//        }
//
//        override fun startPrefixMapping(prefix: String?, uri: String?) {
//        }
//
//        override fun ignorableWhitespace(ch: CharArray?, start: Int, length: Int) {
//        }
//
//        override fun characters(ch: CharArray?, start: Int, length: Int) {
//        }
//
//        override fun endDocument() {
//        }
//
//        override fun startElement(uri: String?, localName: String?, qName: String?, atts: Attributes?) {
//        }
//
//        override fun skippedEntity(name: String?) {
//        }
//
//        override fun setDocumentLocator(locator: Locator?) {
//        }
//
//        override fun endPrefixMapping(prefix: String?) {
//        }
//
//        override fun startDocument() {
//        }
//
//    }
//



    /**
     * 自定义tab处理器
     */
    private val customHandlers: MutableList<TagHandler> = mutableListOf()

    init {
        customHandlers.add(LinkTagHandler())
        customHandlers.add(AtTagHandler())
    }

    fun registerTagHandler(tagHandler: TagHandler) {
        customHandlers.add(tagHandler)
    }


    /**
     * Tag处理器
     */
    interface TagHandler {
        /**
         * 标签处理
         * @param opening 标签的开始还是结束
         * @param tag 标签名
         * @param output 当前已经处理的父文本
         * @param attributes 标签的属性
         *
         * @return true表示处理了当前标签
         */
        fun handleTag(opening: Boolean, tag: String, output: Editable?, attributes: Attributes?): Boolean
    }




    private var wrapped: ContentHandler? = null
    private var text: Editable? = null
    private val tagStatus = ArrayDeque<Boolean>()


    /**
     * 处理标签
     */
    override fun handleTag(opening: Boolean, tag: String, output: Editable, xmlReader: XMLReader) {
        // 处理标签
        if (wrapped == null) {
            // record result object
            text = output

            // 记住原来的ContentHandler
            wrapped = xmlReader.contentHandler
            // 替换成自己的ContentHandler
            xmlReader.contentHandler = this
            // handle endElement() callback for <inject/> tag
            tagStatus.addLast(java.lang.Boolean.FALSE)
        }
    }

    // 处理开始标签
    @Throws(SAXException::class)
    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        // 先调用自己的处理器处理，自己的处理器不处理的情况丢给系统本身的处理器处理
//        val isHandled = customHandler?.handleTag(true, localName, text, attributes) ?: false

        val isHandled = customHandleTag(true, localName, text, attributes )

        tagStatus.addLast(isHandled)

        if (!isHandled) {
            wrapped?.startElement(uri, localName, qName, attributes)
        }
    }

    // 处理结束标签
    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {
        if (!tagStatus.removeLast()) {
            wrapped?.endElement(uri, localName, qName)
        }

        customHandleTag(false, localName, text, null)
    }


    // 标签内容
    @Throws(SAXException::class)
    override fun characters(ch: CharArray, start: Int, length: Int) {
        wrapped?.characters(ch, start, length)
    }


    private fun customHandleTag(isOpen: Boolean, localName: String, text: Editable?, attributes: Attributes?): Boolean {
        var isHandled = false
        customHandlers.firstOrNull {
            if (it.handleTag(isOpen, localName, text, attributes)) {
                isHandled = true
                true
            } else false
        }
        return isHandled
    }


    ////////////////////////////////
    // 其他实现ContentHandler接口方法
    ////////////////////////////////

    override fun setDocumentLocator(locator: Locator) {
        wrapped?.setDocumentLocator(locator)
    }

    @Throws(SAXException::class)
    override fun startDocument() {
        wrapped?.startDocument()
    }

    @Throws(SAXException::class)
    override fun endDocument() {
        wrapped?.endDocument()
    }

    @Throws(SAXException::class)
    override fun startPrefixMapping(prefix: String, uri: String) {
        wrapped?.startPrefixMapping(prefix, uri)
    }

    @Throws(SAXException::class)
    override fun endPrefixMapping(prefix: String) {
        wrapped?.endPrefixMapping(prefix)
    }


    @Throws(SAXException::class)
    override fun ignorableWhitespace(ch: CharArray, start: Int, length: Int) {
        wrapped?.ignorableWhitespace(ch, start, length)
    }

    @Throws(SAXException::class)
    override fun processingInstruction(target: String, data: String) {
        wrapped?.processingInstruction(target, data)
    }

    @Throws(SAXException::class)
    override fun skippedEntity(name: String) {
        wrapped?.skippedEntity(name)
    }
}
