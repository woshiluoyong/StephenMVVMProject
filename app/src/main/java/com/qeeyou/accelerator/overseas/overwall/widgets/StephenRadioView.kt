package com.qeeyou.accelerator.overseas.overwall.widgets

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.qeeyou.accelerator.overseas.overwall.utils.ViewShapeBgUtil

//自定义radio
class StephenRadioView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int = 0): RelativeLayout(context, attributeSet, defStyleAttr) {
    private val outerRingV = View(context)//外部圆环
    private val innerCircleV = View(context)//内部圆圈
    private var isCurSelect: Boolean = false
    private var lastWidth: Int = 0
    private var lastHeight: Int = 0

    init {
        post {
            resetSetWidthHeight(width, height)
        }
    }

    private fun reAddChildView(){
        removeAllViews()
        addView(outerRingV, LayoutParams(lastWidth, lastHeight))
        val innerLp = LayoutParams(lastWidth - ViewShapeBgUtil.dip2px(context, 8f), lastHeight - ViewShapeBgUtil.dip2px(context, 8f))
        innerLp.addRule(CENTER_IN_PARENT)
        addView(innerCircleV, innerLp)
        //init
        changeRadioSelect(isCurSelect)
    }

    fun resetSetWidthHeight(width: Int, height: Int){
        if(width > 0)lastWidth = width
        if(height > 0)lastHeight = height
        reAddChildView()
    }

    fun toggleRadioSelect(){
        changeRadioSelect(!isCurSelect)
    }

    fun changeRadioSelect(isSelected: Boolean){
        if(isSelected){
            innerCircleV.visibility = View.VISIBLE
            outerRingV.visibility = View.VISIBLE
            ViewShapeBgUtil.setShapeBg(context, outerRingV, strokeWidth = 2, strokeColor = Color.parseColor("#637AFF"), colorFill = Color.TRANSPARENT, radiusCorner = lastHeight / 2f)
            ViewShapeBgUtil.setShapeBg(context, innerCircleV, colorFill = Color.parseColor("#637AFF"), radiusCorner = (lastHeight - ViewShapeBgUtil.dip2px(context, 8f)) / 2f)
        }else{
            innerCircleV.visibility = View.INVISIBLE
            outerRingV.visibility = View.VISIBLE
            ViewShapeBgUtil.setShapeBg(context, outerRingV, strokeWidth = 2, strokeColor = Color.parseColor("#CCCCCC"), colorFill = Color.TRANSPARENT, radiusCorner = lastHeight / 2f)
        }
        isCurSelect = isSelected
    }

    fun isCurSelect(): Boolean = isCurSelect
}