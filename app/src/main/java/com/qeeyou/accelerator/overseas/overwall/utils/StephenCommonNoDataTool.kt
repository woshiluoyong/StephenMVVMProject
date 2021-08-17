package com.qeeyou.accelerator.overseas.overwall.utils

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.android.puy.mvvm.utils.StephenCommonNoDataView
import com.qeeyou.accelerator.overseas.overwall.R

class StephenCommonNoDataTool @JvmOverloads constructor(private val activity: AppCompatActivity, val stephenCommonNoDataView: StephenCommonNoDataView?, private var isNeedCheckLoginCode: Boolean = false,
                                                        private val globalBottomBtnClickListener: View.OnClickListener? = null) {
    companion object {
        const val NoDataDefaultCode = -888 //系统定死返回
        const val NoDataForNotwork = -1 //系统定死返回
        const val NoDataForServer = -2 //系统定死返回
        const val NoDataForReqFail = -5 //系统定死返回
        const val NoDataForNoLogin = 4097 //服务器返回code
        const val NoDataForLoginTimeOut = 4057 //服务器返回code
        const val NoDataForNeedVip = 407 //服务器返回code
    }

    private fun commonNoDataViewCheck(serverCode: Int, serverNotData: Boolean? = false): Int { //NoDataForNotwork没网提示,NoDataForLogin没登录提示,NoDataForServer服务器没数据提示
        if (!isConnected(activity)) return NoDataForNotwork
        var rspCode = serverCode
        if ((if (isNeedCheckLoginCode) rspCode == NoDataForNoLogin || rspCode == NoDataForLoginTimeOut else false) || rspCode == NoDataForReqFail || rspCode == NoDataForNeedVip) { //重要的code优先判断
            return rspCode
        } else {
            if (null != serverNotData && serverNotData) rspCode = NoDataForServer
        }
        return rspCode
    }

    @JvmOverloads
    fun commonNoDataViewShow(serverCode: Int = NoDataDefaultCode, serverNotData: Boolean? = false, hintMsg: String? = null, hint2Msg: String? = null, hintPicId: Int? = null,
                             isEmptyResponseClick: Boolean = false, bottomBtnTxt: String? = null, onceBottomBtnClickListener: View.OnClickListener? = null): Int {
        if (null == stephenCommonNoDataView) return NoDataDefaultCode
        val rspCode = commonNoDataViewCheck(serverCode, serverNotData)
        stephenCommonNoDataView.removeCenterTextBottomBtn(-1)
        val bottomBtnClickListener: View.OnClickListener? = onceBottomBtnClickListener ?: globalBottomBtnClickListener
        return when (rspCode) {
            NoDataForNotwork -> {
                stephenCommonNoDataView.run {
                    setNoDataViewShow(isEmptyResponseClick, if (hintMsg.isNullOrBlank()) activity.getString(R.string.net_work_connect_fail) else hintMsg)
                    setCenterText2ViewStr(if (hint2Msg.isNullOrBlank()) activity.getString(R.string.request_data_refresh) else hint2Msg, 7)
                    setCenterTextTopHintImg(ResourcesCompat.getDrawable(activity.resources, R.drawable.pic_empty_no_net, null), 110, 110, 20)

                    if(null != bottomBtnClickListener)setCenterTextBottomBtn(if (bottomBtnTxt.isNullOrBlank()) activity.getString(R.string.re_load_page) else bottomBtnTxt, Color.parseColor("#627CFE"),
                        ResourcesCompat.getDrawable(activity.resources, R.drawable.no_data_reload_btn_selector, null), 100, 34, 22, View.OnClickListener {
                            //commonNoDataViewHide()
                            bottomBtnClickListener?.onClick(it)
                        })
                }
                rspCode
            }
            NoDataForServer -> {
                stephenCommonNoDataView.run {
                    setNoDataViewShow(isEmptyResponseClick, if (hintMsg.isNullOrBlank()) activity.getString(R.string.no_data_hint) else hintMsg)
                    setCenterText2ViewStr(if (hint2Msg.isNullOrBlank()) activity.getString(R.string.request_data_failure) else hint2Msg, 7)
                    setCenterTextTopHintImg(ResourcesCompat.getDrawable(activity.resources, hintPicId ?: R.drawable.pic_empty_no_data, null), 110, 110, 20)

                    if(null != bottomBtnClickListener)setCenterTextBottomBtn(if (bottomBtnTxt.isNullOrBlank()) activity.getString(R.string.re_load_data) else bottomBtnTxt, Color.parseColor("#627CFE"),
                        ResourcesCompat.getDrawable(activity.resources, R.drawable.no_data_reload_btn_selector, null), 100, 34, 22, View.OnClickListener {
                            //commonNoDataViewHide()
                            bottomBtnClickListener?.onClick(it)
                        })
                }
                rspCode
            }
            NoDataForReqFail -> {
                stephenCommonNoDataView.run{
                    setNoDataViewShow(isEmptyResponseClick, if (hintMsg.isNullOrBlank()) activity.getString(R.string.request_data_hidden) else hintMsg)
                    setCenterText2ViewStr(if (hint2Msg.isNullOrBlank()) activity.getString(R.string.request_data_failure) else hint2Msg, 7)
                    setCenterTextTopHintImg(ResourcesCompat.getDrawable(activity.resources, hintPicId ?: R.drawable.pic_empty_no_data, null), 110, 110, 20)

                    if(null != bottomBtnClickListener)setCenterTextBottomBtn(if (bottomBtnTxt.isNullOrBlank()) activity.getString(R.string.re_load_data) else bottomBtnTxt, Color.parseColor("#627CFE"),
                        ResourcesCompat.getDrawable(activity.resources, R.drawable.no_data_reload_btn_selector, null), 100, 34, 22, View.OnClickListener {
                            //commonNoDataViewHide()
                            bottomBtnClickListener?.onClick(it)
                        })
                }
                rspCode
            }
            NoDataForNoLogin -> {
                stephenCommonNoDataView.setNoDataViewShow(isEmptyResponseClick, (if (hintMsg.isNullOrBlank()) activity.getString(R.string.not_login_hint) else hintMsg) + if (isEmptyResponseClick) "" else "")
                rspCode
            }
            NoDataForLoginTimeOut -> {
                stephenCommonNoDataView.setNoDataViewShow(isEmptyResponseClick, (if (hintMsg.isNullOrBlank()) activity.getString(R.string.token_expires_hint) else hintMsg) + if (isEmptyResponseClick) "" else "")
                rspCode
            }
            else -> {//DefaultCode会默认关闭
                commonNoDataViewHide()
                rspCode
            }
        }
    }

    fun commonNoDataViewHide(){
        stephenCommonNoDataView?.setNoDataViewHide()
    }
    
    //判断网络是否连接
    private fun isConnected(context: Context): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (null != connectivity) {
            val info = connectivity.activeNetworkInfo
            if (null != info && info.isConnected) if (info.state == NetworkInfo.State.CONNECTED) return true
        } //end of if
        return false
    }
}