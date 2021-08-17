package com.stephen.test.mvvm.framework.activitys

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.Keep
import androidx.lifecycle.Observer
import com.android.puy.mvvm.base.XActivity
import com.android.puy.mvvm.router.Router
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupPosition
import com.orhanobut.hawk.Hawk
import com.stephen.test.mvvm.framework.R
import com.stephen.test.mvvm.framework.activitys.viewmodel.MainViewModel
import com.stephen.test.mvvm.framework.beans.ResultBean
import com.stephen.test.mvvm.framework.databinding.ActivityMainBinding
import com.stephen.test.mvvm.framework.entitys.MsgOperationEvent
import com.stephen.test.mvvm.framework.utils.*
import com.stephen.test.mvvm.framework.widgets.ConfirmDialog
import com.stephen.test.mvvm.framework.widgets.MainConnectProgressView
import com.stephen.test.mvvm.framework.widgets.MainDrawer
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.stephen.test.mvvm.framework.utils.ViewShapeBgUtil
import java.util.*

class MainActivity : XActivity<MainViewModel, ActivityMainBinding>() {
    private val drawerView by lazy {
        MainDrawer(this)
    }

    override fun needExitActivity(): Boolean = true

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun useEventBus(): Boolean = true

    override fun initViewMode(): MainViewModel = getViewModel(MainViewModel::class.java)

    override fun initView(savedInstanceState: Bundle?) {
        //ToolUtils.instance.debugPrintln("=================MainActivity=====>$isTaskRoot")
        initStatusBar(null, true, null, Color.parseColor("#F7F7FC"), true)
        connectProgressV.setOnClickListener {
            onConnectOperation(it)
        }
    }

    override fun initData() {
        mBinding.mainViewModel = mViewModel
        with(mViewModel) {
            initData()
            userCurAccNode.observe(this@MainActivity, Observer {
                lineNameT.text = {null != it}.doJudge({it!!.name}, {resources.getString(R.string.auto_select)})
                if(null != it){
                    Glide.with(this@MainActivity).load(it.logo_url).error(R.drawable.pic_placeholder_error_shape).placeholder(R.drawable.pic_placeholder_error_shape).into(countryImgV)
                }else{
                    Glide.with(this@MainActivity).load(R.drawable.icon_auto_select).error(R.drawable.pic_placeholder_error_shape).placeholder(R.drawable.pic_placeholder_error_shape).into(countryImgV)
                }
            })
            goResultLiveData.observe(this@MainActivity, Observer {
                when (it) {
                    ResultBean.STATE_CONNECTED -> {
                        connectProgressV.state = MainConnectProgressView.STATE_CONNECTED
                        changeMainUiEnabled(100)
                    }
                    ResultBean.STATE_CONNECT_FAILED, ResultBean.STATE_DISCONNECTED -> {
                        ToolUtils.instance.showLongHintInfo(this@MainActivity, this@MainActivity.getString(R.string.vpn_disconnected))
                        connectProgressV.state = MainConnectProgressView.STATE_NULL
                        changeMainUiEnabled(0)
                    }
                }//end of when
                Router.newIntent(this@MainActivity).putParcelable(Constant.ParamBase, ResultBean(it, lineNameT.text.toString(), connectProgressV.msg))
                    .requestCode(Constant.ReqCode_AccResult).to(ResultActivity::class.java).launch()
            })
            openIpViewLiveData.observe(this@MainActivity, Observer {
                if (it > 0) Router.newIntent(this@MainActivity).to(IpViewActivity::class.java).launch()
            })
            curMode.observe(this@MainActivity, Observer {
                Hawk.put(Constant.KEY_VPN_PROTOCOL_MODE, it)
            })
            BusinessUtil.instance.getCurEnvIpAddress(true)
        }
    }

