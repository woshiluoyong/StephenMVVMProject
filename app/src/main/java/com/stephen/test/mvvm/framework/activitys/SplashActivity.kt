package com.stephen.test.mvvm.framework.activitys

import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.lifecycle.Observer
import com.android.puy.mvvm.base.XActivity
import com.android.puy.mvvm.router.Router
import com.stephen.test.mvvm.framework.BuildConfig
import com.stephen.test.mvvm.framework.R
import com.stephen.test.mvvm.framework.activitys.viewmodel.SplashViewModel
import com.stephen.test.mvvm.framework.databinding.ActivitySplashBinding
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : XActivity<SplashViewModel, ActivitySplashBinding>() {

    override fun getLayoutId(): Int = R.layout.activity_splash

    override fun initViewMode() = getViewModel(SplashViewModel::class.java)

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.loadingViewModel = mViewModel
        initStatusBar(statusBarDark = true)
        showStrT.text = Html.fromHtml("${getString(R.string.stephen)} <font color='#637AFF'>${getString(R.string.mvvm)}</font>")
    }

    override fun initData() {
        with(mViewModel) {
            initData()
            enterMainPage.observe(this@SplashActivity, Observer {
                if (it) onMainEnterClick(null)
            })
        }
    }

    fun onMainEnterClick(v: View?) {//布局上点击也调用到这个方法，调试模式和设置了调试类才跳过闪屏页延时
        Router.newIntent(this@SplashActivity).to(if(BuildConfig.DEBUG && null != mViewModel.enterActivityCls) mViewModel.enterActivityCls else MainActivity::class.java)
            .launch(!(BuildConfig.DEBUG && null != mViewModel.enterActivityCls))
    }

    override fun backCheckOperation(): Boolean = false
}