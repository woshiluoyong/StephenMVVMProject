### 简单方便入门的Android MVVM框架项目，可直接做基本框架使用
#### 本基础框架抽离于已上线项目KoalaVpn([https://play.google.com/store/apps/details?id=com.qeeyou.accelerator.overseas.overwall])
#### 保留了基本的dataBinding、liveData、viewModel基础用法和页面元素，采用假数据假逻辑展示效果
#### 网络请求采用retrofit, 当然，像网络请求不喜欢用retrofit，也可以换成其他的，非常简单，具体可Clone下来瞧瞧，非常easy
* 内置本人多年总结完善的通用标题头，无侵入式页面布局，动态注入方式，有多个重载方法，及其容易定制和方便使用，例如项目LineListActivity界面演示的例子，完全不需要在xml布局里面写标题头(只是暂时就得舍弃dataBinding了)，支持直接注入动态View或Xml，(详细例子可循)[https://github.com/woshiluoyong/StephenAppCliProject] 查看
```kotlin
stephenCommonTopTitleView = StephenCommonTopTitleView(this)
stephenCommonTopTitleView.run {
    setTitleBgColor(ResourcesCompat.getColor(resources, R.color.white, null))
    setTitleLeftIcon(R.drawable.icon_back_btn, getTitleLeftLp(25, 25, 15))
    setTitleCenterText(getString(R.string.title_line_list), 18, "#FF212121", false)
    setTitleLeftClickListener(View.OnClickListener { backBtnClickResponse() })
}
```
* 内置本人多年总结完善的通用无数据提示，无侵入式页面布局，动态注入方式，有多个重载方法，及其容易定制和方便使用，例如项目演示的例子
```kotlin
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
```
* 通用标题头可和通用无数据提示搭配使用，完美，例如项目演示的例子

```kotlin
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
```
