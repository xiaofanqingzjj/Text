package com.example.test.webview

// webView


// X5
//import com.tencent.smtt.export.external.interfaces.*
//import com.tencent.smtt.sdk.*

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.test.R


/**
 * WebView容器
 *
 * @author fortune
 */
open class WebViewFragmentSytem : Fragment() {

    companion object {
        const val TAG = "WebViewFragment##"

        const val PARAM_URL = "url"

        fun instance(url: String?): WebViewFragmentSytem {
            return WebViewFragmentSytem().apply {
                arguments = Bundle().apply {
                    putString(PARAM_URL, url)
                }
            }
        }

//        private val mJavaScriptPluginClasses: MutableMap<String, Class<out WebViewJavaScriptPlugin>> = ConcurrentHashMap()
//
//        /**
//         * 给WebView注册本地js接口
//         *
//         * 使用静态方法注册主要是为了使WebView容器和插件解偶，这样插件和容器可以完全独立
//         */
//        fun registerJavascriptInterface(name: String, interfaceClass:Class<out WebViewJavaScriptPlugin>) {
//            mJavaScriptPluginClasses[name] = interfaceClass
//        }
//
//
        /**
         * 判断加载的url是否要在后面添加通用参数
         */
        private fun shouldAddCommonParam(url: String?): Boolean {
            return true
        }




    }

    private var mLoadingView: View? = null
//    private var mErrorView: EmptyView? = null

    protected var mContentContainer: FrameLayout? = null
    private var mWebView: StoryWebViewSytem? = null

    protected var mOrgUrl: String? = null
    private var mUrl: String? = null


    /**
     * 注册给js调用的对象集合
     */
    private var mJavaInterfaceObjs: MutableMap<String, Any?> = mutableMapOf()

    /**
     * 是否懒加载数据
     */
    var isLazyLoading = false


    /**
     * 一些回调处理事件
     */
    var mCallback: Callback? = null

    var refreshCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 解析参数
        arguments?.run {
            mOrgUrl = "http://dev.story.qq.com/dialogue_drama/index.html#/author?bookId=100040&chapterId=5&isRoot=true" //getString(PARAM_URL)
        }

