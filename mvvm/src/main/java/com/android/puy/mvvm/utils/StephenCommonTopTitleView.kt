package com.android.puy.mvvm.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.ImageView.ScaleType
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

/**
 * Created by Stephen on 2016/3/1.
 * 动态注入不影响原布局的TitleBar
 */
class StephenCommonTopTitleView {
    var MasterColorHex = "#3883C4"
    var MasterFontColorHex: String? = "#ffffff" //字体颜色
    var titleHeightForDp = 50
        private set
    private var TitleContentHeightForDp = -1
    private var titlePositionDpVal = 0 // titlePositionDpVal为0时Gravity.CENTER,为-x时Gravity.TOP,为+x时Gravity.BOTTOM
    private val TitleBottomLineColorHex = "#20000000"
    private var ParentFrameBgColorHex = "#ffffff" //cp
    private var activity: Activity
    //private ImageView titleBgImgV;
    var titleBottomLine: View? = null
        private set
    var titleLeftView: View? = null
        private set
    var titleCenterView: View? = null
        private set
    var titleRightView: View? = null
        private set
    //设置的内容布局
    var contentView: View? = null
        private set
    //整个视图(titleBar+内容)布局
    var mainParentView: View? = null
        private set
    //titleBar布局
    var topTitleView: FrameLayout? = null
        private set
    private var titleLeftFy: FrameLayout? = null
    private var titleRightFy: FrameLayout? = null
    private var titleCenterFy: FrameLayout? = null
    private var contentAboveTitle = false // 当parentIsFrame时title和content的重叠顺序,默认title在上

    constructor(activity: Activity) {
        this.activity = activity
        initDefaultCommTitleView()
    }

    constructor(activity: Activity, titleHeightForDp: Int) {
        this.activity = activity
        this.titleHeightForDp = titleHeightForDp
        initDefaultCommTitleView()
    }

    constructor(activity: Activity, titleHeightForDp: Int, contentAboveTitle: Boolean) {
        this.activity = activity
        this.titleHeightForDp = titleHeightForDp
        this.contentAboveTitle = contentAboveTitle // contentAboveTitle主体布局内容和标题在Frame布局上下是否对换
        initDefaultCommTitleView()
    }

    constructor(activity: Activity, titleHeightForDp: Int, contentAboveTitle: Boolean, titlePositionDpVal: Int) {
        this.activity = activity
        this.titleHeightForDp = titleHeightForDp
        this.contentAboveTitle = contentAboveTitle
        this.titlePositionDpVal = titlePositionDpVal // titlePositionVal设置请看上面解释
        initDefaultCommTitleView()
    }

    constructor(activity: Activity, titleHeightForDp: Int, titleContentHeightForDp: Int, contentAboveTitle: Boolean, titlePositionDpVal: Int) { // titlePositionVal设置请看上面解释
        this.activity = activity
        this.titleHeightForDp = titleHeightForDp // 整个标题栏高度
        TitleContentHeightForDp = titleContentHeightForDp // 真正标题内容区高度(当内容区侵入标题栏区域时就需要注意这个值了,默认为TitleHeightForDp高度)
        this.contentAboveTitle = contentAboveTitle
        this.titlePositionDpVal = titlePositionDpVal
        initDefaultCommTitleView()
    }

