package com.bedrock.module_base.views.recyclerviews

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bedrock.module_base.R
import com.bedrock.module_base.views.recyclerviews.PagingRecyclerView.FooterLoadMoreView.OnClickReloadListener

/**
 * 支持分页的RecyclerView
 *
 * Created by fortunexiao
 */
class PagingRecyclerView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) : HeaderFooterRecyclerView(context, attrs) {

    companion object {
        const val TAG = "PagingRecyclerView"
    }

    // 当前正在加载
    private var isLoading = false

    private var currentState = State.INIT

    private var mFooterLoadMoreView: FooterLoadMoreView? = null

    private var firstVisiblePosition = 0

    private var lastVisiblePosition = 0

    // 触发加载更多的监听器
    private var onLoadMoreListener: OnTriggerLoadMoreListener? = null

    /**
     * 是否开启了分页功能
     */
    private var pagingEnable = false
        set(value) {
            if (field != value) {
                if (value) {
                    mFooterLoadMoreView?.showOrHide(true)
                } else {
                    mFooterLoadMoreView?.showOrHide(false)
                }
            }
            field = value
        }



    init {
        mFooterLoadMoreView = FooterLoadMoreView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            setOnClickReloadListener(object : OnClickReloadListener {
                override fun onClickReload() {
                    state = State.HAS_MORE
                    triggerLoadMore()
                }
            })
        }
        addFooterView(mFooterLoadMoreView)

        pagingEnable = true

        // 初始状态
        resetState()

