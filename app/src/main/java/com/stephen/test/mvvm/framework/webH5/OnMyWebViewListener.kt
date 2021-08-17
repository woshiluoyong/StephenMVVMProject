package com.stephen.test.mvvm.framework.webH5

import android.content.DialogInterface
import android.text.TextUtils
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.stephen.test.mvvm.framework.R
import com.stephen.test.mvvm.framework.utils.BusinessUtil
import com.stephen.test.mvvm.framework.utils.StephenCommonNoDataTool
import com.stephen.test.mvvm.framework.utils.ToolUtils

/**
 * Created by Stephen on 2016/5/7.
 */
abstract class OnMyWebViewListener(private val activity: AppCompatActivity, val isShowLoading: Boolean, private val isResponseClick: Boolean,
                                   private val stephenCommonNoDataTool: StephenCommonNoDataTool?, private val bottomBtnClickListener: View.OnClickListener? = null) : OnWebViewListener {
    var isLoadError: Boolean = false
    var isLoadFinish: Boolean = false

    override fun onReceivedTitle(view: WebView?, title: String?) {
        ToolUtils.instance.debugPrintln("=========$isLoadFinish====>获取该页面的Title:$title")
        if (!TextUtils.isEmpty(title)) {
            if (null != stephenCommonNoDataTool && (title == "找不到网页" || title!!.toLowerCase() == "错误" || title.toLowerCase() == "404 not found"
                        || title.toLowerCase().contains("error") || title.toLowerCase().contains("网页无法打开"))) {
                stephenCommonNoDataTool.commonNoDataViewShow(serverNotData = true, isEmptyResponseClick = isResponseClick, hintMsg = activity.getString(R.string.request_data_exception), onceBottomBtnClickListener = bottomBtnClickListener)
            } //end of if
        } else { //title获取为空的情况,是有时加载服务器页面,服务器返回的html代码只有"<head></head><body></body>",造成app这边显示白板,所以根据这种情况可尝试重试
            onExtendFunction("titleEmpty", isResponseClick)
        }
    }

    override fun onPageStarted(view: WebView?, url: String?) {
        isLoadError = false
        isLoadFinish = false
        stephenCommonNoDataTool?.stephenCommonNoDataView?.setNoDataViewHide()
        try {
            if(isShowLoading)BusinessUtil.instance.showLoading("卖力加载页面中...",true, DialogInterface.OnDismissListener {
                onExtendFunction("loadingDismiss")
            })
        } catch (e: Exception) {}
    }

    override fun onPageFinished(view: WebView?, url: String?) { //StephenToolUtils.showShortHintInfo(context,"页面加载完成...");
        isLoadFinish = true
        if (!isLoadError) stephenCommonNoDataTool?.stephenCommonNoDataView?.setNoDataViewHide()
        BusinessUtil.instance.closeLoading()
        val title = if (null != view) view.title else "PlaceHolderTitle"//占位Title,避免误判
        ToolUtils.instance.debugPrintln("===========onPageFinished====title===>$title")
        if (TextUtils.isEmpty(title)) onExtendFunction("titleEmpty", isResponseClick)
    }

    override fun onOverrideUrlLoading(view: WebView?, url: String?): Boolean { //ToolUtils.instance.logPrintln("=========onOverrideUrlLoading====>"+url);
        return false
    }

    override fun onReceivedError(view: WebView?, isSslError: Boolean, errorCode: Int, description: String?, failingUrl: String?) { //ToolUt
        if(!failingUrl.isNullOrBlank() && (failingUrl.endsWith(".png", true) || failingUrl.endsWith(".jpg", true)
                    || failingUrl.endsWith(".jpeg", true) || failingUrl.endsWith(".gif", true) || failingUrl.endsWith(".bmp", true)
                    || failingUrl.endsWith(".js", true) || failingUrl.endsWith(".css", true)))return
        // ils.instance.logPrintln(errorCode+"======load "+failingUrl+" error==========>"+description);
        isLoadError = true
        BusinessUtil.instance.closeLoading()
        stephenCommonNoDataTool?.commonNoDataViewShow(serverNotData = true, isEmptyResponseClick = true, hintMsg = activity.getString(R.string.request_data_hidden), onceBottomBtnClickListener = bottomBtnClickListener)
    }

    override fun onExtendFunction(tag: Any, param: Any?) {}
}