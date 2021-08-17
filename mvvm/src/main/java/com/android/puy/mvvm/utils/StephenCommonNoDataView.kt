package com.android.puy.mvvm.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Stephen on 2016/3/3.
 * 动态注入不影响原布局的无内容提示界面
 */
class StephenCommonNoDataView @JvmOverloads constructor(private val activity: Activity, isResponseClick: Boolean = false) {
    private var mainContentView: View? = null
    var finalCreateView: View? = null
        private set
    private var selfCustomView: View? = null
    val mainContainerView: FrameLayout
    val noDataMainView: RelativeLayout?
    private val centerTV: TextView?
    private var center2TV: TextView? = null
    private val imgWidthDp = 80
    private val imgHeightDp = 80
    private val imgPaddingDp = 20
    private var containerBgColorVal = Color.parseColor("#100f0f")
    private var mainNoDataBgColorVal = Color.TRANSPARENT
    private var textSizeSpVal = 15
    private var textColorVal = Color.WHITE
    private var text2SizeSpVal = 13
    private var text2ColorVal = Color.GRAY
    private var textPositionRules = arrayOf(RelativeLayout.CENTER_IN_PARENT)
    private var textMarginLeftDp = 0
    private var textMarginRightDp = 0
    private var textMarginTopDp = 0
    private var textMarginBottomDp = 0
    private var bothTxtValDp = 10
    private var noDataMarginLeftPx = 0
    private var noDataMarginRightPx = 0
    private var noDataMarginTopPx = 0
    private var noDataMarginBottomPx = 0
    private var hintStr = "没有数据诶!"
    private var hint2Str: String? = null
    private var isInitShowEmpty = false
    private var isResponseClick = false
    private var bottomBtnIds: MutableList<Int>? = null
    private var bottomBtn: Button? = null
    private var customViewLp: RelativeLayout.LayoutParams? = null
    private var onNoDataViewShowListener: OnNoDataViewShowListener? = null

    init {
        mainContainerView = FrameLayout(activity) //主容器
        mainContainerView.setBackgroundColor(containerBgColorVal)
        //create no data view
        noDataMainView = RelativeLayout(activity)
        noDataMainView.setBackgroundColor(mainNoDataBgColorVal)
        centerTV = TextView(activity)
        centerTV.id = generateViewId()
        this.isResponseClick = isResponseClick
    }

    fun initAndInjectNoDataViewForAllViewReturnView(mainLayoutId: Int, replaceViewId: Int): View? { //注入全部,返回view
        val mainView = LayoutInflater.from(activity).inflate(mainLayoutId, null)
        return if (null != mainView) initAndInjectNoDataViewForAllView(mainView.findViewById<View>(replaceViewId)) else null
    }

    fun initAndInjectNoDataViewForAllViewWithActivity(mainLayoutId: Int, replaceViewId: Int) { //注入全部,直接用于activity
        val mainView = LayoutInflater.from(activity).inflate(mainLayoutId, null)
        if (null == mainView) {
            activity.setContentView(mainLayoutId)
        } else {
            activity.setContentView(initAndInjectNoDataViewForAllView(mainView.findViewById<View>(replaceViewId)))
        }
    }

    fun initAndInjectNoDataViewForPartViewReturnView(mainLayoutId: Int, replaceViewId: Int, layoutParams: ViewGroup.LayoutParams?): View? { //注入部分,返回view
        val mainView = LayoutInflater.from(activity).inflate(mainLayoutId, null)
        return if (null != mainView) initAndInjectNoDataViewForPartView(mainView.findViewById(replaceViewId), mainView, layoutParams) else null
    }

    fun initAndInjectNoDataViewForPartViewWithActivity(mainLayoutId: Int, replaceViewId: Int, layoutParams: ViewGroup.LayoutParams?) { //注入部分,直接用于activity
        val mainView = LayoutInflater.from(activity).inflate(mainLayoutId, null)
        if (null == mainView) {
            activity.setContentView(mainLayoutId)
        } else {
            activity.setContentView(initAndInjectNoDataViewForPartView(mainView.findViewById(replaceViewId), mainView, layoutParams))
        }
    }

    fun initAndInjectNoDataViewForAllView(mainLayoutId: Int): View? {
        val mainView = LayoutInflater.from(activity).inflate(mainLayoutId, null)
        return mainView?.let { initAndInjectNoDataViewForAllView(it) }
    }