        //        addOnScrollListener(onScrollListener);
    }

    fun setOnLoadMoreListener(onLoadMoreListener: OnTriggerLoadMoreListener?) {
        this.onLoadMoreListener = onLoadMoreListener
    }


    fun removeInnerFootView() {
        removeFooterView(mFooterLoadMoreView)
    }


    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        Log.e(TAG, "onScrollStateChanged:$state")
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            checkShouldLoadMore()
        }
    }


    private val onScrollListener: OnScrollListener = object : OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            Log.d(TAG, "onScrolled")
            checkShouldLoadMore()
        }
    }

    private fun triggerLoadMore() {
        isLoading = true
        mFooterLoadMoreView?.showLoading(true)
        onLoadMoreListener?.onTriggerLoadMore()
    }

    private fun checkShouldLoadMore() {
        val layoutManager = layoutManager as? LinearLayoutManager?
        val visibleItemCount = layoutManager?.childCount ?: 0
        val totalItemCount = layoutManager?.itemCount ?: 0

        firstVisiblePosition = layoutManager?.findFirstVisibleItemPosition() ?: 0
        lastVisiblePosition = visibleItemCount + firstVisiblePosition

        Log.w(TAG, "checkShouldLoadMore lastPosition:$lastVisiblePosition, total:$totalItemCount, first:$firstVisiblePosition， currentState:$currentState, isLoading:$isLoading, pageEnable:$pagingEnable")
        if (!isLoading
                && currentState == State.HAS_MORE
                && pagingEnable

                && lastVisiblePosition >= totalItemCount
                && firstVisiblePosition >= 0
        ) {
            Log.i(TAG, "loadMore")
            triggerLoadMore()
        }
    }

    /**
     * 恢复到初始状态
     */
    fun resetState() {
        isLoading = false
        state = State.INIT
    }

    /**
     * 加载完成
     * @param pageLoadSuccess 当前页是否加载成功
     * @param hasMore 是否还有更多，当pageLoadSuccess为false，这个参数忽略
     */
    @Deprecated("")
    fun loadFinish(pageLoadSuccess: Boolean, hasMore: Boolean) {
        loadFinish(pageLoadSuccess, hasMore, true)
    }

    /**
     * 加载完成
     * @param pageLoadSuccess 当前页是否加载成功
     * @param hasMore 是否还有更多，当pageLoadSuccess为false，这个参数忽略
     *
     * @param enablePaging 是否开启分页功能， 一般界面上有一页数据才开启分页功能，如果没有数据，则不会开启分页功能
     */
    fun loadFinish(pageLoadSuccess: Boolean, hasMore: Boolean, enablePaging: Boolean) {
        isLoading = false
        val state = if (enablePaging) { // 有数据的情况下才开启分页功能
            if (!pageLoadSuccess) {
                State.PAGE_LOAD_FAILED
            } else if (!hasMore) {
                State.NO_MORE
            } else {
                State.HAS_MORE
            }
        } else { // 如果界面没有数据，则不显示底部loading
            State.INIT
        }
        Log.d(TAG, "loadFinish pageLoadSuccess:$pageLoadSuccess, hasMore:$hasMore, hasData:$enablePaging, state:$state")
        this.state = state
    }

    /**
     * 设置分页列表的状态
     * @param state
     */
    var state: State
        get() = currentState
        set(state) {
            Log.i(TAG, "setState:$state")
            currentState = state
            mFooterLoadMoreView?.setState(state)
        }

    /**
     * 是否显示底部加载更多Bar
     */
    fun showLoadMore(isShow: Boolean) {
        mFooterLoadMoreView?.showOrHide(isShow)
    }

    fun setLoadingText(loadingText: String?) {
        mFooterLoadMoreView?.setLoadingText(loadingText)
    }

    fun setLoadingFailText(loadingFailText: String?) {
        mFooterLoadMoreView?.setLoadingFailText(loadingFailText)
    }

    fun setNoMoreText(noMoreText: String?) {
        mFooterLoadMoreView?.setNoMoreText(noMoreText)
    }

    val loadMoreFooter: View?
        get() = mFooterLoadMoreView

    /**
     * 正常的情况下，如果没有更多，底部会显示一个"没有更多了"的提示，这个方法可以隐藏掉该提示
     *
     * @param noMoreHideFooter
     */
    fun setNoMoreHideFooter(noMoreHideFooter: Boolean) {
        mFooterLoadMoreView?.setNoMoreHideFooter(noMoreHideFooter)
    }

    /**
     * 滑动到底部监听器
     */
    interface OnTriggerLoadMoreListener {
        fun onTriggerLoadMore()
    }


    //=========================================
    /**
     * 组件的状态
     */
    enum class State {
        /**
         * 初始状态
         */
        INIT,
        /**
         * 有更多
         */
        HAS_MORE,
        /**
         * 分页加载失败
         */
        PAGE_LOAD_FAILED,
        /**
         * 到底了
         */
        NO_MORE
    }

    /**
     * 底部加载更多View
     */
    private class FooterLoadMoreView @JvmOverloads internal constructor(context: Context?, attrs: AttributeSet? = null) : FrameLayout(context!!, attrs) {

        private var currentState: State? = null
        private var container: View? = null
        private var progress: View? = null
        private var tvText: TextView? = null

        private var loadingText: String? = "玩命加载中..."
        private var loadingFailText: String? = "加载失败，点击重试"
        private var noMoreText: String? = "到底了～"

        /**
         * 当没有更多当时候隐藏底部加载条
         */
        private var isNoMoreHideFooter = false
        private var isShow = true

        private var onClickReloadListener: OnClickReloadListener? = null

        init {
            View.inflate(context, R.layout.footer_load_more, this)
            container = findViewById(R.id.container)
            progress = findViewById(R.id.progress)
            tvText = findViewById(R.id.text)


            container?.setOnClickListener(OnClickListener {
                onClickReloadListener?.onClickReload()
            })
            setState(State.INIT)
        }

        fun showLoading(isShow: Boolean) {
            progress?.visibility = if (isShow) View.VISIBLE else View.INVISIBLE
            tvText?.visibility = if (isShow) View.VISIBLE else View.INVISIBLE
        }

        fun setState(state: State?) {
            when (state) {
                State.INIT -> {
                    container?.visibility = View.GONE
                }
                State.HAS_MORE -> {
                    progress?.visibility = View.VISIBLE
                    tvText?.text = loadingText

                    container?.isClickable = false


                    showLoading(false)

                    if (isShow) {
                        container?.visibility = View.VISIBLE
                    }
                }
                State.PAGE_LOAD_FAILED -> {
                    progress?.visibility = View.GONE
                    tvText?.text = loadingFailText

                    container?.isClickable = true

                    if (isShow) {
                        container?.visibility = View.VISIBLE
                    }
                }
                State.NO_MORE -> {
                    progress?.visibility = View.GONE
                    tvText?.text = noMoreText
                    container?.isClickable = false
                    if (isShow) {
                        container?.visibility = View.VISIBLE
                    }
                    if (isNoMoreHideFooter) {
                        container?.visibility = View.GONE
                    }
                }
                else -> {}
            }
            currentState = state
        }

        private fun refreshText() {
            setState(currentState)
        }

        fun setLoadingText(loadingText: String?) {
            this.loadingText = loadingText
            refreshText()
        }

        fun setLoadingFailText(loadingFailText: String?) {
            this.loadingFailText = loadingFailText
            refreshText()
        }

        fun setNoMoreText(noMoreText: String?) {
            this.noMoreText = noMoreText
            refreshText()
        }

        fun setNoMoreHideFooter(noMoreHideFooter: Boolean) {
            isNoMoreHideFooter = noMoreHideFooter
        }

        fun showOrHide(isShow: Boolean) {
            if (isShow) {
                container?.visibility = View.VISIBLE
            } else {
                container?.visibility = View.GONE
            }
            this.isShow = isShow
        }

        fun setOnClickReloadListener(listener: OnClickReloadListener?) {
            onClickReloadListener = listener
        }

        interface OnClickReloadListener {
            fun onClickReload()
        }

    }


}