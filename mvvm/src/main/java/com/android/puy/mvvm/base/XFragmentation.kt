package com.android.puy.mvvm.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gyf.immersionbar.ImmersionBar
import com.tbruyelle.rxpermissions2.RxPermissions
import me.yokeyword.fragmentation.SupportFragment
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.ParameterizedType

abstract class XFragmentation<VM :XViewModel, DB :ViewDataBinding> : SupportFragment() {
    protected lateinit var mViewModel: VM
    protected lateinit var mBinding: DB
    lateinit var context: Activity
    private lateinit var mImmersionBar: ImmersionBar
    private var rxPermissions: RxPermissions? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return if(getLayoutId() > 0){
            val type = javaClass.genericSuperclass
            if (type is ParameterizedType) {//DataBinding
                val cls = type.actualTypeArguments[1] as Class<*>
                ViewDataBinding::class.java.isAssignableFrom(cls) && cls != ViewDataBinding::class.java
                mBinding = DataBindingUtil.inflate(layoutInflater, getLayoutId(), null, false)
                mBinding?.lifecycleOwner = this
                mBinding.root
            } else throw IllegalArgumentException("Generic DataBinding error")
        }else{
            getLayoutView()
        }
        return null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = initViewMode()
        lifecycle.addObserver(mViewModel)
        initView(savedInstanceState)
        if(useEventBus())EventBus.getDefault().register(this)
        initData()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            this.context = context
        }
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        //???onSupportVisible???????????????
        initImmersionBar()
    }

    open fun initImmersionBar() {}

    override fun onDestroyView() {
        super.onDestroyView()
        if(useEventBus())EventBus.getDefault().unregister(this)
    }

    //??????
    abstract fun getLayoutId(): Int

    //??????View
    open fun getLayoutView(): View? = null

    //?????????viewModel
    abstract fun initViewMode(): VM

    //?????????UI??????
    abstract fun initView(savedInstanceState: Bundle?)

    //?????????????????????
    abstract fun initData()

    //??????viewModel
    protected fun <VM : ViewModel?> getViewModel(viewModelClass: Class<VM>): VM {
        return ViewModelProvider(this)[viewModelClass]
    }

    //??????????????????
    open fun initStatusBar(id: Int) {
        mImmersionBar = ImmersionBar.with(this)
        mImmersionBar.titleBar(id)
        mImmersionBar.keyboardEnable(true)
        mImmersionBar.init()
    }

    open fun initStatusBar(id: Int, statusBarDark: Boolean) {
        mImmersionBar = ImmersionBar.with(this)
        mImmersionBar.titleBar(id)
        mImmersionBar.statusBarDarkFont(statusBarDark)
        mImmersionBar.keyboardEnable(true)
        mImmersionBar.init()
    }

    open fun initStatusBar(id: Int, statusBarDark: Boolean, navigationBarColor: Int) {
        mImmersionBar = ImmersionBar.with(this)
        mImmersionBar.titleBar(id)
        mImmersionBar.statusBarDarkFont(statusBarDark)
        mImmersionBar.keyboardEnable(true)
        mImmersionBar.navigationBarColor(navigationBarColor)
        mImmersionBar.init()
    }

    //????????????
    open fun getRxPermissions(): RxPermissions {
        rxPermissions = RxPermissions(this)
        return rxPermissions!!
    }

    //????????????eventBus
    open fun useEventBus(): Boolean = false
}