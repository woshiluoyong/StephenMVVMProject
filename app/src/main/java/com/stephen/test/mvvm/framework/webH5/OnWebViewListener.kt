package com.stephen.test.mvvm.framework.webH5

import android.webkit.WebView

/**
 * Created by Stephen on 2016/5/7.
 */
interface OnWebViewListener {
    fun onPageStarted(view: WebView?, url: String?)
    fun onPageFinished(view: WebView?, url: String?)
    fun onOverrideUrlLoading(view: WebView?, url: String?): Boolean
    fun onReceivedError(view: WebView?, isSslError: Boolean, errorCode: Int, description: String?, failingUrl: String?)
    fun onReceivedTitle(view: WebView?, title: String?)
    fun onExtendFunction(tag: Any, param: Any?=null)
    fun onConsoleMessage(view: WebView?, msg: String?)
}