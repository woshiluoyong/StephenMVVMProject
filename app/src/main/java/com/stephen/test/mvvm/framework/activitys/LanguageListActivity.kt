package com.stephen.test.mvvm.framework.activitys

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import com.android.puy.mvvm.base.NoViewModel
import com.android.puy.mvvm.base.XActivity
import com.android.puy.mvvm.utils.StephenCommonTopTitleView
import com.cy.necessaryview.shapeview.RecShapeLinearLayout
import com.stephen.test.mvvm.framework.R
import com.stephen.test.mvvm.framework.utils.*

class LanguageListActivity : XActivity<NoViewModel, ViewDataBinding>() {

    private var selectedPosition = -1

    override fun getLayoutId(): Int = -1

    override fun initViewMode(): NoViewModel = getViewModel(NoViewModel::class.java)

    override fun getLayoutView(): View? {
        initStatusBar(statusBarDark = true, statusBarColor = Color.parseColor("#F0F2F5"), fitsSystemWindows = true)
        val titleView = StephenCommonTopTitleView(this)
        titleView.run {
            setTitleBgColor(Color.parseColor("#F0F2F5"))
            setTitleLeftIcon(R.drawable.icon_back_btn, getTitleLeftLp(25, 25, 15))
//            setTitleRightIcon(R.drawable.icon_close_btn, getTitleRightLp(25, 25, 15))
            val lp = getTitleCenterLp(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, -1)
            lp.leftMargin = ToolUtils.instance.dp2px(this@LanguageListActivity, 60f)
            lp.rightMargin = ToolUtils.instance.dp2px(this@LanguageListActivity, 60f)
            setTitleCenterText(getString(R.string.language_settings), 18, "#FF212121", false, lp)
            setTitleLeftClickListener(View.OnClickListener {
                finish()
            })
//            setTitleRightClickListener(View.OnClickListener {
//                backToPrevActivity()
//            })
        }

        return titleView.injectCommTitleViewToAllViewReturnView(getContentView())
    }

    override fun onDestroy() {
        super.onDestroy()
        itemViewList.clear()
    }

    private val itemViewList = ArrayList<View>()

    private fun getContentView(): View {
        return layoutInflater.inflate(R.layout.layout_content_language_list, null).apply {
            findViewById<RecShapeLinearLayout>(R.id.layoutLanList).apply {
                for (i in LanguageManager.instance.supportLanCodeArr.indices) {
                    val lanCode = LanguageManager.instance.supportLanCodeArr[i]
                    val lanName = if (lanCode == LanguageManager.AUTO_LAN_CODE) {
                        getString(R.string.lan_auto)
                    } else {
                        LanguageManager.instance.displayLanguage(this@LanguageListActivity, lanCode)
                    }
                    val itemView = getItemView(lanName) { onSelectItem(i, true) }
                    itemViewList.add(itemView)
                    if (i != 0) {
                        addView(getLineView())
                    }
                    addView(itemView)
                }
            }
        }
    }

    private fun onSelectItem(position: Int, isClick: Boolean = false) {
        if (selectedPosition == position) {
            return
        }
        selectedPosition = position

        for (i in itemViewList.indices) {
            itemViewList[i].findViewById<ImageView>(R.id.ivGo)?.let {
                ToolUtils.instance.setViewVisibility(i == position, it)
            }
        }
        if (isClick && position < LanguageManager.instance.supportLanCodeArr.size) {
            BusinessUtil.instance.showLoading(getString(R.string.loading), false)
            ToolUtils.instance.delayExecute(1500) {
                LanguageManager.instance.setAppLan(LanguageManager.instance.supportLanCodeArr[position])
                ToolUtils.instance.delayExecute(500) {
                    BusinessUtil.instance.closeLoading()
                }
            }
        }
    }

    private fun getItemView(title: String, clickAction: () -> Unit): View {
        return layoutInflater.inflate(R.layout.item_about_menu, null, false).apply {
            val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ToolUtils.instance.dp2px(this@LanguageListActivity, 50f))
            layoutParams = lp
            singleClick { clickAction() }
            findViewById<TextView>(R.id.tvMenu).text = title
            findViewById<ImageView>(R.id.ivGo).setImageResource(R.mipmap.ic_lan_selected)
        }
    }

    private fun getLineView(): View {
        return View(this).apply {
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ToolUtils.instance.dp2px(this@LanguageListActivity, 1f))
            lp.setMargins(ToolUtils.instance.dp2px(this@LanguageListActivity, 20f), 0, ToolUtils.instance.dp2px(this@LanguageListActivity, 20f), 0)
            layoutParams = lp
            setBackgroundColor(Color.parseColor("#E9E9E9"))
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initData() {
        val savedLanCode = LanguageManager.instance.getSavedAppLanCode()
        val supportLanCodeArr = LanguageManager.instance.supportLanCodeArr
        for (i in LanguageManager.instance.supportLanCodeArr.indices) {
            if (supportLanCodeArr[i] == savedLanCode) {
                onSelectItem(i)
                break
            }
        }
    }
}