    fun initAndInjectNoDataViewForAllView(mainContentView: View): View? { //注入全部,可直接用于fragment
        finalCreateView = initInjectSpecificView(mainContentView, null)
        return finalCreateView
    }

    //注：replaceParentViewId必须是replaceViewId的最近一级父布局
    fun initAndInjectNoDataViewForPartView(rootView: View, replaceViewId: Int, replaceParentViewId: Int, layoutParams: ViewGroup.LayoutParams?): View? { //注入部分,可直接用于fragment
        return initAndInjectNoDataViewForPartView(rootView, rootView.findViewById(replaceViewId), rootView.findViewById<View>(replaceParentViewId), layoutParams)
    }

    //注：replaceView和rootView必须得有id
    fun initAndInjectNoDataViewForPartView(replaceView: View, rootView: View, layoutParams: ViewGroup.LayoutParams?): View? { //注入部分,可直接用于fragment
        return initAndInjectNoDataViewForPartView(rootView, replaceView, rootView, layoutParams)
    }

    ////////////////////////////////////////
    fun initAndInjectNoDataViewForPartView(rootView: View?, replaceView: View, replaceParentView: View, layoutParams: ViewGroup.LayoutParams?): View? { //注入部分,可直接用于fragment
        val parentView = replaceParentView as ViewGroup
        val replaceIndex = getChildViewInParentViewIndex(parentView, replaceView.id)
        if (-1 != replaceIndex) {
            parentView.addView(initInjectSpecificView(replaceView, parentView), replaceIndex, layoutParams)
        } else {
            print("===============>在注入无数据提示有控件没找到,请确认传入的控件层级无误(replaceParentViewId必须是replaceViewId的最近一级父布局)!")
        }
        finalCreateView = rootView
        return finalCreateView
    }

    private fun initInjectSpecificView(mainContentView: View, rootView: ViewGroup?): View {
        this.mainContentView = mainContentView
        rootView?.removeView(mainContentView) //rootView主要用于只替换布局中部分的显示内容,如果有必须在下步之前移除
        val flp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mainContainerView.addView(mainContentView, if (null != mainContentView.layoutParams) mainContentView.layoutParams else flp)
        if (null == selfCustomView) {
            centerTV?.run {
                text = hintStr
                gravity = Gravity.CENTER
                textSize = textSizeSpVal.toFloat() //sp
                setTextColor(textColorVal)
                if (null != paint) paint.isFakeBoldText = true
                isSingleLine = false
            }
            val centerTvLp = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            centerTvLp.run {
                textPositionRules?.forEach {
                    addRule(it)
                }
                setMargins(dip2px(activity, textMarginLeftDp.toFloat()), dip2px(activity, textMarginTopDp.toFloat()),
                    dip2px(activity, textMarginRightDp.toFloat()), dip2px(activity, textMarginBottomDp.toFloat()))
            }

            noDataMainView!!.addView(centerTV, centerTvLp)
            if (!hint2Str.isNullOrBlank()) {
                center2TV = TextView(activity)
                center2TV?.run {
                    id = generateViewId()
                    text = hint2Str
                    gravity = Gravity.CENTER
                    textSize = text2SizeSpVal.toFloat() //sp
                    setLineSpacing(text2SizeSpVal * 3 / 2.toFloat(), 1f)
                    setTextColor(text2ColorVal)
                    isSingleLine = false
                }
                val txt2lp = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                txt2lp?.run {
                    topMargin = dip2px(activity, bothTxtValDp.toFloat())
                    marginStart = dip2px(activity, textMarginLeftDp.toFloat())
                    marginEnd = dip2px(activity, textMarginRightDp.toFloat())
                    addRule(RelativeLayout.BELOW, centerTV!!.id)
                    addRule(RelativeLayout.CENTER_HORIZONTAL)
                }
                noDataMainView.addView(center2TV, txt2lp)
            }
        } else {
            if (null == customViewLp) {
                noDataMainView!!.addView(selfCustomView)
            } else {
                noDataMainView!!.addView(selfCustomView, customViewLp)
            }
        }
        flp.marginStart = noDataMarginLeftPx
        flp.topMargin = noDataMarginTopPx
        flp.marginEnd = noDataMarginRightPx
        flp.bottomMargin = noDataMarginBottomPx
        mainContainerView.addView(noDataMainView, flp)
        if (isInitShowEmpty) { //默认
            setNoDataViewInit()
        } else {
            setNoDataViewHide()
        }
        return mainContainerView
    }

