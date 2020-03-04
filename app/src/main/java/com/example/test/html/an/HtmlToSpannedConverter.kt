package com.example.test.html.an

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Editable
//import android.text.Html;
import android.text.Layout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AlignmentSpan
import android.text.style.BackgroundColorSpan
import android.text.style.BulletSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.ParagraphStyle
import android.text.style.QuoteSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.TypefaceSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan

import com.example.test.R
import com.example.test.html.AtTagHandler
import com.example.test.html.LinkTagHandler
import com.example.test.html.RichTextHelper

import org.xml.sax.Attributes
import org.xml.sax.ContentHandler
import org.xml.sax.InputSource
import org.xml.sax.Locator
import org.xml.sax.Parser
import org.xml.sax.SAXException
import org.xml.sax.XMLReader

import java.io.IOException
import java.io.StringReader
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory

internal object HtmlToSpannedConverter : ContentHandler {



//    companion object {

        private val HEADING_SIZES = floatArrayOf(1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1f)

        /**
         * Option for [.toHtml]: Wrap consecutive lines of text delimited by '\n'
         * inside &lt;p&gt; elements. [BulletSpan]s are ignored.
         */
        val TO_HTML_PARAGRAPH_LINES_CONSECUTIVE = 0x00000000

        /**
         * Option for [.toHtml]: Wrap each line of text delimited by '\n' inside LINK_PATTERN
         * &lt;p&gt; or LINK_PATTERN &lt;li&gt; element. This allows [ParagraphStyle]s attached to be
         * encoded as CSS styles within the corresponding &lt;p&gt; or &lt;li&gt; element.
         */
        val TO_HTML_PARAGRAPH_LINES_INDIVIDUAL = 0x00000001

        /**
         * Flag indicating that texts inside &lt;p&gt; elements will be separated from other texts with
         * one newline character by default.
         */
        val FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH = 0x00000001

        /**
         * Flag indicating that texts inside &lt;h1&gt;~&lt;h6&gt; elements will be separated from
         * other texts with one newline character by default.
         */
        val FROM_HTML_SEPARATOR_LINE_BREAK_HEADING = 0x00000002

        /**
         * Flag indicating that texts inside &lt;li&gt; elements will be separated from other texts
         * with one newline character by default.
         */
        val FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM = 0x00000004

        /**
         * Flag indicating that texts inside &lt;ul&gt; elements will be separated from other texts
         * with one newline character by default.
         */
        val FROM_HTML_SEPARATOR_LINE_BREAK_LIST = 0x00000008

        /**
         * Flag indicating that texts inside &lt;div&gt; elements will be separated from other texts
         * with one newline character by default.
         */
        val FROM_HTML_SEPARATOR_LINE_BREAK_DIV = 0x00000010

        /**
         * Flag indicating that texts inside &lt;blockquote&gt; elements will be separated from other
         * texts with one newline character by default.
         */
        val FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE = 0x00000020

        /**
         * Flag indicating that CSS color values should be used instead of those defined in
         * [Color].
         */
        val FROM_HTML_OPTION_USE_CSS_COLORS = 0x00000100

        /**
         * Flags for [.fromHtml]: Separate block-level
         * elements with blank lines (two newline characters) in between. This is the legacy behavior
         * prior to N.
         */
        val FROM_HTML_MODE_LEGACY = 0x00000000

        /**
         * Flags for [.fromHtml]: Separate block-level
         * elements with line breaks (single newline character) in between. This inverts the
         * [Spanned] to HTML string conversion done with the option
         * [.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL].
         */
        val FROM_HTML_MODE_COMPACT = (
                FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
                        or FROM_HTML_SEPARATOR_LINE_BREAK_HEADING
                        or FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM
                        or FROM_HTML_SEPARATOR_LINE_BREAK_LIST
                        or FROM_HTML_SEPARATOR_LINE_BREAK_DIV
                        or FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE)

