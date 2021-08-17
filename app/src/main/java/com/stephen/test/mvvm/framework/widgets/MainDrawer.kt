package com.stephen.test.mvvm.framework.widgets

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.android.puy.mvvm.router.Router
import com.lxj.xpopup.core.DrawerPopupView
import com.stephen.test.mvvm.framework.R
import com.stephen.test.mvvm.framework.activitys.AboutActivity
import com.stephen.test.mvvm.framework.activitys.CommWebActivity
import com.stephen.test.mvvm.framework.activitys.LanguageListActivity
import com.stephen.test.mvvm.framework.utils.Constant
import com.stephen.test.mvvm.framework.utils.ToolUtils
import com.stephen.test.mvvm.framework.utils.singleClick

class MainDrawer(private val activity: Activity) : DrawerPopupView(activity) {

    override fun getImplLayoutId(): Int = R.layout.layout_main_drawer

    private lateinit var layoutList: LinearLayout

    override fun onCreate() {
        super.onCreate()
        layoutList = findViewById(R.id.layoutList)

        addItem(R.mipmap.ic_item_help, activity.getString(R.string.help_center), false) {
            Router.newIntent(activity).putString(Constant.ParamTitle, activity.getString(R.string.help_center)).putBoolean(Constant.ParamUpdateTitle, false)
                .putString(Constant.ParamBase, "https://www.2345.com/?k726386446").to(CommWebActivity::class.java).launch()
            dismiss()
        }

        addItem(R.mipmap.ic_item_share, activity.getString(R.string.item_share)) {
            ToolUtils.instance.callSystemTxtShare(activity, activity.getString(R.string.item_share), activity.getString(R.string.share_content) + "\n${String.format(Constant.BASE_URL_GP_APP, activity.packageName)}")
            dismiss()
        }

        addItem(R.mipmap.ic_item_language, activity.getString(R.string.language_settings)) {
            Router.newIntent(activity).to(LanguageListActivity::class.java).launch()
            dismiss()
        }

        addItem(R.mipmap.ic_item_about, activity.getString(R.string.item_about)) {
            Router.newIntent(activity).to(AboutActivity::class.java).launch()
            dismiss()
        }
    }

    private fun addItem(icon: Int, name: String, addLineView: Boolean = true, action: () -> Unit) {
        if (addLineView) {
            val lineView = View(activity)
            lineView.setBackgroundResource(R.color.main_drawer_item_line)
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ToolUtils.instance.dp2px(activity, 1f))
            val margin = ToolUtils.instance.dp2px(activity, 15f)
            lp.marginStart = margin
            lp.marginEnd = margin
            lineView.layoutParams = lp
            layoutList.addView(lineView)
        }//end of if
        val itemView = LayoutInflater.from(activity).inflate(R.layout.item_main_drawer, null, false)
        val iconView = itemView.findViewById<ImageView>(R.id.imgIcon)
        val nameView = itemView.findViewById<TextView>(R.id.textName)
        iconView.setImageResource(icon)
        nameView.text = name
        itemView.singleClick {
            action()
        }
        layoutList.addView(itemView)
    }
}