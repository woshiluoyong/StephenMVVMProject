package com.qeeyou.accelerator.overseas.overwall.widgets

import android.app.Activity
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import com.android.puy.mvvm.utils.QMUIViewHelper
import com.lxj.xpopup.core.CenterPopupView
import com.qeeyou.accelerator.overseas.overwall.R
import com.qeeyou.accelerator.overseas.overwall.utils.Constant
import com.qeeyou.accelerator.overseas.overwall.utils.MarketUtils
import com.qeeyou.accelerator.overseas.overwall.utils.ToolUtils
import com.qeeyou.accelerator.overseas.overwall.utils.singleClick
import org.lzh.framework.updatepluginlib.model.Update

//升级弹窗
class CenterDialogForAppUpdate(val context: Activity, private val update: Update): CenterPopupView(context) {

    override fun getImplLayoutId(): Int = R.layout.dialog_app_update

    override fun onCreate() {
        super.onCreate()

        val updateDescT = findViewById<TextView>(R.id.updateDescT)
        updateDescT.movementMethod = ScrollingMovementMethod.getInstance()
        updateDescT.text = update.updateContent

        findViewById<TextView>(R.id.updateBtn).singleClick {
            if (!MarketUtils.toGooglePlayMarket(context, context.packageName) && !ToolUtils.instance.openUrlExternalBrowser(context, String.format(Constant.BASE_URL_GP_APP, context.packageName))) {
                ToolUtils.instance.showShortHintInfo(context, context.resources.getString(R.string.app_update_jump_fail))
            }//end of if
        }

        val closeImgV = findViewById<View>(R.id.closeImgV)
        QMUIViewHelper.expendTouchArea(closeImgV, 30)
        ToolUtils.instance.setViewVisibility(!update.isForced, closeImgV)
        closeImgV.singleClick { dismiss() }
    }
}