    ///////////////////////////////
    fun setInitShowEmpty(initShowEmpty: Boolean) {
        isInitShowEmpty = initShowEmpty
    }

    //自定义无数据页面,会冲掉默认的布局相关设置
    fun setSelfCustomView(selfCustomView: View?, customViewLp: RelativeLayout.LayoutParams?) {
        this.selfCustomView = selfCustomView
        this.customViewLp = customViewLp
    }

    //更改提示文本
    fun setCenterTextViewStr(hintStr: String) {
        setCenterTextViewStr(hintStr, null, -1, -1, -1, -1)
    }

    //textPositionRule是设置RelativeLayout的子view规则,比如RelativeLayout.ALIGN_PARENT_TOP
    fun setCenterTextViewStr(hintStr: String, textPositionRules: Array<Int>?, textMarginLeftDp: Int, textMarginRightDp: Int, textMarginTopDp: Int, textMarginBottomDp: Int) {
        this.hintStr = hintStr
        if (null != textPositionRules && !textPositionRules.isNullOrEmpty()) this.textPositionRules = textPositionRules!!
        if (textMarginLeftDp >= 0) this.textMarginLeftDp = textMarginLeftDp
        if (textMarginRightDp >= 0) this.textMarginRightDp = textMarginRightDp
        if (textMarginTopDp >= 0) this.textMarginTopDp = textMarginTopDp
        if (textMarginBottomDp >= 0) this.textMarginBottomDp = textMarginBottomDp
        if (null != centerTV) centerTV.text = hintStr
    }

    fun setCenterText2ViewStr(hintStr: String?, bothTxtValDp: Int) {
        hint2Str = hintStr
        this.bothTxtValDp = bothTxtValDp //两个文本间距
        if (null != center2TV) center2TV!!.text = hintStr
    }

    fun setCenterTextVisibility(visibility: Int) {
        if (null != centerTV) centerTV.visibility = visibility
    }

    fun setCenterText2Visibility(visibility: Int) {
        if (null != center2TV) center2TV!!.visibility = visibility
    }

    //提示文本上面的提示图
    fun setCenterTextTopHintImg(imgResId: Int) {
        setCenterTextTopHintImg(imgResId, imgWidthDp, imgHeightDp, imgPaddingDp)
    }

    fun setCenterTextTopHintImg(imgResId: Int, imgWidthDp: Int, imgHeightDp: Int, imgPaddingDp: Int) {
        setTextViewAroundDrawable(activity, centerTV, imgResId, imgWidthDp, imgHeightDp, imgPaddingDp, Gravity.TOP)
    }

    fun setCenterTextTopHintImg(imgDrawable: Drawable?) {
        setCenterTextTopHintImg(imgDrawable, imgWidthDp, imgHeightDp, imgPaddingDp)
    }

    fun setCenterTextTopHintImg(imgDrawable: Drawable?, imgWidthDp: Int, imgHeightDp: Int, imgPaddingDp: Int) {
        setTextViewAroundDrawable(activity, centerTV, imgDrawable, imgWidthDp, imgHeightDp, imgPaddingDp, Gravity.TOP)
    }

    //提示文本和提示图一起设置
    fun setCenterTextStrAndHintImg(hintStr: String, imgResId: Int, imgWidthDp: Int, imgHeightDp: Int, imgPaddingDp: Int) {
        setCenterTextViewStr(hintStr)
        setCenterTextTopHintImg(imgResId, imgWidthDp, imgHeightDp, imgPaddingDp)
    }

    //提示文本下面的按钮,支持多次设置
    fun setCenterTextBottomBtn(btnText: String?, btnColor: Int, btnBgSelector: Drawable?, btnWidthDp: Int, btnHeightDp: Int, imgMarginDp: Int, onClickListener: View.OnClickListener?) {
        if (null != noDataMainView) {
            bottomBtn = Button(activity)
            bottomBtn!!.id = generateViewId()
            bottomBtn!!.gravity = Gravity.CENTER
            bottomBtn!!.text = btnText
            bottomBtn!!.setTextColor(btnColor)
            bottomBtn!!.isSingleLine = true
            bottomBtn!!.setPadding(0, 0, 0, 0)
            setBackgroundAllVersion(bottomBtn, btnBgSelector)
            val bottomBtnLp = RelativeLayout.LayoutParams(dip2px(activity, btnWidthDp.toFloat()), dip2px(activity, btnHeightDp.toFloat()))
            bottomBtnLp.addRule(RelativeLayout.CENTER_HORIZONTAL)
            bottomBtnLp.setMargins(0, dip2px(activity, imgMarginDp.toFloat()), 0, 0)
            if (null == bottomBtnIds) {
                if (null != center2TV) {
                    bottomBtnLp.addRule(RelativeLayout.BELOW, center2TV!!.id)
                } else {
                    if (null != centerTV) bottomBtnLp.addRule(RelativeLayout.BELOW, centerTV.id)
                }
                bottomBtnIds = ArrayList()
            } else {
                bottomBtnLp.addRule(RelativeLayout.BELOW, bottomBtnIds!![bottomBtnIds!!.size - 1])
            }
            bottomBtnIds!!.add(bottomBtn!!.id)
            noDataMainView.addView(bottomBtn, bottomBtnLp)
            bottomBtn!!.setOnClickListener(onClickListener)
        } //end of if
    }

