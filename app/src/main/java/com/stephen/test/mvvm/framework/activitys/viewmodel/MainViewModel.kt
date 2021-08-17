package com.stephen.test.mvvm.framework.activitys.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.android.puy.mvvm.base.XViewModel
import com.lxj.xpopup.XPopup
import com.orhanobut.hawk.Hawk
import com.stephen.test.mvvm.framework.MyApplication
import com.stephen.test.mvvm.framework.beans.MainAccInfoBean
import com.stephen.test.mvvm.framework.utils.*
import com.stephen.test.mvvm.framework.widgets.ConfirmDialog
import org.lzh.framework.updatepluginlib.UpdateBuilder
import com.stephen.test.mvvm.framework.R

class MainViewModel(application: Application) : XViewModel(application) {
    val userCurAccNode = MutableLiveData<MainAccInfoBean.Data.Node?>()
    val goResultLiveData = MutableLiveData<Int>()
    val openIpViewLiveData = MutableLiveData(0)
    //当前连接协议模式
    val curMode = MutableLiveData<Int>(Hawk.get(Constant.KEY_VPN_PROTOCOL_MODE, Constant.MODE_AUTO))
    //是否正在连接中或者断开连接中
    val isConnectingOrDisconnecting = MutableLiveData(false)

    fun initData() {
        BusinessUtil.instance.initAppUpdateConfig()
        ToolUtils.instance.delayExecute(500) {
            UpdateBuilder.create().check()
        }
    }

    fun getOnClickListener(mode: Int): View.OnClickListener {
        return SwitchModeOnClickListener(this, mode)
    }

    class SwitchModeOnClickListener(private val mainViewModel: MainViewModel, private val mode: Int) : View.OnClickListener {
        override fun onClick(v: View?) {
            if (mainViewModel.curMode.value == mode) return
            val curActivity = MyApplication.curMainActivity
            if (curActivity != null) {
                XPopup.Builder(curActivity).dismissOnTouchOutside(false).asCustom(ConfirmDialog(title = curActivity.getString(R.string.switch_mode), desc = null, btnCallBack = {
                    if (it) mainViewModel.curMode.value = mode
                }, activity = curActivity, showCancelBtn = true)).show()
            } else {
                mainViewModel.curMode.value = mode
            }
        }
    }
}