    private fun initDefaultCommTitleView() {
        topTitleView = FrameLayout(activity)
        topTitleView?.run {
            id = generateViewId()
            setBackgroundColor(Color.parseColor(MasterColorHex))
        }
        /*titleBgImgV = new ImageView(activity);
        titleBgImgV.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        titleBgFy.addView(titleBgImgV);*/
        val titleFyLp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(activity, if (TitleContentHeightForDp > 0) TitleContentHeightForDp.toFloat() else titleHeightForDp.toFloat()))
        //titleFyLp.gravity = Gravity.CENTER;
        titleFyLp.gravity = if (0 == titlePositionDpVal) Gravity.CENTER else if (titlePositionDpVal < 0) Gravity.TOP else Gravity.BOTTOM
        if (0 != titlePositionDpVal) {
            if (titlePositionDpVal < 0) titleFyLp.topMargin = dip2px(activity, abs(titlePositionDpVal).toFloat())
            if (titlePositionDpVal > 0) titleFyLp.bottomMargin = dip2px(activity, abs(titlePositionDpVal).toFloat())
        } //end of if
        titleCenterFy = FrameLayout(activity) //中间内容父布局
        //titleCenterFy.setBackgroundResource(R.color.color_red);
        topTitleView!!.addView(titleCenterFy, titleFyLp)
        val titleLeftRightRy = RelativeLayout(activity) //左右按钮父布局
        //titleLeftRightRy.setBackgroundColor(Color.GREEN);//test
        titleLeftRightRy.clipChildren = false //ripple parent set
        //titleLeftRightRy.setPadding(dip2px(activity, TitleHeightForDp / 9), 0, dip2px(activity, TitleHeightForDp / 9), 0);
        topTitleView!!.addView(titleLeftRightRy, titleFyLp)

        titleLeftFy = FrameLayout(activity)
        val titleLeftFyLp = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        titleLeftFyLp.run {
            addRule(RelativeLayout.ALIGN_PARENT_START)
            addRule(RelativeLayout.CENTER_VERTICAL)
        }
        titleRightFy = FrameLayout(activity)
        //titleRightFy.setBackgroundResource(R.color.color_link);
        val titleRightFyLp = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        titleRightFyLp.run {
            addRule(RelativeLayout.ALIGN_PARENT_END)
            addRule(RelativeLayout.CENTER_VERTICAL)
        }
        titleLeftRightRy.addView(titleRightFy, titleRightFyLp)
        titleLeftRightRy.addView(titleLeftFy, titleLeftFyLp)