        mOrgUrl = "http://dev.story.qq.com/dialogue_drama/index.html#/author?bookId=100040&chapterId=5&isRoot=true" //getString(PARAM_URL)
//        mOrgUrl = "http://www.baidu.com" //getString(PARAM_URL)
    }

    override fun onDestroy() {
        super.onDestroy()

        mWebView?.run {
            destroy()
        }

    }

    /**
     * 设置要加载的url
     */
    fun setUrl(url: String?) {
        if (arguments == null) {
            arguments = Bundle()
        }

        arguments?.run {
            putString(PARAM_URL, url)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_webview_debug, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        mContentContainer = findViewById(R.id.fl_content_container)
//
//        mLoadingView = findViewById(R.id.loading_view)
//        mErrorView = findViewById(R.id.empty_view)

        mWebView = view.findViewById(R.id.webview)

        // 初始化WebView
        initWebView(mWebView!!)

        // 添加Js接口
        registerJSInterface()

        //在webview初始化后，保底注入当前登录账号的的cookie；当设置了角色后，会使用角色的uin和logingType来刷新cookie。
//        val accountInfo: PlatformAccountInfo = AccountManager.getPlatformAccountInfo()
//        WebViewUtil.refreshCookie(accountInfo.uin, accountInfo.loginType)

        // 加载数据
        if (!isLazyLoading) {
            loadData()
        }
    }

//    override fun onFragmentFirstVisible() {
//        super.onFragmentFirstVisible()
//
//        if (isLazyLoading) loadData()
//    }


    /**
     * 在这里注册本地接口
     */
    protected fun registerJSInterface() {

//        mJavaScriptPluginClasses.forEach {
//            val name = it.key
//            val jsObj = it.value.newInstance()
//            addJavascriptInterface(name, jsObj)
//        }
    }


    /**
     * 开始加载数据
     */
    private fun loadData() {
        mUrl = if (shouldAddCommonParam(mOrgUrl)) {
            appendCommonParams(mOrgUrl)
        } else {
            mOrgUrl
        }

//        mUrl = if (refreshCount != 0) {
//            if (mUrl?.contains("?") == true) {
//                "$mUrl&version=$refreshCount"
//            } else {
//                "$mUrl?version=$refreshCount"
//            }
//        } else mUrl

//        mLoadingView?.show()
//        mErrorView?.hide()
//
//        DLog.d(TAG, "webView begin load url:$mUrl, orgUrl:$mOrgUrl")


        mWebView?.loadUrl(mUrl)
    }

    private fun reload() {
        refreshCount++
//        mWebView?.clearHistory()
//        mWebView?.clearCache(true)

//        loadData()
        mWebView?.reload()
    }

    /**
     * 兼容助手的WebView，在url后面添加通用参数
     */
    private fun appendCommonParams(orgUrl: String?): String? {
        return orgUrl
//        return WebViewUtil.addCFGroupParamToUrl(orgUrl, false)
    }


    /**
     * 网页处理返回
     * @return 网页处理了返回返回true，网页没处理返回返回false
     */
    fun goBack(): Boolean {
        return if (mWebView?.canGoBack() == true) {
            mWebView?.goBack()
            true
        } else false
    }
//
//    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
//        mJavaInterfaceObjs.forEach {
//            (it.value as? WebViewJavaScriptPlugin)?.onHostActivityRestoreInstanceState(savedInstanceState)
//        }
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        mJavaInterfaceObjs.forEach {
//            (it.value as? WebViewJavaScriptPlugin)?.onHostActivitySaveInstanceState(outState)
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        mJavaInterfaceObjs.forEach {
//            (it.value as? WebViewJavaScriptPlugin)?.onHostActivityActivityResult(requestCode, resultCode, data)
//        }
//    }
//
//    @SuppressLint("JavascriptInterface")
//    fun addJavascriptInterface(name: String, jsObj: Any) {
//        mWebView?.run {
//            if (jsObj is WebViewJavaScriptPlugin) {
//                jsObj.attach(this, this@WebViewFragmentSytem)
//            }
//            addJavascriptInterface(jsObj, name)
//        }
//
//        mJavaInterfaceObjs[name] = jsObj
//    }

    fun isNormalInjection(): Boolean {
        return true
    }


    private fun initWebView(mWebView: StoryWebViewSytem) {
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_")
        mWebView.removeJavascriptInterface("accessibility")
        mWebView.removeJavascriptInterface("accessibilityTraversal")

        // 设置setWebChromeClient对象
        mWebView.webChromeClient = object : WebChromeClient() {

            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                try {
                    val message = consoleMessage.message()
                    val lineNumber = consoleMessage.lineNumber()
                    val sourceID = consoleMessage.sourceId()
                    val messageLevel = consoleMessage.messageLevel().toString()
//                    Log.e(TAG, "onConsoleMessage mUrl:$mUrl, messageLevel:$messageLevel,  sourceId:$sourceID, lineNumber:$lineNumber, message:$message")
                } catch (e: Throwable) {
                }

                return super.onConsoleMessage(consoleMessage)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                mCallback?.onReceivedTitle(view?.url, title)
            }
        }

        mWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                Log.i(TAG, "shouldOverrideUrlLoading:$url")

                // 非http地址
                if ( // 如果是下载链接使用系统浏览器打开
                        url.endsWith(".apk")
                        || (!url.startsWith("http://") && !url.startsWith("https://"))) { // 检查是否内部跳转地址

                    if (url.startsWith("story://")) {
                        // story:// scheme的地址
//                        Jumper.jump(view.context, url)
                    } else { // 给系统来打开
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            mWebView.context.startActivity(intent)
                        } catch (e: Exception) {
                            Log.w(TAG, "open failed:" + e.message + url)
                        }
                    }
                    return true
                }
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

//                DLog.d(TAG, "onPageStarted:$url")
//                mErrorView?.visibility = View.GONE
            }

            override fun onPageFinished(view: WebView, url: String) {

//                DLog.d(TAG, "onPageFinished:$url")
//
//                //testJsApi();
//                val contentHeight: Int = mWebView.contentHeight
//                Log.d(TAG, "onPageFinish:$contentHeight")
//                mCallback?.onLoadFinish(url)
//
//                mLoadingView?.hide(true)
//
//                if (BuildConfig.DEBUG) {
////                    Utils.addVConsole(mWebView)
//                }
//
//                // 如果是空页面，显示空页面
//                if ("about:blank" == url) { //显示空页面
//                    mErrorView?.visibility = View.VISIBLE
//                    mErrorView?.onEmpty {
//                        reload()
//                    }
//                }
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
//                DLog.e(TAG, "onReceivedError request:$request, errorCode:${error?.errorCode}, description:${error?.description}")
//                handleLoadError(view, error?.errorCode ?: 0, error?.description as? String?)
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
//                DLog.e(TAG, "onReceivedError  error$errorCode, desc:$description, failingUrl:$failingUrl")
                handleLoadError(view, errorCode, description)
            }

            private fun handleLoadError(webView: WebView?, code: Int, msg: String?) {
//                mErrorView?.onError() {
//                    reload()
//                }
//                mErrorView?.visibility = View.VISIBLE
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (activity?.applicationInfo?.flags ?: 0 and ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true) //time consume, only open when debug
            }
        }

        mWebView.settings?.run {
            cacheMode = WebSettings.LOAD_DEFAULT
            javaScriptEnabled = true
            allowFileAccess = true
            layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS

            setSupportZoom(false)
            builtInZoomControls = true
            useWideViewPort = true

            setSupportMultipleWindows(false)

            displayZoomControls = false
            loadWithOverviewMode = true

            setAppCacheEnabled(true)
            databaseEnabled = true
            domStorageEnabled = true

            setGeolocationEnabled(true)
            setAppCacheMaxSize(Long.MAX_VALUE)

            pluginState = WebSettings.PluginState.ON_DEMAND
            setRenderPriority(WebSettings.RenderPriority.HIGH)
            mediaPlaybackRequiresUserGesture = false



            // coolpad的手机有点问题，useragent不能包含pad这个单词
            var agent = userAgentString
//            var newAgent: String? = null
            if (agent.contains("CoolPad")) {
                agent = agent.replace("CoolPad".toRegex(), "Xiaomi")
            }
            if (agent.contains("coolpad")) {
                agent = agent.replace("coolpad".toRegex(), "Xiaomi")
            }
            if (agent.contains("COOLPAD")) {
                agent = agent.replace("COOLPAD".toRegex(), "Xiaomi")
            }

//            // UA里带上助手标识让网页知道这是从我们APP访问的
//            val versionCode: Int = AppUtil.getVersionCode(context)
//            agent = "$agent;GameHelper/$versionCode"
//
//            userAgentString = agent
//            DLog.d(TAG, "ua:${userAgentString}")
        }


        // 设置下载监听
        mWebView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            try {
                val uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // ingore
            }
        }
    }


    /**
     * 一些回调
     */
    interface Callback {
        fun onReceivedTitle(url: String?, title: String?) {}
        fun onLoadFinish(url: String?) {}
    }
}