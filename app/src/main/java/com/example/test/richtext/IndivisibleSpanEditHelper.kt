package com.example.test.richtext

import android.text.*
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import kotlin.math.abs
import kotlin.reflect.KClass

/**
 * 监听删除按钮
 */
private class OnDelKeyListener(): View.OnKeyListener {

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        return (v as? EditText)?.run {
            if (keyCode == KeyEvent.KEYCODE_DEL && event?.action == KeyEvent.ACTION_DOWN) {
                onDelDown(this.text)
            }
            false
        } ?: false
    }


    private fun onDelDown(text: Spannable): Boolean {
        // 获取当前光标的位置
        val selectionStart = Selection.getSelectionStart(text)
        val selectionEnd = Selection.getSelectionEnd(text)

        // 当前的光标前面是否是一个span，如果是则选择span，这样删除的时候会把整个span删除
        val myClickSpans = text.getSpans(selectionStart, selectionEnd, IndivisibleTextSpan::class.java)
        val currentEndSpan = myClickSpans.firstOrNull {
            text.getSpanEnd(it) == selectionStart
        }

        // 光标前面有span
        currentEndSpan?.run {
            if (selectionStart == selectionEnd) { // 当前是闪烁光标，不是选择光标，则做选择操作
                val start = text.getSpanStart(this)
                val end = text.getSpanEnd(this)
                Selection.setSelection(text, start, end)
            }
        }

        return false
    }
}


private class NoCopySpanEditableFactory(private vararg val spans: NoCopySpan): Editable.Factory() {
    override fun newEditable(source: CharSequence): Editable {
        return SpannableStringBuilder.valueOf(source).apply {
            spans.forEach {
                setSpan(it, 0, source.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }
    }
}

/**
 *
 */
private class SpanWatcherGroup(var spanWatchers: MutableList<SpanWatcher> = mutableListOf()) : SpanWatcher {
    override fun onSpanAdded(text: Spannable?, what: Any?, start: Int, end: Int) {
        spanWatchers.forEach {
            it.onSpanAdded(text, what, start, end)
        }
    }

    override fun onSpanRemoved(text: Spannable?, what: Any?, start: Int, end: Int) {
        spanWatchers.forEach {
            it.onSpanRemoved(text, what, start, end)
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
        spanWatchers.forEach {
            it.onSpanChanged(text, what, ostart, oend, nstart, nend)
        }
    }

}


/**
 * 不允许把光标挪到某个span的内部
 * 当光标移入特定的span中之后，把光标挪到span的末尾
 */
private class SelectionSpanWatcher<T: Any>(private val kClass: KClass<T>): SpanWatcher {

    companion object {
        const val TAG = "SelectionSpanWatcher"
    }

    // 光标的位置
    private var cursorPos = 0

    override fun onSpanChanged(text: Spannable, what: Any, ostart: Int, oend: Int, nstart: Int, nend: Int) {

        // 如果start和end相同，表示光标的移动
        if (what === Selection.SELECTION_START || what === Selection.SELECTION_END) {

            Log.d(TAG, "onSpanChanged, what:$what, oStart:$ostart, oend:$oend, nstart:$nstart, nend:$nend")

            // 记录下start的位置
            if (what === Selection.SELECTION_START) {
                cursorPos = nstart
            }

            if (what === Selection.SELECTION_END) {

                // 当start和end相同才表示光标的移动
                if (nstart == cursorPos) {

                    // 找到光标上的span，把光标重新移到span的开头或者结尾
                    text.getSpans(nstart, nend, kClass.java).firstOrNull()?.run {

                        // 找到span的开始和结束位置
                        val spanStart = text.getSpanStart(this)
                        val spanEnd = text.getSpanEnd(this)

                        // 找到和用户触摸最接近的start或者end
                        val targetIndex = if (abs(cursorPos - spanEnd) > abs(cursorPos - spanStart)) spanStart else spanEnd

                        // 把光标挪到对应的位置
                        Selection.setSelection(text, targetIndex)
                    }
                    // 用完之后清理下
                    cursorPos = 0
                }
            }
        }
    }

    override fun onSpanRemoved(text: Spannable?, what: Any?, start: Int, end: Int) {
    }

    override fun onSpanAdded(text: Spannable?, what: Any?, start: Int, end: Int) {
    }
}


/**
 * 不可分割的文本Span.
 * 在编辑框中，被当作一个整体对待，整个删除，且中间不允许插入光标
 * 比如话题名，@人等需要被当作一个整体的Span的对象应该实现该接口
 * @author fortunexiao
 */
interface IndivisibleTextSpan


/**
 * 所有实现IndivisibleSpan接口的Span会被当作一个整体对待，光标删除会整体删除，光标不能移动到文本的内部
 *
 */
fun EditText.enableIndivisibleTextSpan() {
    // 删除的时候整个删除span
    setOnKeyListener(OnDelKeyListener())

    // 设置一个SpanWatcherGroup，允许外部设置Span变化监听器
    val spanWatcher  = SpanWatcherGroup();

    // 不允许把光标插在标签span的内部
    spanWatcher.spanWatchers.add(SelectionSpanWatcher(IndivisibleTextSpan::class))
    setTag(0x7f010199, spanWatcher)
    setEditableFactory(NoCopySpanEditableFactory(spanWatcher))
}

/**
 * 添加Span变化监听器，该方法要在enableIndivisibleTextSpan之后调用才有效
 */
fun EditText.addSpanWatcher(spanWatcher: SpanWatcher) {
    val watchers = getTag(0x7f010199) as? SpanWatcherGroup
    watchers?.spanWatchers?.add(spanWatcher)
}

