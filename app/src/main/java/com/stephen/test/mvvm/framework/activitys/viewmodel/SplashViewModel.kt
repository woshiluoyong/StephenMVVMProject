package com.stephen.test.mvvm.framework.activitys.viewmodel

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import com.android.puy.mvvm.base.XViewModel

class SplashViewModel(application: Application) : XViewModel(application) {
    val enterActivityCls: Class<*>? = null//设置null就关闭调试进入,设置具体界面class就调试进入具体界面
    val enterMainPage = MutableLiveData<Boolean>(false)

    fun initData() {
        if(null != enterActivityCls){
            enterMainPage.value = true
            return
        }//end of if
        timer.start()
    }

    private val timer by lazy {
        object : CountDownTimer(4000L, 500) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                cancel()
                enterMainPage.value = true
            }
        }
    }
}