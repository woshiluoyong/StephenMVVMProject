package com.qeeyou.accelerator.overseas.overwall.activitys

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import com.android.puy.mvvm.base.NoViewModel
import com.android.puy.mvvm.base.XActivity
import com.android.puy.mvvm.router.Router
import com.android.puy.mvvm.utils.StephenCommonTopTitleView
import com.cy.necessaryview.shapeview.RecShapeLinearLayout
import com.qeeyou.accelerator.overseas.overwall.R
import com.qeeyou.accelerator.overseas.overwall.utils.*
import kotlinx.android.synthetic.main.activity_splash.*
import org.lzh.framework.updatepluginlib.UpdateBuilder
import org.lzh.framework.updatepluginlib.UpdateConfig
import org.lzh.framework.updatepluginlib.base.CheckCallback
import org.lzh.framework.updatepluginlib.model.Update

class AboutActivity : XActivity<NoViewModel, ViewDataBinding>() {

    override fun getLayoutId(): Int = -1

    override fun initViewMode(): NoViewModel = getViewModel(NoViewModel::class.java)

    override fun getLayoutView(): View? {
        initStatusBar(statusBarDark = true, statusBarColor = Color.parseColor("#F0F2F5"), fitsSystemWindows = true)
        val titleView = StephenCommonTopTitleView(this)
        titleView.run {
            setTitleBgColor(Color.parseColor("#F0F2F5"))
            setTitleLeftIcon(R.drawable.icon_back_btn, getTitleLeftLp(25, 25, 15))
            val lp = getTitleCenterLp(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, -1)
            lp.leftMargin = ToolUtils.instance.dp2px(this@AboutActivity, 60f)
            lp.rightMargin = ToolUtils.instance.dp2px(this@AboutActivity, 60f)
            setTitleCenterText(getString(R.string.item_about), 18, "#FF212121", false, lp)
            setTitleLeftClickListener(View.OnClickListener {
                backBtnClickResponse()
            })
        }
        return titleView.injectCommTitleViewToAllViewReturnView(getContentView())
    }

    @SuppressLint("SetTextI18n")
    private fun getContentView(): View {
        return layoutInflater.inflate(R.layout.layout_about_content, null).apply {
            findViewById<TextView>(R.id.tvName).apply {
                text = Html.fromHtml("${getString(R.string.koala)} <font color='#637AFF'>${getString(R.string.vpn)}</font>")
            }

            findViewById<TextView>(R.id.tvVersion).apply {
                text = "V${ToolUtils.instance.getAppVersionName(this@AboutActivity, packageName)}"
            }

            findViewById<RecShapeLinearLayout>(R.id.layoutMenu).apply {
                addView(getItemView(getString(R.string.item_user_protocol)) {
                    Router.newIntent(this@AboutActivity)
                        .putString(Constant.ParamTitle, this@AboutActivity.getString(R.string.item_user_protocol))
                        .putBoolean(Constant.ParamUpdateTitle, false)
                        .putString(Constant.ParamBase, "https://www.2345.com/?k726386446")
                        .to(CommWebActivity::class.java)
                        .launch()
                })
                addView(getLineView())
                addView(getItemView(getString(R.string.item_privacy_protocol)) {
                    Router.newIntent(this@AboutActivity)
                        .putString(Constant.ParamTitle, this@AboutActivity.getString(R.string.item_privacy_protocol))
                        .putBoolean(Constant.ParamUpdateTitle, false)
                        .putString(Constant.ParamBase, "https://www.2345.com/?k726386446")
                        .to(CommWebActivity::class.java)
                        .launch()
                })
            }

            findViewById<RecShapeLinearLayout>(R.id.layoutMenuMore).apply {
                addView(getItemView(getString(R.string.check_update)) {
                    UpdateBuilder.create(UpdateConfig.getConfig().setCheckCallback(object : CheckCallback {
                        override fun onCheckStart() {
                            BusinessUtil.instance.showLoading(getString(R.string.loading))
                        }

                        override fun hasUpdate(update: Update?) {
                            BusinessUtil.instance.closeLoading()
                        }

                        override fun noUpdate() {
                            BusinessUtil.instance.closeLoading()
                            ToolUtils.instance.showShortHintInfo(this@AboutActivity, getString(R.string.no_update))
                        }

                        override fun onCheckError(t: Throwable?) {
                            BusinessUtil.instance.closeLoading()
                            ToolUtils.instance.showShortHintInfo(this@AboutActivity, getString(R.string.no_update))
                        }

                        override fun onUserCancel() {
                        }

                        override fun onCheckIgnore(update: Update?) {
                        }
                    })).check()
                })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        UpdateConfig.getConfig().checkCallback = null
    }

    private fun getItemView(title: String, clickAction: () -> Unit): View {
        return layoutInflater.inflate(R.layout.item_about_menu, null, false).apply {
            val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ToolUtils.instance.dp2px(this@AboutActivity, 50f))
            layoutParams = lp
            singleClick { clickAction() }
            findViewById<TextView>(R.id.tvMenu).text = title
            findViewById<ImageView>(R.id.ivGo).setImageResource(R.mipmap.ic_about_item_go)
        }
    }

    private fun getLineView(): View {
        return View(this).apply {
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ToolUtils.instance.dp2px(this@AboutActivity, 1f))
            lp.setMargins(ToolUtils.instance.dp2px(this@AboutActivity, 20f), 0, ToolUtils.instance.dp2px(this@AboutActivity, 20f), 0)
            layoutParams = lp
            setBackgroundColor(Color.parseColor("#E9E9E9"))
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initData() {
    }
}