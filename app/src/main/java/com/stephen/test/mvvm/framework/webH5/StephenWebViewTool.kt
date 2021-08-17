package com.stephen.test.mvvm.framework.webH5

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import com.stephen.test.mvvm.framework.utils.ToolUtils
import org.apache.http.conn.ssl.AllowAllHostnameVerifier
import org.apache.http.conn.ssl.SSLSocketFactory
import java.lang.reflect.InvocationTargetException

/**
 * Created by Stephen on 2016/5/7.
 */
class StephenWebViewTool(private val activity: Activity) {
    companion object{
        val REQUEST_CODE_GALLERY: Int = 888
    }

    var bridgeWebView: LollipopFixedWebView = LollipopFixedWebView(activity)

    fun initMyBridgeWebView(notSetClient: Boolean, onWebViewListener: OnWebViewListener?) {
        if (null != bridgeWebView) {
            SSLSocketFactory.getSocketFactory().hostnameVerifier = AllowAllHostnameVerifier()
            bridgeWebView.setInitialScale(25) //default 0
            bridgeWebView.setVerticalScrollBarEnabled(false)
            bridgeWebView.setHorizontalScrollBarEnabled(false)
            setWebViewSetting(bridgeWebView.getSettings())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) WebView.setWebContentsDebuggingEnabled(true)
            val bridgeWebViewClient = MyWebViewClient(onWebViewListener)
            if (!notSetClient) bridgeWebView.setWebViewClient(bridgeWebViewClient)
            val bridgeWebChromeClient = MyWebChromeClient(onWebViewListener)
            if (!notSetClient) bridgeWebView.setWebChromeClient(bridgeWebChromeClient)
        } // end of if
        //if(null != bridgeWebView)bridgeWebView.init(activity,notSetClient,onWebViewListener);
    }

    private fun setWebViewSetting(settings: WebSettings) {
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        //让加载进来的页面自适应手机屏幕分辨率居中显示
        //settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL)
        settings.textZoom = 100//设置字体放大大小100(高版本使用)
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        //关闭安全浏览模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.safeBrowsingEnabled = false
        }
        //解除数据阻止
        settings.blockNetworkImage = false
        //同时加载Https和Http混合模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) settings.mixedContentMode =
            WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        // Set the nav dump for HTC 2.x devices (disabling for ICS, deprecated entirely for Jellybean 4.2)
        try {
            val gingerbread_getMethod = WebSettings::class.java.getMethod(
                "setNavDump", *arrayOf<Class<*>?>(
                    Boolean::class.javaPrimitiveType
                )
            )
            val manufacturer = Build.MANUFACTURER
            ToolUtils.instance.debugPrintln("CordovaWebView is running on device made by: $manufacturer")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB && Build.MANUFACTURER.contains("HTC")) {
                gingerbread_getMethod.invoke(settings, true)
            }
        } catch (e: NoSuchMethodException) {
            ToolUtils.instance.debugPrintln("We are on a modern version of Android, we will deprecate HTC 2.3 devices in 2.8")
        } catch (e: IllegalArgumentException) {
            ToolUtils.instance.debugPrintln("Doing the NavDump failed with bad arguments")
        } catch (e: IllegalAccessException) {
            ToolUtils.instance.debugPrintln("This should never happen: IllegalAccessException means this isn't Android anymore")
        } catch (e: InvocationTargetException) {
            ToolUtils.instance.debugPrintln("This should never happen: InvocationTargetException means this isn't Android anymore.")
        }
        // Jellybean rightfully tried to lock this down. Too bad they didn't give us a whitelist while we do this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.allowUniversalAccessFromFileURLs = true
        } else {
            try {
                val clazz: Class<*> = settings.javaClass
                val method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", Boolean::class.javaPrimitiveType)
                method?.invoke(settings, true)
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        //We don't save any form data in the application
        settings.saveFormData = false
        //settings.setSavePassword(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) settings.mediaPlaybackRequiresUserGesture = false
        // Enable database
        // We keep this disabled because we use or shim to get around DOM_EXCEPTION_ERROR_16
        val databasePath = activity.applicationContext.getDir("database", Context.MODE_PRIVATE).path
        settings.databaseEnabled = true
        //settings.setDatabasePath(databasePath);
        //settings.setGeolocationDatabasePath(databasePath);
        // Enable DOM storage
        settings.domStorageEnabled = true
        // Enable built-in geolocation
        //settings.setGeolocationEnabled(true);
        settings.allowContentAccess = true
        settings.allowFileAccessFromFileURLs = true
        settings.allowUniversalAccessFromFileURLs = true
        // Enable AppCache
        // Fix for CB-2282
        settings.setAppCacheMaxSize(5 * 1048576.toLong())
        settings.setAppCachePath(databasePath)
        settings.setAppCacheEnabled(false)
        settings.domStorageEnabled = true
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        //缩放
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }

    internal inner class MyWebViewClient(private val onWebViewListener: OnWebViewListener?) : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            onWebViewListener?.onPageStarted(view, url)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            /*if(BuildConfig.DEBUG){//http://wechatfe.github.io/vconsole/lib/vconsole.min.js?v=3.2.0
                ToolUtils.instance.assetFile2Str(activity, "vconsole.min.js")?.let { ToolUtils.instance.webViewLoadJsStr(view, it) }
                ToolUtils.instance.webViewLoadJsStr(view, "new VConsole()")
            }// end of if*/
            onWebViewListener?.onPageFinished(view, url)
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            try {
                if (null != onWebViewListener) return onWebViewListener.onOverrideUrlLoading(view, request?.url?.path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) return
            onWebViewListener?.onReceivedError(view, false, errorCode, description, failingUrl)
        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            if(request?.isForMainFrame == true)onWebViewListener?.onReceivedError(view, false, error?.errorCode ?: -1, error?.description?.toString(), request?.url?.path)
        }

        /*override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
            super.onReceivedHttpError(view, request, errorResponse)
        }*/

        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
            super.onReceivedSslError(view, handler, error)
            onWebViewListener?.onReceivedError(view, true, error?.primaryError ?: -1, error?.toString(), error?.url)
            try {
                handler?.proceed()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
            //if (!url.isNullOrBlank() && url!!.contains("http://localhost/redirect"))activity?.finish()//拳头注册完成
            return super.shouldInterceptRequest(view, url)
        }
    }

    internal inner class MyWebChromeClient(private val onWebViewListener: OnWebViewListener?) : WebChromeClient() {
        private var filePathCallback: ValueCallback<Array<Uri?>>? = null

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            onWebViewListener?.onReceivedTitle(view, title)
        }

        override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
            result?.cancel()
            return super.onJsAlert(view, url, message, result)
        }

        override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
            result?.cancel()
            return super.onJsConfirm(view, url, message, result)
        }

        override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
            result?.cancel()
            return super.onJsPrompt(view, url, message, defaultValue, result)
        }

        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            println("===Stephen=============>==========onConsoleMessage==========>" + consoleMessage?.message())
            onWebViewListener?.onConsoleMessage(bridgeWebView, consoleMessage?.message())
            return super.onConsoleMessage(consoleMessage)
        }

        override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri?>>?, fileChooserParams: FileChooserParams?): Boolean {
            this.filePathCallback = filePathCallback
            try {//打开本地相册
                val intent = Intent()
                intent.action = Intent.ACTION_PICK
                intent.type = "image/*"
                activity.startActivityForResult(intent, REQUEST_CODE_GALLERY)
            } catch (e: Exception) {
                e.printStackTrace()
                callBackSelectFile(null)
            }
            return true
        }

        override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback) {
            callback.invoke(origin, true, false)
            super.onGeolocationPermissionsShowPrompt(origin, callback)
        }

        private fun callBackSelectFile(results: Array<Uri?>?){
            filePathCallback?.onReceiveValue(results)
            filePathCallback = null
        }

        fun callOnActivityResult(requestCode: Int?, resultCode: Int?, data: Intent?) {
            when (requestCode) {
                REQUEST_CODE_GALLERY -> {
                    if (null != data) {
                        var results: Array<Uri?>? = null
                        val dataString: String? = data.dataString
                        if (!TextUtils.isEmpty(dataString)) {
                            results = arrayOf(Uri.parse(dataString))
                        } else {
                            val clipData: ClipData? = data.clipData
                            if (null != clipData) {
                                results = arrayOfNulls(clipData.itemCount)
                                for (i in 0 until clipData.itemCount) {
                                    val item = clipData.getItemAt(i)
                                    results[i] = item.uri
                                } // end of for
                            }// end of if
                        }
                        callBackSelectFile(results)
                    } else {
                        callBackSelectFile(null)
                    }
                }
                else -> callBackSelectFile(null)
            }
        }
    }

    fun execJsMethod(jsMethodName: String?, param: Any?=null) {
        bridgeWebView?.evaluateJavascript("javascript:$jsMethodName(${param ?: ""})"){
            println("===Stephen====execJsMethod====result====>$it")
        }
    }

    fun loadUrl(loadUrl: String?) {
        ToolUtils.instance.debugPrintln("=============>加载链接:$loadUrl")
        bridgeWebView?.loadUrl(loadUrl ?: "")
    }

    fun reloadUrl() {
        bridgeWebView?.reload()
    }

    //选择图片回调必须在activity界面的回调处回调才起作用
    @SuppressLint("NewApi")
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(null != bridgeWebView && null != bridgeWebView!!.webChromeClient && bridgeWebView!!.webChromeClient!! is MyWebChromeClient){
            (bridgeWebView!!.webChromeClient!! as MyWebChromeClient).callOnActivityResult(requestCode, resultCode, data)
        }// end of if
    }

    fun setWebHtmlLoadMode(isHardWare: Boolean?) {
        bridgeWebView?.setLayerType(if (null != isHardWare && isHardWare) View.LAYER_TYPE_HARDWARE else View.LAYER_TYPE_SOFTWARE, null)
    }
}