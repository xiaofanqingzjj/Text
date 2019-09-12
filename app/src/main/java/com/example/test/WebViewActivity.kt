package com.example.test

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.webkit.*

class WebViewActivity : Activity() {

    companion object {
        val URL = "url"

        val TAG = "WebViewActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_webview)

        val webView = findViewById<WebView>(R.id.webview)

        webView.webChromeClient = object : WebChromeClient() {

        }

        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                Log.d(TAG, "onPageStarted:$url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                Log.i(TAG, "onPageFinished:$url")
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)

//                Log.d(TAG, "onReceivedError:${request?.url}, error:${error.toString()}")
            }



        }

        webView.settings.run {
            javaScriptEnabled = true
        }


        val url = intent?.getStringExtra(URL)
        webView.loadUrl(url)
    }


}