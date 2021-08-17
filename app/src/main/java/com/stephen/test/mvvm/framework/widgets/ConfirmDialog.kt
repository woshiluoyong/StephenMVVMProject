package com.stephen.test.mvvm.framework.widgets

import android.app.Activity
import android.widget.TextView
import com.cy.necessaryview.shapeview.RecShapeTextView
import com.lxj.xpopup.core.CenterPopupView
import com.stephen.test.mvvm.framework.R
import com.stephen.test.mvvm.framework.utils.ToolUtils
import com.stephen.test.mvvm.framework.utils.singleClick

class ConfirmDialog(
    private val title: String?, private val desc: String?, private val showCancelBtn: Boolean, private val btnCallBack: (getOk: Boolean) -> Unit,
    private val activity: Activity, private val cancelTxt: String? = null, private val confirmTxt: String? = null,
    private val titleColor: Int? = null, private val descColor: Int? = null) : CenterPopupView(activity) {

    override fun getImplLayoutId(): Int = R.layout.view_dialog_confirm

    private lateinit var btnCancel: RecShapeTextView
    private lateinit var btnConfirm: RecShapeTextView
    private lateinit var titleView: TextView
    private lateinit var descView: TextView

    override fun onCreate() {
        btnCancel = findViewById(R.id.btnCancel)
        btnConfirm = findViewById(R.id.btnConfirm)
        titleView = findViewById(R.id.textTitle)
        descView = findViewById(R.id.textDesc)

        if (!cancelTxt.isNullOrBlank()) btnCancel.text = cancelTxt
        if (!confirmTxt.isNullOrBlank()) btnConfirm.text = confirmTxt

        if (title.isNullOrEmpty()) {
            ToolUtils.instance.setViewVisibility(false, titleView)
        } else {
            titleView.text = title
            if (null != titleColor) titleView.setTextColor(titleColor)
        }
        if (desc.isNullOrEmpty()) {
            ToolUtils.instance.setViewVisibility(false, descView)
        } else {
            descView.text = desc
            if (null != descColor) descView.setTextColor(descColor)
        }

        ToolUtils.instance.setViewVisibility(showCancelBtn, btnCancel)

        btnConfirm.singleClick {
            btnCallBack(true)
            dismiss()
        }
        btnCancel.singleClick {
            btnCallBack(false)
            dismiss()
        }
    }
}