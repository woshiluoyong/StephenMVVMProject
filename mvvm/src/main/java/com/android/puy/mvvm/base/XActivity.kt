package com.android.puy.mvvm.base

import android.app.Activity
import android.os.Bundle
import android.os.Process
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.puy.mvvm.utils.QMUIKeyboardHelper
import com.gyf.immersionbar.ImmersionBar
import com.tbruyelle.rxpermissions2.RxPermissions
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.ParameterizedType

abstract class XActivity<VM :XViewModel, DB :ViewDataBinding> : AppCompatActivity() {
    protected lateinit var mViewModel: VM
    protected lateinit var mBinding: DB
    lateinit var context: Activity
    protected lateinit var mImmersionBar: ImmersionBar
    private var rxPermissions: RxPermissions? = null
    private var firstKeyTime: Long = 0 //第一次按键时间

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this

        if(getLayoutId() > 0){
            val type = javaClass.genericSuperclass
            if (type is ParameterizedType) {//DataBinding
                val cls = type.actualTypeArguments[1] as Class<*>
                ViewDataBinding::class.java.isAssignableFrom(cls) && cls != ViewDataBinding::class.java
                mBinding = DataBindingUtil.setContentView(this, getLayoutId())
                mBinding?.lifecycleOwner = this
            } else throw IllegalArgumentException("Generic DataBinding error")
        }else{
            setContentView(getLayoutView())
        }

        mViewModel = initViewMode()
        lifecycle.addObserver(mViewModel)
        initView(savedInstanceState)
        if(useEventBus())EventBus.getDefault().register(this)
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(useEventBus())EventBus.getDefault().unregister(this)
    }

    //布局
    abstract fun getLayoutId(): Int

    //布局View
    open fun getLayoutView(): View? = null

    //初始化viewModel
    abstract fun initViewMode(): VM

    //初试化UI相关
    abstract fun initView(savedInstanceState: Bundle?)

    //初始化数据相关
    abstract fun initData()

    //获取viewModel
    protected fun <VM : ViewModel?> getViewModel(viewModelClass: Class<VM>): VM {
        return ViewModelProvider(this)[viewModelClass]
    }

    //沉浸式状态栏
    @JvmOverloads
    open fun initStatusBar(id: Int? = null, statusBarDark: Boolean? = null, navigationBarColor: Int? = null, statusBarColor: Int? = null, fitsSystemWindows: Boolean? = null) {
        mImmersionBar = ImmersionBar.with(this).apply {
            keyboardEnable(true)
            //  解决状态栏和布局重叠问题，可任选下列其一
            //  .titleBar(view)
            //  .titleBarMarginTop(view)
            //  .statusBarView(view)
            //  .fitsSystemWindows(true)    //默认为false，当为true时一定要指定statusBarColor()，不然状态栏为透明色

            fitsSystemWindows?.let { fitsSystemWindows(it) }
            id?.let {  titleBar(id) }
            statusBarDark?.let { statusBarDarkFont(it) }
            navigationBarColor?.let { navigationBarColor(it) }
            statusBarColor?.let { statusBarColorInt(it) }
        }
        mImmersionBar.init()
    }

    //权限请求
    open fun getRxPermissions(): RxPermissions {
        rxPermissions = RxPermissions(this)
        return rxPermissions!!
    }

    //是否使用eventBus
    open fun useEventBus(): Boolean = false

    //是否需要点击空白区域隐藏输入法
    open fun isCheckBlackHideKeyBoard(): Boolean = true //default

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN && isCheckBlackHideKeyBoard()) { //点击空白区域,隐藏输入法
            val view = currentFocus
            if (null != view && QMUIKeyboardHelper.isShouldHideKeyboard(view, ev)) QMUIKeyboardHelper.hideKeyboard(view)
        } //end of if
        return super.dispatchTouchEvent(ev)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> if (backCheckOperation()) {
                if (needExitActivity()) {
                    val secondTime = System.currentTimeMillis()
                    if (secondTime - firstKeyTime > 2000) { //如果两次按键时间间隔大于2秒
                        Toast.makeText(context, customExitHintMsg(), Toast.LENGTH_SHORT).show()
                        firstKeyTime = secondTime //更新firstTime
                        return true
                    } else { //两次按键小于2秒时
                        minimizationProgram()
                    }
                } else {
                    backToPrevActivity()
                }
            } //end of if
        }
        return true
        //return super.onKeyUp(keyCode, event);
    }

    open fun customExitHintMsg(msg: String? = null): String{
        return if(msg.isNullOrBlank()) "Quickly click twice to exit the app" else msg
    }

    //需要直接退出的activity
    open fun needExitActivity(): Boolean {
        return false //(null != activity && (activity instanceof MainActivity));
    }

    //返回上一步
    open fun backToPrevActivity() {
        if (null != context) context.finish()
    }

    //返回时检查操作
    open fun backCheckOperation(): Boolean {
        return true //default
    }

    //菜单左边按键响应
    open fun backBtnClickResponse() {
        if (backCheckOperation()) backToPrevActivity() //default
    }

    //关闭程序之前的操作
    open fun beforeFinishProgram() {}

    //最小化程序
    open fun minimizationProgram() {
        beforeFinishProgram()
        moveTaskToBack(true)
    }

    //kill程序
    open fun killSelfProgram() {
        minimizationProgram()
        System.exit(0);
        Process.killProcess(Process.myPid())
    }
}