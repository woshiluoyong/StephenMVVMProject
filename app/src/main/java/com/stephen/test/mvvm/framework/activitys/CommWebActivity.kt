package com.stephen.test.mvvm.framework.activitys

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebView.HitTestResult
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ViewDataBinding
import com.android.puy.mvvm.base.NoViewModel
import com.android.puy.mvvm.base.XActivity
import com.android.puy.mvvm.utils.StephenCommonNoDataView
import com.android.puy.mvvm.utils.StephenCommonTopTitleView
import com.stephen.test.mvvm.framework.R
import com.stephen.test.mvvm.framework.utils.*
import com.stephen.test.mvvm.framework.webH5.OnMyWebViewListener
import com.stephen.test.mvvm.framework.webH5.StephenWebViewTool
import java.util.*

class CommWebActivity : XActivity<NoViewModel, ViewDataBinding>() {
    private var webUrl: String? = null
    private var stephenCommonTopTitleView: StephenCommonTopTitleView? = null
    private var stephenWebViewTool: StephenWebViewTool? = null
    private var isShowLoading: Boolean = true
    private var updateTitle: Boolean = true

    override fun initViewMode(): NoViewModel = getViewModel(NoViewModel::class.java)

    override fun getLayoutId(): Int = -1

    override fun getLayoutView(): View {
        stephenCommonTopTitleView = StephenCommonTopTitleView(this)
        stephenCommonTopTitleView!!.run {
            setTitleBgColor(ResourcesCompat.getColor(resources, R.color.white, null))
            setTitleLeftIcon(R.drawable.icon_back_btn, stephenCommonTopTitleView!!.getTitleLeftLp(25, 25, 15))
            setTitleRightIcon(R.drawable.icon_close_btn, stephenCommonTopTitleView!!.getTitleRightLp(25, 25, 15))
            val lp = stephenCommonTopTitleView?.getTitleCenterLp(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, -1)
            lp?.leftMargin = ToolUtils.instance.dp2px(this@CommWebActivity, 60f)
            lp?.rightMargin = ToolUtils.instance.dp2px(this@CommWebActivity, 60f)
            setTitleCenterText("Loading...", 18, "#FF212121", false, lp)
            setTitleLeftClickListener(View.OnClickListener {
                backClickEvent()
            })
            setTitleRightClickListener(View.OnClickListener {
                backToPrevActivity()
            })
        }

        val stephenWebViewCommonNoDataView = StephenCommonNoDataView(this)
        stephenWebViewCommonNoDataView!!.run {
            setMainNoDataBgColorVal(ResourcesCompat.getColor(resources, R.color.white, null))
            setMainContainerBgColorVal(ResourcesCompat.getColor(resources, R.color.white, null))
            setCenterTextViewStr(getString(R.string.request_data_hidden), arrayOf(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.CENTER_HORIZONTAL),
                25, 25, 100, -1)
            setCenterTextSizeSpAndColorVal(16, Color.parseColor("#666666"))
            setCenterText2ViewStr(getString(R.string.request_data_failure), 7)
            setCenterText2SizeSpAndColorVal(14, Color.parseColor("#B2B2B2"))
        }

        stephenWebViewTool = StephenWebViewTool(this)
        stephenWebViewTool!!.initMyBridgeWebView(false, object : OnMyWebViewListener(this,false,true,
            StephenCommonNoDataTool(this, stephenWebViewCommonNoDataView), object : View.OnClickListener{
                override fun onClick(v: View?) {
                    stephenWebViewTool?.loadUrl(webUrl!!)
                }
            }) {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                if (updateTitle) {
                    updatePageTitle(title)
                }
            }

            override fun onPageStarted(view: WebView?, url: String?) {
                super.onPageStarted(view, url)
                stephenWebViewTool!!.bridgeWebView.visibility = View.GONE
                if(isShowLoading)BusinessUtil.instance.showLoading()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                stephenWebViewTool!!.bridgeWebView.visibility = View.VISIBLE
                if(isShowLoading)BusinessUtil.instance.closeLoading()
            }

            override fun onConsoleMessage(view: WebView?, msg: String?) {
                if (!TextUtils.isEmpty(msg) && msg!!.contains("nativeBackPressEvent is not defined")) finish()
            }
        })

        return stephenCommonTopTitleView!!.injectCommTitleViewToAllViewReturnView(stephenWebViewCommonNoDataView!!.initAndInjectNoDataViewForAllView(stephenWebViewTool!!.bridgeWebView))
    }

    override fun initView(savedInstanceState: Bundle?) {}

    override fun initData() {
        initStatusBar(stephenCommonTopTitleView!!.topTitleId, true, R.color.white)
        updatePageTitle(intent.getStringExtra(Constant.ParamTitle))
        updateTitle = intent.getBooleanExtra(Constant.ParamUpdateTitle, true)
        try{isShowLoading = intent.getBooleanExtra(Constant.ParamBool, true)}catch (e: Exception){}
        webUrl = intent.getStringExtra(Constant.ParamBase) ?: ""

        stephenWebViewTool!!.bridgeWebView.setOnLongClickListener(OnLongClickListener {
            val hitTestResult: HitTestResult = stephenWebViewTool!!.bridgeWebView.hitTestResult//如果是图片类型或者是带有图片链接的类型
            if (hitTestResult.type == HitTestResult.IMAGE_TYPE || hitTestResult.type == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {}
            false //保持长按可以复制文字
        })

        stephenWebViewTool?.bridgeWebView?.addJavascriptInterface(H5CallNative(this), "sendData")
        stephenWebViewTool?.bridgeWebView?.addJavascriptInterface(H5CallNative(this), "UserCenter")
        stephenWebViewTool?.loadUrl(webUrl!!)
    }

    fun getWebUrl(): String? = webUrl

    private fun updatePageTitle(title: String?) {//如果设置过就只更新文本,避免更换了颜色等
        if (title.isNullOrBlank()) return
        if (stephenCommonTopTitleView!!.titleCenterView!! is TextView) (stephenCommonTopTitleView!!.titleCenterView!! as TextView).text = title
    }

    private fun backClickEvent() {
        if (null == stephenWebViewTool || null == stephenWebViewTool!!.bridgeWebView) backToPrevActivity()
        if (stephenWebViewTool!!.bridgeWebView.canGoBack()) {
            stephenWebViewTool!!.bridgeWebView.goBack()
        } else {
            backToPrevActivity()
        }
    }

    override fun backCheckOperation(): Boolean {
        backClickEvent()
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        stephenWebViewTool?.onActivityResult(requestCode, resultCode, data)
    }

    //Js交互方法
    inner class H5CallNative(val context: Activity) {

        @JavascriptInterface
        fun closeNativePage() {
            backToPrevActivity()
        }

    }
}