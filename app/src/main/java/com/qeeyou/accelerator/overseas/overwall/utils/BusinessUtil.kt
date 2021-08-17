package com.qeeyou.accelerator.overseas.overwall.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.orhanobut.hawk.Hawk
import com.qeeyou.accelerator.overseas.overwall.BuildConfig
import com.qeeyou.accelerator.overseas.overwall.MyApplication
import com.qeeyou.accelerator.overseas.overwall.beans.IpViewBean
import com.qeeyou.accelerator.overseas.overwall.net.ApiRequestMethod
import com.qeeyou.accelerator.overseas.overwall.net.RetrofitHelper
import org.lzh.framework.updatepluginlib.UpdateConfig
import org.lzh.framework.updatepluginlib.base.FileChecker
import org.lzh.framework.updatepluginlib.base.UpdateParser
import org.lzh.framework.updatepluginlib.base.UpdateStrategy
import org.lzh.framework.updatepluginlib.model.CheckEntity
import org.lzh.framework.updatepluginlib.model.Update
import java.util.*


//和业务相关的工具方法,比如判断游客登录等
class BusinessUtil {
    private var loadingDialog: BasePopupView? = null

    private fun readResolve(): Any {//防止单例对象在反序列化时重新生成对象
        return instance
    }

    companion object {
        @JvmStatic
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BusinessUtil()
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    fun showLoading(loadingMsg: String?="Loading...", isBackKeyCancel: Boolean = false, onDismissListener: DialogInterface.OnDismissListener? = null){
        closeLoading()
        try{
            loadingDialog = XPopup.Builder(MyApplication?.currentActivity).setPopupCallback(object : SimpleCallback(){
                override fun onDismiss(popupView: BasePopupView?) {
                    super.onDismiss(popupView)
                    onDismissListener?.onDismiss(popupView?.dialog)
                }
            }).dismissOnBackPressed(isBackKeyCancel).dismissOnTouchOutside(false).asLoading(loadingMsg).show()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun closeLoading(){
        try {
            loadingDialog?.dismiss()
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            loadingDialog = null
        }
    }

    //生成接口所需trance id
    fun getTraceId(groupId: String? = null, retryCount: Int = 1): String {
        val strBuilder = StringBuilder().apply {
            append(ToolUtils.instance.getFormattedDate(System.currentTimeMillis(), "yyyyMMddHHmmss"))
            append("-")
            append(DeviceUuidFactory.getInstance().getDeviceUuid(MyApplication.app.applicationContext).toString().replace("-", ""))
            append("-")
            if (groupId.isNullOrEmpty()) {
                append(getRandomGroupId())
            } else {
                append(groupId)
            }
            append("-")
            append(ToolUtils.instance.getRandomNumString(4))
            append("-")
            append("0000")
            append("-")
            append(ToolUtils.instance.numToString(retryCount, 2, '0'))
        }
        return strBuilder.toString()
    }

    private fun getRandomGroupId(){
        UUID.randomUUID().toString().replace("-", "").substring(0, 8)
    }

    //获取接口所需自定义User-Agent
    fun getUa(): String {
        val context = MyApplication.app.applicationContext
        val strBuilder = StringBuilder().apply {
            append(Build.MODEL)
            append("/")
            append("android")
            append("/")
            append(Build.VERSION.RELEASE)
            append("/")
            append("Google Play")
            append("/")
            append("Stephen MVVM")
            append("/")
            append(ToolUtils.instance.getAppVersionName(context, context.packageName))
            append("/")
            append(DeviceUuidFactory.getInstance().getDeviceUuid(context))
            append("/")
            append(LanguageManager.instance.checkAutoAndSupport(LanguageManager.instance.getSavedAppLanCode()))
        }
        return strBuilder.toString()
    }

    fun isDebug(): Boolean {
        return BuildConfig.BUILD_TYPE == "debug"
    }

    //获取当前环境ip
    @SuppressLint("CheckResult")
    fun getCurEnvIpAddress(isBeforeFlag: Boolean, callBack: ((ipViewBean: IpViewBean?) -> Unit)? = null){
        val saveKey = {isBeforeFlag}.doJudge({Constant.ACC_BEFORE_IP_INFO}, {Constant.ACC_AFTER_IP_INFO})
        ApiRequestMethod.wrapRequestLoadingObservable(RetrofitHelper.instance.getCurIpAddress(),null)?.subscribe({
            if(it?.ip.isNullOrBlank()){
                Hawk.delete(saveKey)
            }else{
                Hawk.put(saveKey, it)
            }
            callBack?.run { this(it) }
        }, {
            Hawk.delete(saveKey)
            callBack?.run { this(null) }
        })
    }

    //初始化版本更新
    fun initAppUpdateConfig() {
        val checkEntity = CheckEntity()
        checkEntity.url = "${Constant.SERVER_URL}/api/common_bll/v1/appinfo/check_version/"
        checkEntity.headers = mapOf("UA" to getUa())
        UpdateConfig.getConfig().setCheckEntity(checkEntity)
            .setUpdateStrategy(object : UpdateStrategy() {
                override fun isShowUpdateDialog(update: Update?): Boolean = true

                override fun isShowDownloadDialog(): Boolean = true

                override fun isAutoInstall(): Boolean = false
            }).setCheckNotifier(AppUpdateNotifier()).setFileChecker(object : FileChecker() {
                override fun onCheckBeforeDownload(): Boolean = false

                override fun onCheckBeforeInstall() {}
            }).updateParser = object : UpdateParser() {
            override fun parse(response: String?): Update {
                ToolUtils.instance.debugPrintln("=====UpdatePluginLog===Json==>$response")
                val update = Update()
                try {
                    var jsonObject = ToolUtils.instance.fromJsonToObj("{\"data\":{\"update_log\": \"本地测试升级弹窗Json\", \"version\":\"1.1.1\"}}")//response)//
                    if (null != jsonObject && jsonObject.has("data")) {
                        jsonObject = jsonObject.getJSONObject("data")
                        if (jsonObject.has("update_log") && jsonObject.has("version")) {//有新版本
                            update.updateContent = jsonObject.getString("update_log");
                            update.versionName = jsonObject.getString("version")
                            update.versionCode = ToolUtils.instance.getAppVersionCode(MyApplication.app.applicationContext, MyApplication.app.packageName).toInt() + 1//因为服务器是根据versionName字符串判定的
                            if (jsonObject.has("force_update")) update.isForced = jsonObject.getBoolean("force_update")
                        }//end of if
                    }//end of if
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return update
            }
        }
    }

    //判断是否是RTL布局
    fun isRtl(context: Context): Boolean {
        return ToolUtils.instance.isRtl(context, LanguageUtils.getLocaleByCode(LanguageManager.instance.checkAutoAndSupport(LanguageManager.instance.getSavedAppLanCode())))
    }
}