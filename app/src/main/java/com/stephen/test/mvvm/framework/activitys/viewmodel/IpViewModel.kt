package com.stephen.test.mvvm.framework.activitys.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.android.puy.mvvm.base.XViewModel
import com.orhanobut.hawk.Hawk
import com.stephen.test.mvvm.framework.beans.IpViewBean
import com.stephen.test.mvvm.framework.utils.Constant
import com.stephen.test.mvvm.framework.utils.doJudge

class IpViewModel(application: Application) : XViewModel(application) {
    var ipBeforeLiveData = MutableLiveData("")
    var ipAfterLiveData = MutableLiveData("")

    fun initData(){
        ipBeforeLiveData.value = "${{Hawk.get<IpViewBean>(Constant.ACC_BEFORE_IP_INFO)?.ip.isNullOrBlank()}.doJudge({""}
            ,{Hawk.get<IpViewBean>(Constant.ACC_BEFORE_IP_INFO)?.ip})}  |  ${{Hawk.get<IpViewBean>(Constant.ACC_BEFORE_IP_INFO)?.country.isNullOrBlank()}
            .doJudge({""},{Hawk.get<IpViewBean>(Constant.ACC_BEFORE_IP_INFO)?.country})}"
        ipAfterLiveData.value = "${{Hawk.get<IpViewBean>(Constant.ACC_AFTER_IP_INFO)?.ip.isNullOrBlank()}.doJudge({""}
            ,{Hawk.get<IpViewBean>(Constant.ACC_AFTER_IP_INFO)?.ip})}  |  ${{Hawk.get<IpViewBean>(Constant.ACC_AFTER_IP_INFO)?.country.isNullOrBlank()}
            .doJudge({""},{Hawk.get<IpViewBean>(Constant.ACC_AFTER_IP_INFO)?.country})}"
    }
}