        titleBottomLine = View(activity)
        titleBottomLine!!.setBackgroundColor(Color.parseColor(TitleBottomLineColorHex))
        topTitleView!!.addView(titleBottomLine, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(activity, 1f), Gravity.BOTTOM))
        titleBottomLineVisibility = View.GONE
        titleCenterVisibility = View.GONE
        titleLeftVisibility = View.GONE
        titleRightVisibility = View.GONE
    }

    @JvmOverloads
    fun injectCommTitleViewToAllViewReturnView(mainLayoutId: Int, parentIsFrame: Boolean = false, topPaddingDp: Int = -1): View { //注入全部,返回view
        return injectCommTitleViewToAllViewReturnView(LayoutInflater.from(activity).inflate(mainLayoutId, null), parentIsFrame, topPaddingDp)
    }

    @JvmOverloads
    fun injectCommTitleViewToAllViewReturnView(mainView: View?, parentIsFrame: Boolean = false, topPaddingDp: Int = -1): View { //注入全部,返回view
        contentView = mainView
        return if (parentIsFrame) {
            val mainFy = FrameLayout(activity)
            mainFy.run{
                id = generateViewId()
                setBackgroundColor(Color.parseColor(ParentFrameBgColorHex))
            }
            if (topPaddingDp > 0) mainView!!.setPadding(0, dip2px(activity, topPaddingDp.toFloat()), 0, 0)
            if (contentAboveTitle) {
                mainFy.run {
                    addView(topTitleView, FrameLayout.LayoutParams.MATCH_PARENT, dip2px(activity, titleHeightForDp.toFloat()))
                    if (null != mainView) addView(mainView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
                }
                //mainView.setBackgroundColor(ResourcesCompat.getColor(activity.getResources(), R.color.color_red, null));
            } else {
                mainFy.run{
                    if (null != mainView) addView(mainView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
                    addView(topTitleView, FrameLayout.LayoutParams.MATCH_PARENT, dip2px(activity, titleHeightForDp.toFloat()))
                }
            }
            mainParentView = mainFy
            mainFy
        } else {
            val mainLy = LinearLayout(activity)
            mainLy.run{
                id = generateViewId()
                setBackgroundColor(Color.parseColor(ParentFrameBgColorHex))
                orientation = LinearLayout.VERTICAL
                addView(topTitleView, LinearLayout.LayoutParams.MATCH_PARENT, dip2px(activity, titleHeightForDp.toFloat()))
                if (null != mainView) addView(mainView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            }
            mainParentView = mainLy
            mainLy
        }
    }

    //注入全部,直接用于activity
    @JvmOverloads
    fun injectCommTitleViewToAllViewWithActivity(mainLayoutId: Int, parentIsFrame: Boolean = false, topPaddingDp: Int = -1) {
        val mainView = LayoutInflater.from(activity).inflate(mainLayoutId, null)
        if (null == mainView) {
            activity.setContentView(mainLayoutId)
        } else {
            injectCommTitleViewToAllViewWithActivity(mainView, parentIsFrame, topPaddingDp)
        }
    }

    //注入全部,直接用于activity
    @JvmOverloads
    fun injectCommTitleViewToAllViewWithActivity(mainView: View, parentIsFrame: Boolean = false, topPaddingDp: Int = -1) {
        contentView = mainView
        if (parentIsFrame) {
            val mainFy = FrameLayout(activity)
            mainFy.run{
                id = generateViewId()
                setBackgroundColor(Color.parseColor(ParentFrameBgColorHex))
            }
            if (topPaddingDp > 0) mainView.setPadding(0, dip2px(activity, topPaddingDp.toFloat()), 0, 0)
            if (contentAboveTitle) {
                mainFy.run{
                    addView(topTitleView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, dip2px(activity, titleHeightForDp.toFloat())))
                    addView(mainView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
                }
            } else {
                mainFy.run {
                    addView(mainView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
                    addView(topTitleView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, dip2px(activity, titleHeightForDp.toFloat())))
                }
            }
            mainParentView = mainFy
            activity.setContentView(mainFy)
        } else {
            val mainLy = LinearLayout(activity)
            mainLy.run{
                id = generateViewId()
                setBackgroundColor(Color.parseColor(ParentFrameBgColorHex))
                orientation = LinearLayout.VERTICAL
                addView(topTitleView, LinearLayout.LayoutParams.MATCH_PARENT, dip2px(activity, titleHeightForDp.toFloat()))
                addView(mainView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            }
            mainParentView = mainLy
            activity.setContentView(mainLy)
        }
    }

    fun setPaddingTopVal(ptDpVal: Int) { // 解决沉浸式带来的问题
        topTitleView!!.setPadding(0, dip2px(activity, if (DefaultPt == ptDpVal) (titleHeightForDp / 3).toFloat() else ptDpVal.toFloat()), 0, 0)
    }

    fun setParentFrameBgColorHex(parentFrameBgColorHex: String) {
        ParentFrameBgColorHex = parentFrameBgColorHex
    }

    val topTitleId: Int
        get() = if (null != topTitleView) topTitleView!!.id else -1

    val mainParentId: Int
        get() = if (null != mainParentView) mainParentView!!.id else -1

    var titleBottomLineVisibility: Int
        get() = titleBottomLine!!.visibility
        set(visibility) {
            titleBottomLine!!.visibility = visibility
        }

    fun setTitleBgColor(color: Int) {
        topTitleView!!.setBackgroundColor(color)
    }

    fun setTitleBgDrawable(bgDrawable: Drawable?) {
        setBackgroundAllVersion(topTitleView, bgDrawable)
        //titleBgImgV.setImageDrawable(bgDrawable);
    }

    fun setBottomLineHeight(colorHex: String, heightDp: Float){
        titleBottomLine!!.setBackgroundColor(Color.parseColor(colorHex))
        topTitleView!!.removeView(titleBottomLine)
        topTitleView!!.addView(titleBottomLine, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(activity, heightDp), Gravity.BOTTOM))
    }

    ///////////////////////////////////center
    var titleCenterVisibility: Int
        get() = titleCenterFy!!.visibility
        set(visibility) {
            titleCenterFy!!.visibility = visibility
        }

    fun setTitleCenterText(text: String?) {
        setTitleCenterText(text, null)
    }

    fun setTitleCenterText(text: String?, lp: FrameLayout.LayoutParams?) {
        setTitleCenterText(null, text, -1, null, false, lp)
    }

    fun setTitleCenterText(text: String?, isBold: Boolean, lp: FrameLayout.LayoutParams?) {
        setTitleCenterText(null, text, -1, null, isBold, lp)
    }

    fun setTitleCenterText(text: String?, textSizeSp: Int, colorHex: String?, isBold: Boolean) {
        setTitleCenterText(null, text, textSizeSp, colorHex, isBold, null)
    }

    fun setTitleCenterText(text: String?, colorHex: String?, isBold: Boolean, lp: FrameLayout.LayoutParams?) {
        setTitleCenterText(null, text, -1, colorHex, isBold, lp)
    }

    fun setTitleCenterText(text: String?, textSizeSp: Int, colorHex: String?, isBold: Boolean, lp: FrameLayout.LayoutParams?) {
        setTitleCenterText(null, text, textSizeSp, colorHex, isBold, lp)
    }

    fun setTitleCenterText(textViewVal: TextView?, textVal: String?, textSizeSp: Int, colorHex: String?, isBold: Boolean, lp: FrameLayout.LayoutParams?) {
        var textView = textViewVal
        if (null == textView) {
            textView = TextView(activity)
            textView.run{
                gravity = Gravity.CENTER
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END
            }
        } //end of if
        textView.run{
            textSize = if (textSizeSp <= 0) { titleHeightForDp / 2.toFloat() } else { textSizeSp.toFloat() }
            setTextColor(if (TextUtils.isEmpty(colorHex)) Color.parseColor(MasterFontColorHex) else Color.parseColor(colorHex))
            if (isBold && null != paint) paint.isFakeBoldText = true
            text = textVal
        }
        setTitleCenterView(textView, lp ?: FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER))
    }

    fun setTitleCenterIcon(iconId: Int) {
        setTitleCenterIcon(iconId, FrameLayout.LayoutParams(dip2px(activity, titleHeightForDp / 3 * 2.toFloat()),
                dip2px(activity, titleHeightForDp / 3 * 2.toFloat()), Gravity.CENTER))
    }

    fun setTitleCenterIcon(iconId: Int, flParams: FrameLayout.LayoutParams?) {
        setTitleCenterIcon(null, iconId, flParams)
    }

    fun setTitleCenterIcon(imageViewVal: ImageView?, iconId: Int, flParams: FrameLayout.LayoutParams?) {
        var imageView = imageViewVal
        if (null == imageView) imageView = ImageView(activity)
        imageView.run {
            setImageDrawable(activity.resources.getDrawable(iconId))
        }
        setTitleCenterView(imageView, flParams)
    }

    fun setTitleCenterView(curView: View?, flParams: FrameLayout.LayoutParams?) {
        if (titleCenterVisibility != View.VISIBLE) titleCenterVisibility = View.VISIBLE
        if (null != titleCenterView) {
            titleCenterFy?.removeView(titleCenterView)
            titleCenterView = null
        } //end of if
        if (null == titleCenterView) {
            titleCenterView = curView
            titleCenterView?.run{
                id = generateViewId()
            }
            titleCenterFy!!.addView(titleCenterView, flParams)
        } //end of if
    }

    fun setTitleCenterClickListener(onClickListener: View.OnClickListener?) {
        titleCenterFy!!.setOnClickListener(onClickListener)
    }

    ///////////////////////////////////left
    var titleLeftVisibility: Int
        get() = titleLeftFy!!.visibility
        set(visibility) {
            titleLeftFy!!.visibility = visibility
        }

    fun setTitleLeftText(text: String?, isBold: Boolean) {
        setTitleLeftText(text, isBold, null)
    }

    fun setTitleLeftText(text: String?, isBold: Boolean, lp: FrameLayout.LayoutParams?) {
        setTitleLeftText(null, text, -1, null, isBold, lp)
    }

    fun setTitleLeftText(text: String?, textSizeSp: Int, colorHex: String?, isBold: Boolean) {
        setTitleLeftText(null, text, textSizeSp, colorHex, isBold,null)
    }

    fun setTitleLeftText(text: String?, textSizeSp: Int, colorHex: String?, isBold: Boolean, lp: FrameLayout.LayoutParams?) {
        setTitleLeftText(null, text, textSizeSp, colorHex, isBold, lp)
    }

    fun setTitleLeftText(textViewVal: TextView?, textVal: String?, textSizeSp: Int, colorHex: String?, isBold: Boolean, lp: FrameLayout.LayoutParams?) {
        var textView = textViewVal
        if (null == textView) {
            textView = TextView(activity)
            textView.run{
                gravity = Gravity.CENTER
                setSingleLine(true)
            }
        } //end of if
        textView.run{
            textSize = if (textSizeSp <= 0) { titleHeightForDp / 3.toFloat() } else { textSizeSp.toFloat() }
            setTextColor(if (TextUtils.isEmpty(colorHex)) Color.parseColor(MasterFontColorHex) else Color.parseColor(colorHex))
            if (isBold && null != paint) paint.isFakeBoldText = true
            text = textVal
        }
        setTitleLeftView(textView, lp ?: FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER))
    }

    fun setTitleLeftIcon(iconId: Int) {
        setTitleLeftIcon(iconId, ScaleType.FIT_CENTER, FrameLayout.LayoutParams(dip2px(activity, titleHeightForDp / 9 * 4.toFloat()),
                dip2px(activity, titleHeightForDp / 9 * 4.toFloat()), Gravity.CENTER))
    }

    fun setTitleLeftIcon(iconId: Int, flParams: FrameLayout.LayoutParams?) {
        setTitleLeftIcon(iconId, ScaleType.FIT_CENTER, flParams)
    }

    fun setTitleLeftIcon(iconId: Int, scaleTypeVal: ScaleType?, flParams: FrameLayout.LayoutParams?) {
        val imageView = ImageView(activity)
        imageView.run{
            scaleType = scaleTypeVal
            setImageDrawable(activity.resources.getDrawable(iconId))
        }
        setTitleLeftView(imageView, flParams)
    }

    fun setTitleLeftIcon(iconBmp: Bitmap?, scaleTypeVal: ScaleType?, flParams: FrameLayout.LayoutParams?) {
        val imageView = ImageView(activity)
        imageView.run {
            scaleType = scaleTypeVal
            setImageBitmap(iconBmp)
        }
        setTitleLeftView(imageView, flParams)
    }

    fun setTitleLeftView(curView: View?, flParams: FrameLayout.LayoutParams?) {
        if (titleLeftVisibility != View.VISIBLE) titleLeftVisibility = View.VISIBLE
        if (null != titleLeftView) {
            titleLeftFy?.removeView(titleLeftView)
            titleLeftView = null
        } //end of if
        if (null == titleLeftView) {
            titleLeftView = curView
            titleLeftView?.run{
                id = generateViewId()
            }
            titleLeftFy!!.addView(titleLeftView, flParams)
        } //end of if
    }

    fun setTitleLeftClickListener(onClickListener: View.OnClickListener?) {
        titleLeftFy!!.setOnClickListener(onClickListener)
    }

    ///////////////////////////////////right
    var titleRightVisibility: Int
        get() = titleRightFy?.visibility ?: -1
        set(visibility) {
            titleRightFy?.visibility = visibility
        }

    fun setTitleRightText(text: String?, isBold: Boolean) {
        setTitleRightText(text, isBold, null)
    }

    fun setTitleRightText(text: String?, isBold: Boolean, lp: FrameLayout.LayoutParams?) {
        setTitleRightText(null, text, -1, null, isBold, lp)
    }

    fun setTitleRightText(text: String?, textSizeSp: Int, colorHex: String?, isBold: Boolean) {
        setTitleRightText(null, text, textSizeSp, colorHex, isBold, null)
    }

    fun setTitleRightText(text: String?, textSizeSp: Int, colorHex: String?, isBold: Boolean, lp: FrameLayout.LayoutParams?) {
        setTitleRightText(null, text, textSizeSp, colorHex, isBold, lp)
    }

    fun setTitleRightText(textViewVal: TextView?, textVal: String?, textSizeSp: Int, colorHex: String?, isBold: Boolean, lp: FrameLayout.LayoutParams?) {
        var textView = textViewVal
        if (null == textView) {
            textView = TextView(activity)
            textView.run{
                gravity = Gravity.CENTER
                setSingleLine(true)
            }
        } //end of if
        textView.run{
            textSize = if (textSizeSp <= 0) { titleHeightForDp / 3.toFloat() } else { textSizeSp.toFloat() }
            setTextColor(if (TextUtils.isEmpty(colorHex)) Color.parseColor(MasterFontColorHex) else Color.parseColor(colorHex))
            if (isBold && null != paint) paint.isFakeBoldText = true
            text = textVal
        }
        setTitleRightView(textView, lp ?: FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER))
    }

    fun setTitleRightIcon(iconId: Int) {
        setTitleRightIcon(iconId, FrameLayout.LayoutParams(dip2px(activity, titleHeightForDp / 9 * 4.toFloat()),
                dip2px(activity, titleHeightForDp / 9 * 4.toFloat()), Gravity.CENTER))
    }

    fun setTitleRightIcon(iconId: Int, flParams: FrameLayout.LayoutParams?) {
        setTitleRightIcon(iconId, ScaleType.FIT_CENTER, flParams)
    }

    fun setTitleRightIcon(iconId: Int, scaleType: ScaleType?, flParams: FrameLayout.LayoutParams?) {
        setTitleRightIcon(null, iconId, scaleType, flParams)
    }

    fun setTitleRightIcon(imageViewVal: ImageView?, iconId: Int, scaleTypeVal: ScaleType?, flParams: FrameLayout.LayoutParams?) {
        var imageView = imageViewVal
        if (null == imageView) imageView = ImageView(activity)
        imageView.run {
            scaleType = scaleTypeVal
            setImageDrawable(activity.resources.getDrawable(iconId))
        }
        setTitleRightView(imageView, flParams)
    }

    fun setTitleRightIcon(iconBmp: Bitmap?) {
        setTitleRightIcon(iconBmp, ScaleType.FIT_CENTER, FrameLayout.LayoutParams(dip2px(activity, titleHeightForDp / 9 * 5.toFloat()),
                dip2px(activity, titleHeightForDp / 9 * 5.toFloat()), Gravity.CENTER))
    }

    fun setTitleRightIcon(iconBmp: Bitmap?, scaleTypeVal: ScaleType?, flParams: FrameLayout.LayoutParams?) {
        val imageView = ImageView(activity)
        imageView.run {
            scaleType = scaleTypeVal
            setImageBitmap(iconBmp)
        }
        setTitleRightView(imageView, flParams)
    }

    fun setTitleRightView(curView: View?, flParams: FrameLayout.LayoutParams?) {
        if (titleRightVisibility != View.VISIBLE) titleRightVisibility = View.VISIBLE
        if(null != titleRightView){
            titleRightFy?.removeView(titleRightView)
            titleRightView = null
        }// end of if
        if (null == titleRightView) {
            titleRightView = curView
            titleRightView?.run{
                id = generateViewId()
            }
            titleRightFy!!.addView(titleRightView, flParams)
        } //end of if
    }

    fun setTitleRightClickListener(onClickListener: View.OnClickListener?) {
        titleRightFy?.setOnClickListener(onClickListener)
    }

    ////////////////////////////////////////////////////////////////////
    fun setVisibility(visibility: Int) {
        topTitleView?.visibility = visibility
    }

    fun getVisibility() = topTitleView?.visibility

    fun setTitleClickListener(onClickListener: View.OnClickListener?) {
        topTitleView?.setOnClickListener(onClickListener)
    }

    ////////////////////////////////////////////////////////////////////
    val titleLeftLp: FrameLayout.LayoutParams
        get() = getTitleLeftLp(20, 20, 12)

    fun getTitleLeftLp(widthForDp: Int, heightForDp: Int, leftMarginDp: Int): FrameLayout.LayoutParams {
        val leftLp = FrameLayout.LayoutParams(if (isSystemLayoutParams(widthForDp)) widthForDp else dip2px(activity, widthForDp.toFloat()),
            if (isSystemLayoutParams(heightForDp)) heightForDp else dip2px(activity, heightForDp.toFloat()))
        leftLp.run {
            if (leftMarginDp > -1) marginStart = dip2px(activity, leftMarginDp.toFloat())
            gravity = Gravity.CENTER_VERTICAL
        }
        return leftLp
    }

    val titleRightLp: FrameLayout.LayoutParams
        get() = getTitleRightLp(20, 20, 12)

    fun getTitleRightLp(widthForDp: Int, heightForDp: Int, rightMarginDp: Int): FrameLayout.LayoutParams {
        val rightLp = FrameLayout.LayoutParams(if (isSystemLayoutParams(widthForDp)) widthForDp else dip2px(activity, widthForDp.toFloat()),
            if (isSystemLayoutParams(heightForDp)) heightForDp else dip2px(activity, heightForDp.toFloat()))
        rightLp.run {
            if (rightMarginDp > -1) marginEnd = dip2px(activity, rightMarginDp.toFloat())
            gravity = Gravity.CENTER_VERTICAL
        }
        return rightLp
    }

    val titleCenterLp: FrameLayout.LayoutParams
        get() = getTitleCenterLp(-1, -1, titleHeightForDp / 6)

    fun getTitleCenterLp(widthForDp: Int, heightForDp: Int, topMarginDp: Int): FrameLayout.LayoutParams {
        val centerLp = FrameLayout.LayoutParams(if (-1 == widthForDp) ViewGroup.LayoutParams.WRAP_CONTENT else if (isSystemLayoutParams(widthForDp))
            widthForDp else dip2px(activity, widthForDp.toFloat()), if (-1 == heightForDp) ViewGroup.LayoutParams.MATCH_PARENT
            else if (isSystemLayoutParams(heightForDp)) heightForDp else dip2px(activity, heightForDp.toFloat()))
        centerLp.run {
            gravity = Gravity.CENTER
            if (topMarginDp > -1) topMargin = dip2px(activity, topMarginDp.toFloat())
        }
        return centerLp
    }

    private fun isSystemLayoutParams(`val`: Int): Boolean {
        return ViewGroup.LayoutParams.WRAP_CONTENT == `val` || ViewGroup.LayoutParams.MATCH_PARENT == `val` || ViewGroup.LayoutParams.FILL_PARENT == `val`
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
    private val sNextGeneratedId = AtomicInteger(1)
    fun generateViewId(): Int {
        while (true) {
            val result: Int = sNextGeneratedId.get()
            var newValue = result + 1
            if (newValue > 0x00FFFFFF) newValue = 1 //Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) return result
        }
    }

    ////////////////////////////////////////工具方法end/////////////////////////////////////////////

    companion object {
        var DefaultPt = -1
    }
}