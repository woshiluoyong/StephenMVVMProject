package com.stephen.test.mvvm.framework.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.*
import android.os.Build
import android.view.View
import android.widget.CheckBox
import android.widget.TextView

/**
 * Created By Stephen on 2020/12/31 11:02
 * 动态设置view的shape背景
 */
object ViewShapeBgUtil {
    const val ShapeType_rectangle = 0
    const val ShapeType_oval = 1
    const val ShapeType_line = 2
    const val ShapeType_ring = 3

    const val GradientType_linear_gradient = 0
    const val GradientType_radial_gradient = 1
    const val GradientType_sweep_gradient = 2

    const val OrientationGradient_top_bottom = 0
    const val OrientationGradient_tr_bl = 1
    const val OrientationGradient_right_left = 2
    const val OrientationGradient_br_tl = 3
    const val OrientationGradient_bottom_top = 4
    const val OrientationGradient_bl_tr = 5
    const val OrientationGradient_left_right = 6
    const val OrientationGradient_tl_br = 7

    //动态设置shape,高宽这些都直接传dp值,不要传px值
    fun setShapeBg(context: Context?, view: View?, shapeType: Int?= ShapeType_rectangle, colorFill: Int?=0x00000000, radiusTopLeft: Float?=0f, radiusTopRight: Float?=0f,
                   radiusBottomLeft: Float?=0f, radiusBottomRight: Float?=0f, radiusCorner: Float?=0f, gradientType: Int?= GradientType_linear_gradient,
                   orientationGradient: Int?= OrientationGradient_left_right, radiusGradient: Int?=0, centerX: Float?=0.5f, centerY: Float?=0.5f, colorStart: Int?=0x00000000,
                   colorCenter: Int?=0x00000000, colorEnd: Int?=0x00000000, strokeWidth: Int?=0, strokeColor: Int?=0x00000000, strokeDashWidth: Int?=0, strokeDashGap: Int?=0,
                   strokePaddingLeft: Int?=0, strokePaddingTop: Int?=0, strokePaddingRight: Int?=0, strokePaddingBottom: Int?=0, haveRipple: Boolean? = false, colorRipple: Int?=0x66000000){
        if(null == view || null == context)return

        val layers = arrayOf<Drawable>(
            generateShapeDrawable(context, shapeType, colorFill, radiusTopLeft, radiusTopRight, radiusBottomLeft, radiusBottomRight, radiusCorner, gradientType,
            orientationGradient, radiusGradient, centerX, centerY, colorStart, colorCenter, colorEnd, strokeWidth, strokeColor, strokeDashWidth, strokeDashGap)
        )
        val layerDrawable = LayerDrawable(layers)
        //设置描边方向，可控制每个方向的描边粗细和有无,不画线的方向，需要padding,strokeWidth的2倍
        layerDrawable.setLayerInset(0, strokePaddingLeft!!, strokePaddingTop!!, strokePaddingRight!!, strokePaddingBottom!!)
        if (haveRipple!! && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//水波纹点击效果,5.0以上才有效,当控件设置了点击监听器,并且控件点击有效时,才能产生水波纹
            val rippleDrawable = RippleDrawable(ColorStateList.valueOf(colorRipple!!), layerDrawable, null)
            view.background = rippleDrawable//设置drawable，大功告成
            return
        }// end of if
        view.background = layerDrawable//设置drawable，大功告成
    }

