package com.qeeyou.accelerator.overseas.overwall.activitys

import android.os.Bundle
import androidx.lifecycle.Observer
import com.android.puy.mvvm.base.XActivity
import com.qeeyou.accelerator.overseas.overwall.R
import com.qeeyou.accelerator.overseas.overwall.activitys.viewmodel.ResultViewModel
import com.qeeyou.accelerator.overseas.overwall.beans.ResultBean
import com.qeeyou.accelerator.overseas.overwall.databinding.ActivityResultBinding
import com.qeeyou.accelerator.overseas.overwall.utils.Constant

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