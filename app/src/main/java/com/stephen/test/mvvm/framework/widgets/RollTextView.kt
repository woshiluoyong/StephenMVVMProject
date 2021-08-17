package com.stephen.test.mvvm.framework.widgets

import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet

class RollTextView@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        //设置单行
        setSingleLine()
        //设置Ellipsize
        ellipsize = TextUtils.TruncateAt.MARQUEE
        //获取焦点
        isFocusable = true
        //走马灯的重复次数，-1代表无限重复
        marqueeRepeatLimit = -1
        //强制获得焦点
        isFocusableInTouchMode = true
    }

    override fun isFocused(): Boolean {
        return true
    }

    //用于EditText抢注焦点的问题
    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        if (focused) super.onFocusChanged(focused, direction, previouslyFocusedRect)
    }

    //Window与Window间焦点发生改变时的回调
    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        if (hasWindowFocus) super.onWindowFocusChanged(hasWindowFocus)
    }
}