    //禁用与否相关的切换
    private fun changeMainUiEnabled(connectProgress: Int) {
        lineNameT.setTextColor(Color.parseColor({ 0 == connectProgress || connectProgress == 100 }.doJudge({ "#323438" }, { "#7f323438" })))
        when (connectProgress) {
            0 -> {
                vpnOperationBtn.text = resources.getString(R.string.acc_btn_connect)
                vpnOperationBtn.setTextColor(Color.WHITE)
                ViewShapeBgUtil.setShapeBg(this, vpnOperationBtn, radiusCorner = 12f, colorStart = Color.parseColor("#4870FF"),
                    colorCenter = Color.parseColor("#6785FF"), colorEnd = Color.parseColor("#869BFF"))
            }
            else -> {
                vpnOperationBtn.text = resources.getString({ 100 == connectProgress }.doJudge({ R.string.acc_btn_disconnect }, { R.string.acc_btn_connecting }))
                vpnOperationBtn.setTextColor(Color.parseColor("#637AFF"))
                ViewShapeBgUtil.setShapeBg(this, vpnOperationBtn, radiusCorner = 12f, strokeWidth = 1, strokeColor = Color.parseColor("#637AFF"), colorFill = Color.TRANSPARENT)
            }
        }
        menuImgV.tag = connectProgress
        lineAreaRy.tag = connectProgress
        vpnOperationBtn.tag = connectProgress
        connectProgressV.tag = connectProgress
    }

    //操作方法
    fun onConnectOperation(v: View?) {
        if (ToolUtils.instance.isFastClick() || connectProgressV.state == MainConnectProgressView.STATE_CONNECTING) return
        val progress = { null != v?.tag && v?.tag is Int }.doJudge({ v?.tag as Int }, { 0 })
        ToolUtils.instance.debugPrintln("====onConnectOperation========>progress:$progress")
        when (progress) {
            0 -> startAccelerateFun()
            100 -> stopAccelerateFun(true)
        }
    }

    fun goToLineList(v: View) {
        if (ToolUtils.instance.isFastClick() || connectProgressV.state == MainConnectProgressView.STATE_CONNECTING) return
        val progress = { null != v?.tag && v?.tag is Int }.doJudge({ v?.tag as Int }, { 0 })
        if (0 == progress || 100 == progress) {
            Router.newIntent(this).to(LineListActivity::class.java).launch()
        }//end of if
    }

    fun openDrawer(v: View) {
        if (ToolUtils.instance.isFastClick() || connectProgressV.state == MainConnectProgressView.STATE_CONNECTING) return
        val progress = { null != v?.tag && v?.tag is Int }.doJudge({ v?.tag as Int }, { 0 })
        if (0 == progress || 100 == progress) {
            val popupPosition = if (BusinessUtil.instance.isRtl(this)) {
                PopupPosition.Right
            } else {
                PopupPosition.Left
            }
            XPopup.Builder(this).popupPosition(popupPosition).dismissOnTouchOutside(true).asCustom(drawerView).show()
        }//end of if
    }

    fun openIpView(v: View) {
        mViewModel.openIpViewLiveData.value = mViewModel.openIpViewLiveData.value?.plus(1)
    }

    //开始加速
    private fun startAccelerateFun() {
        connectProgressV.state = MainConnectProgressView.STATE_CONNECTING
        ToolUtils.instance.delayExecute(2000){
            mViewModel.goResultLiveData.value = ResultBean.STATE_CONNECTED
        }
    }

    //停止加速
    private fun stopAccelerateFun(isCheckConfirm: Boolean) {
        if(isCheckConfirm){
            XPopup.Builder(this).dismissOnBackPressed(false).dismissOnTouchOutside(false)
                .asCustom(ConfirmDialog(getString(R.string.acc_close_hint_info), null, true, {
                    if(it){
                        connectProgressV.state = MainConnectProgressView.STATE_CONNECTING
                        ToolUtils.instance.delayExecute(2000){
                            mViewModel.goResultLiveData.value = ResultBean.STATE_DISCONNECTED
                        }
                    }//end of if
                }, activity = this, descColor = Color.parseColor("#323233"))).show()
        }else{
            mViewModel.goResultLiveData.value = ResultBean.STATE_DISCONNECTED
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constant.ReqCode_AccResult -> if (resultCode == RESULT_OK) startAccelerateFun()
        }//end of when
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAccelerateFun(false)
    }

    @Keep
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOpMsgEvent(msgEvent: MsgOperationEvent) {
        when (msgEvent.flag) {
            MsgOperationEvent.MsgSwitchAccModel -> {}
            MsgOperationEvent.MsgAccIntervalData -> {}
        }
    }

    /**
     * 当配置改变时，比如切换系统语言，Activity会重建，但ViewModel会保存当前数据状态，因Main界面部分ui逻辑没有和ViewModel数据绑定，导致ui异常
     * 解决：此方法返回false，Activity在destroy时，ViewModel会执行clear方法，清空全部数据
     */
    override fun isChangingConfigurations(): Boolean {
        return false
    }
}

