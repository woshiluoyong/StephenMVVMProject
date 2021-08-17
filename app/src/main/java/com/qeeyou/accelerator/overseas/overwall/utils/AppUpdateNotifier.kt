package com.qeeyou.accelerator.overseas.overwall.utils

import android.app.Activity
import android.app.Dialog
import com.lxj.xpopup.XPopup
import com.qeeyou.accelerator.overseas.overwall.widgets.CenterDialogForAppUpdate
import org.lzh.framework.updatepluginlib.base.CheckNotifier

//自定义升级弹窗
class AppUpdateNotifier : CheckNotifier() {
    override fun create(activity: Activity): Dialog? {
        //val isCanClose = {BusinessUtil.instance.isDebug()}.doJudge({true}, {{update.isForced}.doJudge({false}, {true})})
        XPopup.Builder(activity).dismissOnBackPressed(!update.isForced).dismissOnTouchOutside(!update.isForced).autoFocusEditText(false)
            .autoOpenSoftInput(false).asCustom(CenterDialogForAppUpdate(activity, update)).show()
        return null
    }
}