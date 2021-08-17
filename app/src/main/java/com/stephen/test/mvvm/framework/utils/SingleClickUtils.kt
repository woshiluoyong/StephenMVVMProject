package com.stephen.test.mvvm.framework.utils

import android.view.View

inline fun <T : View> T.singleClick(time: Long = 1000, crossinline block: (T) -> Unit) {
    setOnClickListener {
        val currentTimeMillis = System.currentTimeMillis()
        if ((currentTimeMillis < lastClickTime || currentTimeMillis - lastClickTime > time) && this.isClickable) {
            lastClickTime = currentTimeMillis
            block(this)
        }// end of if
    }
}
//兼容点击事件设置为this的情况
fun <T : View> T.singleClick(onClickListener: View.OnClickListener, time: Long = 1000) {
    setOnClickListener {
        val currentTimeMillis = System.currentTimeMillis()
        if ((currentTimeMillis < lastClickTime || currentTimeMillis - lastClickTime > time) && this.isClickable) {
            lastClickTime = currentTimeMillis
            onClickListener.onClick(this)
        }// end of if
    }
}

var <T : View> T.lastClickTime: Long
    set(value) = setTag(1766613352, value)
    get() = getTag(1766613352) as? Long ?: 0