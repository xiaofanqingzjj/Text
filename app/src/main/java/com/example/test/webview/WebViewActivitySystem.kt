//package com.example.test.webview
//
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import com.tencent.gamehelper.BackPressedHandler
//import com.tencent.gamehelper.BaseActivity
//import java.lang.Exception
//
//
///**
// * 新的WebViewActivity，替换掉助手的WebViewActivity
// *
// * @author fortune
// */
//class WebViewActivitySystem : BaseActivity() {
//
//    companion object {
//
//        const val PARAM_URL = "url"
//        const val PARAM_IS_FULLSCREEN = "IS_FULLSCREEN"
//        const val PARAM_TITLE = "title"
//
//        /**
//         * show
//         */
//        @JvmOverloads
//        fun show(context: Context, url: String?, title: String? = null, isFullScreen: Boolean = false) {
//            context.startActivity(Intent(context, WebViewActivitySystem::class.java).apply {
//                putExtra(PARAM_URL, url)
//                putExtra(PARAM_IS_FULLSCREEN, isFullScreen)
//                putExtra(PARAM_TITLE, title)
//            })
//        }
//
//        /**
//         * 显示对话体容器，对话体容器使用全屏暂时
//         */
//        fun showByDialog(context: Context, url: String? ) {
//            show(context, url, null, true)
//        }
//
//
//        /**
//         * 是否是全屏展示
//         */
//        private fun isFullscreen(url: String?, isFullScreen: Boolean): Boolean {
//            var isFullscreen = isFullScreen
//
//            if (!isFullscreen) {
//                // 判断url是否有全屏参数
//                val uri = Uri.parse(url)
//                isFullscreen =  try {
//                    if (uri.isHierarchical) {
//                        uri.getQueryParameter("fullscreen") == "1"
//                    } else false
//                } catch (e: Exception) { false }
//            }
//            return isFullscreen
//        }
//
//
//
//    }
//
//    private var mWebViewFragment: WebViewFragmentSytem? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val url = intent?.getStringExtra(PARAM_URL)
//        val isFullScreen = intent?.getBooleanExtra(PARAM_IS_FULLSCREEN, false) ?: false
//        val title = intent?.getStringExtra(PARAM_TITLE) ?: ""
//
//        if (isFullscreen(url, isFullScreen)) {
//            hideActionBar()
//        }
//
//        setTitle(title)
//
//        // 创建WebViewFragment
//        mWebViewFragment = createWebViewFragment()
//        mWebViewFragment?.setUrl(url)
//
//        replaceFragment(mWebViewFragment!!)
//
//        // 处理返回按钮
//        addBackPressHandler(BackPressedHandler { false
//            mWebViewFragment?.goBack() ?: false
//        })
//    }
//
//    private fun createWebViewFragment(): WebViewFragmentSytem {
//        // 创建WebViewFragment
//        return WebViewFragmentSytem().apply {
//            mCallback = object : WebViewFragmentSytem.Callback {
//                override fun onReceivedTitle(url: String?, title: String?) {
//                    if (title != null) {
//                        setTitle(title)
//                    }
//                }
//            }
//        }
//    }
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        mWebViewFragment?.onActivityResult(requestCode, resultCode, data)
//
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        mWebViewFragment?.onSaveInstanceState(outState)
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
//        super.onRestoreInstanceState(savedInstanceState)
//        mWebViewFragment?.onRestoreInstanceState(savedInstanceState)
//    }
//
//
//}