    fun removeCenterTextBottomBtn(btnIndex: Int) {//传-1清除全部
        if (null != noDataMainView && !bottomBtnIds.isNullOrEmpty() && btnIndex < bottomBtnIds!!.size){
            if(btnIndex < 0){
                bottomBtnIds!!.forEachIndexed{index,value ->
                    val tmpBottomBtn = noDataMainView.findViewById<Button>(value)
                    if(null != tmpBottomBtn)noDataMainView.removeView(tmpBottomBtn)
                }
                bottomBtnIds!!.clear()
                bottomBtnIds = null
            }else{
                val tmpBottomBtn = noDataMainView.findViewById<Button>(btnIndex)
                if(null != tmpBottomBtn)noDataMainView.removeView(tmpBottomBtn)
                bottomBtnIds!!.removeAt(btnIndex)
            }
        }// end of if
    }

    fun setCenterTextBottomBtnVisibility(visibility: Int) {
        if (null != bottomBtn) bottomBtn!!.visibility = visibility
    }

    fun setCenterTextBottomBtnText(hintStr: String?) {
        if (null != bottomBtn) bottomBtn!!.text = hintStr
    }

    fun setMainContainerBgColorVal(bgColorVal: Int) {
        containerBgColorVal = bgColorVal
        mainContainerView.setBackgroundColor(containerBgColorVal)
    }

    fun setMainNoDataBgColorVal(bgColorVal: Int) {
        mainNoDataBgColorVal = bgColorVal
        noDataMainView!!.setBackgroundColor(mainNoDataBgColorVal)
    }

    fun setCenterTextSizeSpAndColorVal(textSizeSpVal: Int, textColorVal: Int) {
        this.textSizeSpVal = textSizeSpVal
        this.textColorVal = textColorVal
    }

    fun setCenterText2SizeSpAndColorVal(textSizeSpVal: Int, textColorVal: Int) {
        text2SizeSpVal = textSizeSpVal
        text2ColorVal = textColorVal
    }

    fun setNoDataViewShow() {
        setNoDataViewShow(null)
    }

    fun setNoDataViewShow(hintStr: String?) {
        setNoDataViewShow(true, hintStr)
    }

    fun setNoDataViewShow(isResponseClick: Boolean, hintStr: String?) {
        setResponseClick(isResponseClick)
        if(!hintStr.isNullOrEmpty() && null != centerTV) centerTV.text = hintStr
        if (null != mainContentView) if (mainContentView!!.visibility != View.GONE) mainContentView!!.visibility = View.GONE
        if (null != noDataMainView) if (noDataMainView.visibility != View.VISIBLE) noDataMainView.visibility = View.VISIBLE
        onNoDataViewShowListener?.onNoDataViewShow(true)
    }

    fun setNoDataViewHide() {
        setResponseClick(true)
        if (null != noDataMainView) if (noDataMainView.visibility != View.GONE) noDataMainView.visibility = View.GONE
        if (null != mainContentView) if (mainContentView!!.visibility != View.VISIBLE) mainContentView!!.visibility = View.VISIBLE
        onNoDataViewShowListener?.onNoDataViewShow(false)
    }

    fun setNoDataViewInit() {
        setResponseClick(true)
        if (null != noDataMainView) noDataMainView.visibility = View.GONE
        if (null != mainContentView) mainContentView!!.visibility = View.GONE
        onNoDataViewShowListener?.onNoDataViewShow(false)
    }

    fun setResponseClick(responseClick: Boolean) {
        isResponseClick = responseClick
    }