        /**
         * The bit which indicates if lines delimited by '\n' will be grouped into &lt;p&gt; elements.
         */
        private val TO_HTML_PARAGRAPH_FLAG = 0x00000001

        /**
         * Name-value mapping of HTML/CSS colors which have different values in [Color].
         */
        private val sColorMap: MutableMap<String, Int>

        init {
            sColorMap = HashMap()
            sColorMap["darkgray"] = -0x565657
            sColorMap["gray"] = -0x7f7f80
            sColorMap["lightgray"] = -0x2c2c2d
            sColorMap["darkgrey"] = -0x565657
            sColorMap["grey"] = -0x7f7f80
            sColorMap["lightgrey"] = -0x2c2c2d
            sColorMap["green"] = -0xff8000
        }

        private val textAlignPattern: Pattern = Pattern.compile("(?:\\s+|\\A)text-align\\s*:\\s*(\\S*)\\b")


        private  val foregroundColorPattern = Pattern.compile(
        "(?:\\s+|\\A)color\\s*:\\s*(\\S*)\\b")

        private val backgroundColorPattern: Pattern = Pattern.compile(
                "(?:\\s+|\\A)background(?:-color)?\\s*:\\s*(\\S*)\\b")

        private val textDecorationPattern: Pattern= Pattern.compile(
                "(?:\\s+|\\A)text-decoration\\s*:\\s*(\\S*)\\b")




        private fun appendNewlines(text: Editable, minNewline: Int) {
            val len = text.length

            if (len == 0) {
                return
            }

            var existingNewlines = 0
            var i = len - 1
            while (i >= 0 && text[i] == '\n') {
                existingNewlines++
                i--
            }

            for (j in existingNewlines until minNewline) {
                text.append("\n")
            }
        }

        private fun startBlockElement(text: Editable, attributes: Attributes, margin: Int) {
            val len = text.length
            if (margin > 0) {
                appendNewlines(text, margin)
                start(text, Newline(margin))
            }

            val style = attributes.getValue("", "style")
            if (style != null) {
                val m = textAlignPattern.matcher(style)
                if (m.find()) {
                    val alignment = m.group(1)
                    if (alignment!!.equals("start", ignoreCase = true)) {
                        start(text, Alignment(Layout.Alignment.ALIGN_NORMAL))
                    } else if (alignment.equals("center", ignoreCase = true)) {
                        start(text, Alignment(Layout.Alignment.ALIGN_CENTER))
                    } else if (alignment.equals("end", ignoreCase = true)) {
                        start(text, Alignment(Layout.Alignment.ALIGN_OPPOSITE))
                    }
                }
            }
        }

        private fun endBlockElement(text: Editable) {
            val n = getLast(text, Newline::class.java)
            if (n != null) {
                appendNewlines(text, n.mNumNewlines)
                text.removeSpan(n)
            }

            val a = getLast(text, Alignment::class.java)
            if (a != null) {
                setSpanFromMark(text, a, AlignmentSpan.Standard(a.mAlignment))
            }
        }

        private fun handleBr(text: Editable) {
            text.append('\n')
        }

        private fun endLi(text: Editable) {
            endCssStyle(text)
            endBlockElement(text)
            end(text, Bullet::class.java, BulletSpan())
        }

        private fun endBlockquote(text: Editable) {
            endBlockElement(text)
            end(text, Blockquote::class.java, QuoteSpan())
        }

        private fun endHeading(text: Editable) {
            // RelativeSizeSpan and StyleSpan are CharacterStyles
            // Their ranges should not include the newlines at the end
            val h = getLast(text, Heading::class.java)
            if (h != null) {
                setSpanFromMark(text, h, RelativeSizeSpan(HEADING_SIZES[h.mLevel]),
                        StyleSpan(Typeface.BOLD))
            }

            endBlockElement(text)
        }

