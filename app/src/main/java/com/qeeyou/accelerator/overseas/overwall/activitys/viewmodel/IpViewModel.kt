package com.qeeyou.accelerator.overseas.overwall.activitys.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.android.puy.mvvm.base.XViewModel
import com.orhanobut.hawk.Hawk
import com.qeeyou.accelerator.overseas.overwall.beans.IpViewBean
import com.qeeyou.accelerator.overseas.overwall.utils.Constant
import com.qeeyou.accelerator.overseas.overwall.utils.doJudge

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