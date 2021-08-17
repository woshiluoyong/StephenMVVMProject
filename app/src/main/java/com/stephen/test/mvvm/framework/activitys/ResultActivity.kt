package com.stephen.test.mvvm.framework.activitys

import android.os.Bundle
import androidx.lifecycle.Observer
import com.android.puy.mvvm.base.XActivity
import com.stephen.test.mvvm.framework.R
import com.stephen.test.mvvm.framework.activitys.viewmodel.ResultViewModel
import com.stephen.test.mvvm.framework.beans.ResultBean
import com.stephen.test.mvvm.framework.databinding.ActivityResultBinding
import com.stephen.test.mvvm.framework.utils.Constant

class ResultActivity : XActivity<ResultViewModel, ActivityResultBinding>(){
    override fun getLayoutId(): Int = R.layout.activity_result

    override fun initViewMode(): ResultViewModel = getViewModel(ResultViewModel::class.java)

    override fun initView(savedInstanceState: Bundle?) {
        initStatusBar(statusBarDark = false)
    }

    override fun initData() {
        val resultBean = intent.getParcelableExtra<ResultBean>(Constant.ParamBase) ?: return
        mBinding.resultBean = resultBean
        mBinding.resultViewModel = mViewModel
        mViewModel.descStr.value = resultBean.getDescStr(this)
        mViewModel.closeActionLiveData.observe(this, Observer {
            if (it) backToPrevActivity()
        })
    }

    override fun backCheckOperation(): Boolean {
        mViewModel.closeActionLiveData.value = true
        return false
    }
}