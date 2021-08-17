package com.stephen.test.mvvm.framework.activitys.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.android.puy.mvvm.base.XViewModel

class ResultViewModel(application: Application) : XViewModel(application) {

    val actions = Actions(this)
    val closeActionLiveData = MutableLiveData(false)
    val descStr = MutableLiveData<String>()

    class Actions(resultViewModel: ResultViewModel) {
        val closeAction = {
            resultViewModel.closeActionLiveData.value = true
        }
    }
}