    fun setNoDataUiPadding(leftDp: Int, topDp: Int, rightDp: Int, bottomDp: Int) {
        noDataMainView?.setPadding(dip2px(activity, leftDp.toFloat()), dip2px(activity, topDp.toFloat()),
            dip2px(activity, rightDp.toFloat()), dip2px(activity, bottomDp.toFloat()))
    }

    fun setNoDataUiMargin(leftDp: Int, topDp: Int, rightDp: Int, bottomDp: Int) {
        noDataMarginLeftPx = dip2px(activity, leftDp.toFloat())
        noDataMarginTopPx = dip2px(activity, topDp.toFloat())
        noDataMarginRightPx = dip2px(activity, rightDp.toFloat())
        noDataMarginBottomPx = dip2px(activity, bottomDp.toFloat())
    }

    fun setOnNoDataViewClickListener(onClickListener: View.OnClickListener) {
        setOnNoDataViewClickListener(false, onClickListener)
    }

    fun setOnNoDataViewClickListener(isNoDataViewHide: Boolean, onClickListener: View.OnClickListener) {
        noDataMainView?.setOnClickListener(View.OnClickListener {
            if (isResponseClick) {
                if (isNoDataViewHide) setNoDataViewHide()
                onClickListener?.onClick(it)
            } //end of if
        })
    }

    fun setOnNoDataViewShowListener(onNoDataViewShowListener: OnNoDataViewShowListener){
        this.onNoDataViewShowListener = onNoDataViewShowListener
    }

    ////////////////////////////////////////工具方法start/////////////////////////////////////////////

    //将dp转换为与之相等的px
    fun dip2px(context: Context?, dpValue: Float): Int {
        if(null == context)return dpValue.toInt()
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    //兼容低版本的设置View背景方法,避免报错
    fun setBackgroundAllVersion(view: View?, bgDrawable: Drawable?) {
        if(null == view)return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.background = bgDrawable
        } else {
            view.setBackgroundDrawable(bgDrawable)
        }
    }

    //生成viewId
    private var sNextGeneratedId :AtomicInteger? = null
    fun generateViewId(): Int {
        if(null == sNextGeneratedId)sNextGeneratedId = AtomicInteger(1)
        while (true) {
            val result: Int = sNextGeneratedId!!.get()
            var newValue = result + 1
            if (newValue > 0x00FFFFFF) newValue = 1 //Roll over to 1, not 0.
            if (sNextGeneratedId!!.compareAndSet(result, newValue)) return result
        }
    }

    //得到子控件在父控件中的索引
    fun getChildViewInParentViewIndex(parentView: ViewGroup?, findViewId: Int): Int {
        if (null != parentView) {
            for (i in 0 until parentView.childCount) {
                val childView = parentView.getChildAt(i)
                if (null != childView && findViewId == childView.id) return i
            } //end of for
        } //end of if
        return -1
    }

    //这是设置图片的不同状态,设置TextView环绕图片(direction value is 1/left,2/top,3/right,4/bottom)
    fun setTextViewAroundDrawable(context: Context, textView: TextView?, imgResId: Int, imgWidthDp: Int, imgHeightDp: Int, imgPaddingDp: Int, direction: Int) {
        setTextViewAroundDrawable(context, textView, context.resources.getDrawable(imgResId), imgWidthDp, imgHeightDp, imgPaddingDp, direction)
    }

    fun setTextViewAroundDrawable(context: Context?, textView: TextView?, aroundDrawable: Drawable?, imgWidthDp: Int, imgHeightDp: Int, imgPaddingDp: Int, direction: Int) {
        if (null != textView && null != aroundDrawable) {
            aroundDrawable.setBounds(0, 0, dip2px(context, imgWidthDp.toFloat()), dip2px(context, imgHeightDp.toFloat()))
            textView.compoundDrawablePadding = dip2px(context, imgPaddingDp.toFloat())
            when (direction) {
                Gravity.LEFT -> textView.setCompoundDrawables(aroundDrawable, null, null, null)
                Gravity.TOP -> textView.setCompoundDrawables(null, aroundDrawable, null, null)
                Gravity.RIGHT -> textView.setCompoundDrawables(null, null, aroundDrawable, null)
                Gravity.BOTTOM -> textView.setCompoundDrawables(null, null, null, aroundDrawable)
            }
        } //edn of if
    }

    ////////////////////////////////////////工具方法end/////////////////////////////////////////////

    interface OnNoDataViewClickListener {
        fun onNoDataViewClick()
    }

    interface OnNoDataViewShowListener {
        fun onNoDataViewShow(isShow: Boolean)
    }
}