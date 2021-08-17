package com.stephen.test.mvvm.framework.activitys

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.puy.mvvm.base.NoViewModel
import com.android.puy.mvvm.base.XActivity
import com.android.puy.mvvm.utils.StephenCommonNoDataView
import com.android.puy.mvvm.utils.StephenCommonTopTitleView
import com.stephen.test.mvvm.framework.R
import com.stephen.test.mvvm.framework.adapter.QDListSectionAdapter
import com.stephen.test.mvvm.framework.beans.MainAccInfoBean
import com.stephen.test.mvvm.framework.entitys.SectionHeader
import com.stephen.test.mvvm.framework.entitys.SectionItem
import com.stephen.test.mvvm.framework.qmui.QMUISection
import com.stephen.test.mvvm.framework.qmui.QMUIStickySectionAdapter
import com.stephen.test.mvvm.framework.qmui.QMUIStickySectionLayout
import com.stephen.test.mvvm.framework.utils.JsonUtil
import com.stephen.test.mvvm.framework.utils.StephenCommonNoDataTool
import com.stephen.test.mvvm.framework.utils.ToolUtils

class LineListActivity : XActivity<NoViewModel, ViewDataBinding>(){
    private lateinit var stephenCommonTopTitleView: StephenCommonTopTitleView
    private lateinit var stephenCommonNoDataTool: StephenCommonNoDataTool
    private lateinit var mSectionLayout: QMUIStickySectionLayout
    private lateinit var mAdViewLy: LinearLayout

    private lateinit var mAdapter: QMUIStickySectionAdapter<SectionHeader?, SectionItem?, QMUIStickySectionAdapter.ViewHolder?>
    private var mainDataList: MutableList<QMUISection<SectionHeader?, SectionItem?>>? = null

    override fun initViewMode(): NoViewModel = getViewModel(NoViewModel::class.java)

    override fun getLayoutId(): Int = -1

    override fun getLayoutView(): View? {
        stephenCommonTopTitleView = StephenCommonTopTitleView(this)
        stephenCommonTopTitleView.run {
            setTitleBgColor(ResourcesCompat.getColor(resources, R.color.white, null))
            setTitleLeftIcon(R.drawable.icon_back_btn, getTitleLeftLp(25, 25, 15))
            setTitleCenterText(getString(R.string.title_line_list), 18, "#FF212121", false)
            setTitleLeftClickListener(View.OnClickListener { backBtnClickResponse() })
        }

        val stephenCommonNoDataView = StephenCommonNoDataView(this)
        stephenCommonNoDataView.run {
            setMainNoDataBgColorVal(ResourcesCompat.getColor(resources, R.color.white, null))
            setMainContainerBgColorVal(ResourcesCompat.getColor(resources, R.color.white, null))
            setCenterTextViewStr(getString(R.string.line_load_fail_title), arrayOf(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.CENTER_HORIZONTAL),
                25, 25, 100, -1)
            setCenterTextSizeSpAndColorVal(16, Color.parseColor("#666666"))
            setCenterText2ViewStr(getString(R.string.line_load_fail_desc), 7)
            setCenterText2SizeSpAndColorVal(14, Color.parseColor("#B2B2B2"))
        }

        stephenCommonNoDataTool = StephenCommonNoDataTool(this, stephenCommonNoDataView, globalBottomBtnClickListener = View.OnClickListener {
            initData()
        })

        val mainLy = LinearLayout(this)
        mainLy.orientation = LinearLayout.VERTICAL

        mSectionLayout = QMUIStickySectionLayout(this)
        mSectionLayout.id = ToolUtils.instance.generateViewId()
        mSectionLayout.setLayoutManager(object : LinearLayoutManager(this) {
            override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
                return RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        })

        mainLy.addView(mSectionLayout, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))

        mAdViewLy = LinearLayout(this)
        mAdViewLy.orientation = LinearLayout.VERTICAL
        mainLy.addView(mAdViewLy, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        return stephenCommonTopTitleView.injectCommTitleViewToAllViewReturnView(stephenCommonNoDataView.initAndInjectNoDataViewForAllView(mainLy))
    }

    override fun initView(savedInstanceState: Bundle?) {
        initStatusBar(stephenCommonTopTitleView.topTitleId, true)

        mAdapter = QDListSectionAdapter(this){
            ToolUtils.instance.showLongHintInfo(this, "$it")
        }
        mSectionLayout.setAdapter(mAdapter, false)
    }

    override fun initData() {
        mainDataList = mutableListOf(QMUISection(SectionHeader(SectionHeader.AUTO_ACC), null))
        val nodeList = arrayListOf<MainAccInfoBean.Data.Node?>()
        val mainAccInfoBean = JsonUtil.fromJson(ToolUtils.instance.getFromAssets(this, "lineData.json"), MainAccInfoBean::class.java) as MainAccInfoBean?
        mainAccInfoBean?.data?.forEach {
            it?.run {
                val sectionItemList = arrayListOf<SectionItem>()
                var isExpand = false
                nodes?.forEach { node ->
                    node?.logo_url = icon_url
                    node?.belong_country = country
                    node?.is_selected = false
                    if(!isExpand)isExpand = node?.is_selected ?: false
                    sectionItemList.add(SectionItem(node))
                    nodeList.add(node)
                }
                mainDataList?.add(QMUISection(SectionHeader(country, icon_url), sectionItemList, !isExpand))
            }
        }
        mAdapter?.setDataWithoutDiff(mainDataList, false)
        stephenCommonNoDataTool.commonNoDataViewShow(serverNotData = nodeList.size <= 0, hintMsg = getString(R.string.line_load_fail_title), hint2Msg = getString(R.string.line_load_fail_desc),
            hintPicId = R.drawable.pic_empty_line_list, bottomBtnTxt = getString(R.string.line_load_fail_btn))
    }
}