    fun generateShapeDrawable(context: Context, shapeType: Int?= ShapeType_rectangle, colorFill: Int?=0x00000000, radiusTopLeft: Float?=0f, radiusTopRight: Float?=0f,
                              radiusBottomLeft: Float?=0f, radiusBottomRight: Float?=0f, radiusCorner: Float?=0f, gradientType: Int?= GradientType_linear_gradient,
                              orientationGradient: Int?= OrientationGradient_top_bottom, radiusGradient: Int?=0, centerX: Float?=0.5f, centerY: Float?=0.5f, colorStart: Int?=0x00000000,
                              colorCenter: Int?=0x00000000, colorEnd: Int?=0x00000000, strokeWidth: Int?=0, strokeColor: Int?=0x00000000, strokeDashWidth: Int?=0, strokeDashGap: Int?=0): GradientDrawable{
        val gradientDrawable = GradientDrawable() //创建背景drawable
        //形状类型
        when (shapeType) {
            ShapeType_rectangle -> {
                gradientDrawable.shape = GradientDrawable.RECTANGLE //矩形
                //，左上角开始，顺时针到左下角，1.左上角x方向弧度，2.左上角y方向弧度，3.右上角x方向弧度，4.右下角y方向弧度，以此类推
                val cornerRadii = floatArrayOf(
                    dip2px(context, radiusTopLeft!!).toFloat(), dip2px(context, radiusTopLeft).toFloat(), dip2px(context, radiusTopRight!!).toFloat(),
                    dip2px(context, radiusTopRight).toFloat(), dip2px(context, radiusBottomRight!!).toFloat(), dip2px(context, radiusBottomRight).toFloat(),
                    dip2px(context, radiusBottomLeft!!).toFloat(), dip2px(context, radiusBottomLeft).toFloat())
                gradientDrawable.cornerRadii = cornerRadii //设置四个角的8个弧度半径
                if (radiusCorner != 0f) gradientDrawable.cornerRadius = dip2px(context, radiusCorner!!).toFloat() //radiusCorner优先级比cornerRadii高
            }
            ShapeType_oval -> gradientDrawable.shape = GradientDrawable.OVAL //椭圆
            ShapeType_line -> gradientDrawable.shape = GradientDrawable.LINE //直线
            ShapeType_ring -> gradientDrawable.shape = GradientDrawable.RING //圆环
        }
        gradientDrawable.setColor(colorFill!!) //设置填充色

        when (gradientType) {
            GradientType_linear_gradient -> {
                gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT //线性渐变
                when (orientationGradient) {
                    OrientationGradient_top_bottom -> gradientDrawable.orientation = GradientDrawable.Orientation.TOP_BOTTOM //从上到下
                    OrientationGradient_tr_bl -> gradientDrawable.orientation = GradientDrawable.Orientation.TR_BL //从右上到左下
                    OrientationGradient_right_left -> gradientDrawable.orientation = GradientDrawable.Orientation.RIGHT_LEFT //从右到左
                    OrientationGradient_br_tl -> gradientDrawable.orientation = GradientDrawable.Orientation.BR_TL //从右下到左上
                    OrientationGradient_bottom_top -> gradientDrawable.orientation = GradientDrawable.Orientation.BOTTOM_TOP //从下到上
                    OrientationGradient_bl_tr -> gradientDrawable.orientation = GradientDrawable.Orientation.BL_TR //从左下到右上
                    OrientationGradient_left_right -> gradientDrawable.orientation = GradientDrawable.Orientation.LEFT_RIGHT //从左到右
                    OrientationGradient_tl_br -> gradientDrawable.orientation = GradientDrawable.Orientation.TL_BR //从左上到右下
                }
            }
            GradientType_radial_gradient -> {
                gradientDrawable.gradientType = GradientDrawable.RADIAL_GRADIENT //辐射渐变
                gradientDrawable.gradientRadius = radiusGradient!!.toFloat() //设置渐变半径
                gradientDrawable.setGradientCenter(centerX!!, centerY!!) //设置渐变相对于控件的中心点坐标，如(0.3,0.6)
            }
            GradientType_sweep_gradient -> {
                gradientDrawable.gradientType = GradientDrawable.SWEEP_GRADIENT //扫描渐变
                gradientDrawable.setGradientCenter(centerX!!, centerY!!) //设置渐变相对于控件的中心点坐标，如(0.3,0.6)
            }
        }
        //设置渐变颜色
        if (colorStart != 0x00000000 && colorEnd != 0x00000000) {
            val colors = intArrayOf(colorStart!!, colorCenter!!, colorEnd!!)
            gradientDrawable.colors = colors
        }// end of if
        //描边
        gradientDrawable.setStroke(dip2px(context, strokeWidth!!.toFloat()), strokeColor!!, dip2px(context, strokeDashWidth!!.toFloat()).toFloat(), strokeDashGap!!.toFloat())
        return gradientDrawable
    }

    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    //设置禁用状态selector,"+"/"-"号代表值里的true 和 false, 有“-”为false 没有则为true
    fun setEnabledStateDrawableSelector(view: View?, enabledDrawable: Drawable, disabledDrawable: Drawable){
        val stateListDrawable = StateListDrawable()
        stateListDrawable.addState(intArrayOf(android.R.attr.state_enabled), enabledDrawable)
        stateListDrawable.addState(intArrayOf(-android.R.attr.state_enabled), disabledDrawable)
        view?.background = stateListDrawable
    }

    //设置checkbox selector,"+"/"-"号代表值里的true 和 false, 有“-”为false 没有则为true
    fun setCheckBoxDrawableSelector(context: Context, view: CheckBox?, widthDp: Float, heightDp: Float, checkedDrawable: Drawable, uncheckedDrawable: Drawable){
        val stateListDrawable = StateListDrawable()
        stateListDrawable.addState(intArrayOf(android.R.attr.state_checked), checkedDrawable)
        stateListDrawable.addState(intArrayOf(-android.R.attr.state_checked), uncheckedDrawable)
        stateListDrawable.setBounds(0, 0, dip2px(context, widthDp), dip2px(context, heightDp))
        view?.buttonDrawable = stateListDrawable
    }

    //设置文字颜色selector,"+"/"-"号代表值里的true 和 false, 有“-”为false 没有则为true
    fun setColorSelector(view: TextView?, enabledColor: Int, disabledColor: Int){
        val stateAry = arrayOfNulls<IntArray>(2)
        stateAry[0] = intArrayOf(android.R.attr.state_enabled)
        stateAry[1] = intArrayOf(-android.R.attr.state_enabled)
        view?.setTextColor(ColorStateList(stateAry, intArrayOf(enabledColor, disabledColor)))
    }

    //rgb型的color转为argb型的color
    fun calcIntRgbColorToArgbColor(sourceColor: Int, alpha: Int): Int {
        val red = sourceColor and 0xff0000 shr 16
        val green = sourceColor and 0x00ff00 shr 8
        val blue = sourceColor and 0x0000ff
        println("===Stephen=====calcIntRgbColorToArgbColor===>alpha:$alpha==>red:$red==>green:$green==>blue:$blue")
        return Color.argb(alpha, red, green, blue)
    }
}