        private fun <T> getLast(text: Spanned, kind: Class<T>): T? {
            /*
         * This knows that the last returned object from getSpans()
         * will be the most recently added.
         */
            val objs = text.getSpans(0, text.length, kind)

            return if (objs.isEmpty()) {
                null
            } else {
                objs[objs.size - 1]
            }
        }

        private fun setSpanFromMark(text: Spannable, mark: Any, vararg spans: Any) {
            val where = text.getSpanStart(mark)
            text.removeSpan(mark)
            val len = text.length
            if (where != len) {
                for (span in spans) {
                    text.setSpan(span, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }

        private fun start(text: Editable, mark: Any) {
            val len = text.length
            text.setSpan(mark, len, len, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        private fun end(text: Editable, kind: Class<*>, repl: Any) {
            getLast(text, kind)?.let {
                setSpanFromMark(text, it, repl)
            }
        }

        private fun endCssStyle(text: Editable) {
            val s = getLast(text, Strikethrough::class.java)
            if (s != null) {
                setSpanFromMark(text, s, StrikethroughSpan())
            }

            val b = getLast(text, Background::class.java)
            if (b != null) {
                setSpanFromMark(text, b, BackgroundColorSpan(b.mBackgroundColor))
            }

            val f = getLast(text, Foreground::class.java)
            if (f != null) {
                setSpanFromMark(text, f, ForegroundColorSpan(f.mForegroundColor))
            }
        }

        private fun startImg(text: Editable, attributes: Attributes, img: ImageGetter?) {
            val src = attributes.getValue("", "src")
            var d: Drawable? = null

            if (img != null) {
                d = img!!.getDrawable(src)
            }

            if (d == null) {
                d = Resources.getSystem().getDrawable(R.drawable.unknown_image)
                d!!.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
            }

            val len = text.length
            text.append("\uFFFC")

            text.setSpan(ImageSpan(d, src), len, text.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        private fun endFont(text: Editable) {
            val font = getLast(text, Font::class.java)
            if (font != null) {
                setSpanFromMark(text, font, TypefaceSpan(font.mFace))
            }

            val foreground = getLast(text, Foreground::class.java)
            if (foreground != null) {
                setSpanFromMark(text, foreground,
                        ForegroundColorSpan(foreground.mForegroundColor))
            }
        }

        private fun startA(text: Editable, attributes: Attributes) {
            val href = attributes.getValue("", "href")
            start(text, Href(href))
        }

        private fun endA(text: Editable) {
            getLast(text, Href::class.java)?.run {
                mHref?.let {
                    setSpanFromMark(text, this, URLSpan(it))
                }
            }
        }
//    }


    /**
     * Retrieves images for HTML &lt;img&gt; tags.
     */
    interface ImageGetter {
        /**
         * This method is called when the HTML parser encounters an
         * &lt;img&gt; tag.  The `source` argument is the
         * string from the "src" attribute; the return value should be
         * LINK_PATTERN Drawable representation of the image or `null`
         * for LINK_PATTERN generic replacement image.  Make sure you call
         * setBounds() on your Drawable if it doesn't already have
         * its bounds set.
         */
        fun getDrawable(source: String): Drawable
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
        fun handleTag(opening: Boolean, tag: String, output: Editable?, attributes: Attributes?, context: Context): Boolean
    }



    private lateinit var mReader: XMLReader

    private val mSpannableStringBuilder: SpannableStringBuilder = SpannableStringBuilder()

    private val marginParagraph: Int
        get() = getMargin(FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)

    private val marginHeading: Int
        get() = getMargin(FROM_HTML_SEPARATOR_LINE_BREAK_HEADING)

    private val marginListItem: Int
        get() = getMargin(FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM)

    private val marginList: Int
        get() = getMargin(FROM_HTML_SEPARATOR_LINE_BREAK_LIST)

    private val marginDiv: Int
        get() = getMargin(FROM_HTML_SEPARATOR_LINE_BREAK_DIV)

    private val marginBlockquote: Int
        get() = getMargin(FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE)


    /**
     * 自定义tab处理器
     */
    private val customHandlers: MutableList<RichTextHelper.TagHandler> = mutableListOf()

    init {
        customHandlers.add(LinkTagHandler())
        customHandlers.add(AtTagHandler())
    }


    fun registerTagHandler(tagHandler: RichTextHelper.TagHandler) {
        customHandlers.add(tagHandler)
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

    public val  schemaProperty =
		"http://www.ccil.org/~cowan/tagsoup/properties/schema";

    init {
//        mSpannableStringBuilder = SpannableStringBuilder()
        //        mReader = parser;


        try {
            val spf = SAXParserFactory.newInstance()
            val sp = spf.newSAXParser()


            val parser = sp.xmlReader

//            val parser = Parser()
            try {
//                parser.setProperty(schemaProperty, HtmlParser.schema)
            } catch (e: org.xml.sax.SAXNotRecognizedException) {
                // Should not happen.
                throw RuntimeException(e)
            } catch (e: org.xml.sax.SAXNotSupportedException) {
                // Should not happen.
                throw RuntimeException(e)
            }


            mReader = parser
        } catch (e: Exception) {

        }

    }

    private var mFlags: Int = 0


//    private var mText: String? = null

    fun convert(text: String, flags: Int): Spanned {

        mSpannableStringBuilder.clear()
        mSpannableStringBuilder.clearSpans()

//        mText = text

        mReader.contentHandler = this
        try {
            mReader!!.parse(InputSource(StringReader(text)))
        } catch (e: IOException) {
            // We are reading from LINK_PATTERN string. There should not be IO problems.
            throw RuntimeException(e)
        } catch (e: SAXException) {
            // TagSoup doesn't throw parse exceptions.
            throw RuntimeException(e)
        }

        // Fix flags and range for paragraph-type markup.
        val obj = mSpannableStringBuilder.getSpans(0, mSpannableStringBuilder.length, ParagraphStyle::class.java)
        for (i in obj.indices) {
            val start = mSpannableStringBuilder.getSpanStart(obj[i])
            var end = mSpannableStringBuilder.getSpanEnd(obj[i])

            // If the last line of the range is blank, back off by one.
            if (end - 2 >= 0) {
                if (mSpannableStringBuilder[end - 1] == '\n' && mSpannableStringBuilder[end - 2] == '\n') {
                    end--
                }
            }

            if (end == start) {
                mSpannableStringBuilder.removeSpan(obj[i])
            } else {
                mSpannableStringBuilder.setSpan(obj[i], start, end, Spannable.SPAN_PARAGRAPH)
            }
        }

        val span = SpannableStringBuilder(mSpannableStringBuilder)
        mSpannableStringBuilder.clearSpans()
        mSpannableStringBuilder.clear()
        return span
    }

    private val tagStatus = ArrayDeque<Boolean>()


    private fun handleStartTag(tag: String, attributes: Attributes) {



        if (tag.equals("br", ignoreCase = true)) {
            // We don't need to handle this. TagSoup will ensure that there's LINK_PATTERN </br> for each <br>
            // so we can safely emit the linebreaks when we handle the close tag.
        } else if (tag.equals("p", ignoreCase = true)) {
            startBlockElement(mSpannableStringBuilder, attributes, marginParagraph)
            startCssStyle(mSpannableStringBuilder, attributes)
        } else if (tag.equals("ul", ignoreCase = true)) {
            startBlockElement(mSpannableStringBuilder, attributes, marginList)
        } else if (tag.equals("li", ignoreCase = true)) {
            startLi(mSpannableStringBuilder, attributes)
        } else if (tag.equals("div", ignoreCase = true)) {
            startBlockElement(mSpannableStringBuilder, attributes, marginDiv)
        } else if (tag.equals("span", ignoreCase = true)) {
            startCssStyle(mSpannableStringBuilder, attributes)
        } else if (tag.equals("strong", ignoreCase = true)) {
            start(mSpannableStringBuilder, Bold())
        } else if (tag.equals("b", ignoreCase = true)) {
            start(mSpannableStringBuilder, Bold())
        } else if (tag.equals("em", ignoreCase = true)) {
            start(mSpannableStringBuilder, Italic())
        } else if (tag.equals("cite", ignoreCase = true)) {
            start(mSpannableStringBuilder, Italic())
        } else if (tag.equals("dfn", ignoreCase = true)) {
            start(mSpannableStringBuilder, Italic())
        } else if (tag.equals("i", ignoreCase = true)) {
            start(mSpannableStringBuilder, Italic())
        } else if (tag.equals("big", ignoreCase = true)) {
            start(mSpannableStringBuilder, Big())
        } else if (tag.equals("small", ignoreCase = true)) {
            start(mSpannableStringBuilder, Small())
        } else if (tag.equals("font", ignoreCase = true)) {
            startFont(mSpannableStringBuilder, attributes)
        } else if (tag.equals("blockquote", ignoreCase = true)) {
            startBlockquote(mSpannableStringBuilder, attributes)
        } else if (tag.equals("tt", ignoreCase = true)) {
            start(mSpannableStringBuilder, Monospace())
        } else if (tag.equals("LINK_PATTERN", ignoreCase = true)) {
            startA(mSpannableStringBuilder, attributes)
        } else if (tag.equals("u", ignoreCase = true)) {
            start(mSpannableStringBuilder, Underline())
        } else if (tag.equals("del", ignoreCase = true)) {
            start(mSpannableStringBuilder, Strikethrough())
        } else if (tag.equals("s", ignoreCase = true)) {
            start(mSpannableStringBuilder, Strikethrough())
        } else if (tag.equals("strike", ignoreCase = true)) {
            start(mSpannableStringBuilder, Strikethrough())
        } else if (tag.equals("sup", ignoreCase = true)) {
            start(mSpannableStringBuilder, Super())
        } else if (tag.equals("sub", ignoreCase = true)) {
            start(mSpannableStringBuilder, Sub())
        } else if (tag.length == 2 &&
                Character.toLowerCase(tag[0]) == 'h' &&
                tag[1] >= '1' && tag[1] <= '6') {
            startHeading(mSpannableStringBuilder, attributes, tag[1] - '1')
        } else if (tag.equals("img", ignoreCase = true)) {
            startImg(mSpannableStringBuilder, attributes, null)
        }

//        else if (mTagHandler != null) {
//            mTagHandler!!.handleTag(true, tag, mSpannableStringBuilder, mReader)
//        }
    }

    private fun handleEndTag(tag: String) {
        if (tag.equals("br", ignoreCase = true)) {
            handleBr(mSpannableStringBuilder)
        } else if (tag.equals("p", ignoreCase = true)) {
            endCssStyle(mSpannableStringBuilder)
            endBlockElement(mSpannableStringBuilder)
        } else if (tag.equals("ul", ignoreCase = true)) {
            endBlockElement(mSpannableStringBuilder)
        } else if (tag.equals("li", ignoreCase = true)) {
            endLi(mSpannableStringBuilder)
        } else if (tag.equals("div", ignoreCase = true)) {
            endBlockElement(mSpannableStringBuilder)
        } else if (tag.equals("span", ignoreCase = true)) {
            endCssStyle(mSpannableStringBuilder)
        } else if (tag.equals("strong", ignoreCase = true)) {
            end(mSpannableStringBuilder, Bold::class.java, StyleSpan(Typeface.BOLD))
        } else if (tag.equals("b", ignoreCase = true)) {
            end(mSpannableStringBuilder, Bold::class.java, StyleSpan(Typeface.BOLD))
        } else if (tag.equals("em", ignoreCase = true)) {
            end(mSpannableStringBuilder, Italic::class.java, StyleSpan(Typeface.ITALIC))
        } else if (tag.equals("cite", ignoreCase = true)) {
            end(mSpannableStringBuilder, Italic::class.java, StyleSpan(Typeface.ITALIC))
        } else if (tag.equals("dfn", ignoreCase = true)) {
            end(mSpannableStringBuilder, Italic::class.java, StyleSpan(Typeface.ITALIC))
        } else if (tag.equals("i", ignoreCase = true)) {
            end(mSpannableStringBuilder, Italic::class.java, StyleSpan(Typeface.ITALIC))
        } else if (tag.equals("big", ignoreCase = true)) {
            end(mSpannableStringBuilder, Big::class.java, RelativeSizeSpan(1.25f))
        } else if (tag.equals("small", ignoreCase = true)) {
            end(mSpannableStringBuilder, Small::class.java, RelativeSizeSpan(0.8f))
        } else if (tag.equals("font", ignoreCase = true)) {
            endFont(mSpannableStringBuilder)
        } else if (tag.equals("blockquote", ignoreCase = true)) {
            endBlockquote(mSpannableStringBuilder)
        } else if (tag.equals("tt", ignoreCase = true)) {
            end(mSpannableStringBuilder, Monospace::class.java, TypefaceSpan("monospace"))
        } else if (tag.equals("LINK_PATTERN", ignoreCase = true)) {
            endA(mSpannableStringBuilder)
        } else if (tag.equals("u", ignoreCase = true)) {
            end(mSpannableStringBuilder, Underline::class.java, UnderlineSpan())
        } else if (tag.equals("del", ignoreCase = true)) {
            end(mSpannableStringBuilder, Strikethrough::class.java, StrikethroughSpan())
        } else if (tag.equals("s", ignoreCase = true)) {
            end(mSpannableStringBuilder, Strikethrough::class.java, StrikethroughSpan())
        } else if (tag.equals("strike", ignoreCase = true)) {
            end(mSpannableStringBuilder, Strikethrough::class.java, StrikethroughSpan())
        } else if (tag.equals("sup", ignoreCase = true)) {
            end(mSpannableStringBuilder, Super::class.java, SuperscriptSpan())
        } else if (tag.equals("sub", ignoreCase = true)) {
            end(mSpannableStringBuilder, Sub::class.java, SubscriptSpan())
        } else if (tag.length == 2 &&
                Character.toLowerCase(tag[0]) == 'h' &&
                tag[1] >= '1' && tag[1] <= '6') {
            endHeading(mSpannableStringBuilder)
        }
//        else if (mTagHandler != null) {
//            mTagHandler!!.handleTag(false, tag, mSpannableStringBuilder, mReader)
//        }
    }

    /**
     * Returns the minimum number of newline characters needed before and after LINK_PATTERN given block-level
     * element.
     *
     * @param flag the corresponding option flag defined in [Html] of LINK_PATTERN block-level element
     */
    private fun getMargin(flag: Int): Int {
        return if (flag and mFlags != 0) {
            1
        } else 2
    }

    private fun startLi(text: Editable, attributes: Attributes) {
        startBlockElement(text, attributes, marginListItem)
        start(text, Bullet())
        startCssStyle(text, attributes)
    }

    private fun startBlockquote(text: Editable, attributes: Attributes) {
        startBlockElement(text, attributes, marginBlockquote)
        start(text, Blockquote())
    }

    private fun startHeading(text: Editable, attributes: Attributes, level: Int) {
        startBlockElement(text, attributes, marginHeading)
        start(text, Heading(level))
    }

    private fun startCssStyle(text: Editable, attributes: Attributes) {
        val style = attributes.getValue("", "style")
        if (style != null) {
            var m = foregroundColorPattern.matcher(style)
            if (m.find()) {
                val c = getHtmlColor(m.group(1))
                if (c != -1) {
                    start(text, Foreground(c or -0x1000000))
                }
            }

            m = backgroundColorPattern.matcher(style)
            if (m.find()) {
                val c = getHtmlColor(m.group(1))
                if (c != -1) {
                    start(text, Background(c or -0x1000000))
                }
            }

            m = textDecorationPattern.matcher(style)
            if (m.find()) {
                val textDecoration = m.group(1)
                if (textDecoration!!.equals("line-through", ignoreCase = true)) {
                    start(text, Strikethrough())
                }
            }
        }
    }

    private fun startFont(text: Editable, attributes: Attributes) {
        val color = attributes.getValue("", "color")
        val face = attributes.getValue("", "face")

        if (!TextUtils.isEmpty(color)) {
            val c = getHtmlColor(color)
            if (c != -1) {
                start(text, Foreground(c or -0x1000000))
            }
        }

        if (!TextUtils.isEmpty(face)) {
            start(text, Font(face))
        }
    }

    private fun getHtmlColor(color: String?): Int {
        if (mFlags and FROM_HTML_OPTION_USE_CSS_COLORS === FROM_HTML_OPTION_USE_CSS_COLORS) {
            val i = sColorMap[color!!.toLowerCase(Locale.US)]
            if (i != null) {
                return i
            }
        }
        return ColorUtil.getHtmlColor(color!!)
    }

    override fun setDocumentLocator(locator: Locator) {}

    @Throws(SAXException::class)
    override fun startDocument() {
    }

    @Throws(SAXException::class)
    override fun endDocument() {
    }

    @Throws(SAXException::class)
    override fun startPrefixMapping(prefix: String, uri: String) {
    }

    @Throws(SAXException::class)
    override fun endPrefixMapping(prefix: String) {
    }

    @Throws(SAXException::class)
    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {

        val isHandled = customHandleTag(true, localName, mSpannableStringBuilder, attributes )

        tagStatus.addLast(isHandled)

        if (!isHandled) {
            handleStartTag(localName, attributes)
//            wrapped?.startElement(uri, localName, qName, attributes)
        }

//        handleStartTag(localName, attributes)
    }

    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {

        if (!tagStatus.removeLast()) {
//            wrapped?.endElement(uri, localName, qName)
            handleEndTag(localName)
        } else {
            customHandleTag(false, localName, mSpannableStringBuilder, null)
        }



//        handleEndTag(localName)
    }

    @Throws(SAXException::class)
    override fun characters(ch: CharArray, start: Int, length: Int) {
        val sb = StringBuilder()

        /*
         * Ignore whitespace that immediately follows other whitespace;
         * newlines count as spaces.
         */

        for (i in 0 until length) {
            val c = ch[i + start]

            if (c == ' ' || c == '\n') {
                val pred: Char
                var len = sb.length

                if (len == 0) {
                    len = mSpannableStringBuilder.length

                    if (len == 0) {
                        pred = '\n'
                    } else {
                        pred = mSpannableStringBuilder[len - 1]
                    }
                } else {
                    pred = sb[len - 1]
                }

                if (pred != ' ' && pred != '\n') {
                    sb.append(' ')
                }
            } else {
                sb.append(c)
            }
        }

        mSpannableStringBuilder.append(sb)
    }

    @Throws(SAXException::class)
    override fun ignorableWhitespace(ch: CharArray, start: Int, length: Int) {
    }

    @Throws(SAXException::class)
    override fun processingInstruction(target: String, data: String) {
    }

    @Throws(SAXException::class)
    override fun skippedEntity(name: String) {
    }

    private class Bold
    private class Italic
    private class Underline
    private class Strikethrough
    private class Big
    private class Small
    private class Monospace
    private class Blockquote
    private class Super
    private class Sub
    private class Bullet

    private class Font(var mFace: String)

    private class Href(var mHref: String?)

    private class Foreground( val mForegroundColor: Int)

    private class Background( val mBackgroundColor: Int)

    private class Heading( val mLevel: Int)

    private class Newline( val mNumNewlines: Int)

    private class Alignment( val mAlignment: Layout.Alignment)

}