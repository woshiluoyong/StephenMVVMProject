package com.stephen.test.mvvm.framework.dataBinding

import android.graphics.drawable.Drawable
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.stephen.test.mvvm.framework.R
import com.stephen.test.mvvm.framework.utils.ToolUtils
import com.stephen.test.mvvm.framework.utils.singleClick

object ViewBinding {

    @JvmStatic
    @BindingAdapter(
        value = ["loadImageUrl", "crossfade", "placeholderResId", "errorResId"],
        requireAll = false
    )
    fun loadImage(imageView: ImageView, loadImageUrl: String?, crossfade: Boolean?, placeholderResId: Drawable?, errorResId: Drawable?) {
        Glide.with(imageView.context).load(loadImageUrl).error(R.drawable.pic_placeholder_error_shape).placeholder(R.drawable.pic_placeholder_error_shape).into(imageView)
    }

    @JvmStatic
    @BindingAdapter(value = ["loadUrl"])
    fun loadUrl(webView: WebView, loadUrl: String) {
        webView.loadUrl(loadUrl)
    }

    @JvmStatic
    @BindingAdapter(value = ["setSingleClickAction"])
    fun setSingleClickAction(view: View, clickAction: () -> Unit) {
        view.singleClick {
            clickAction()
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["imgRes"], requireAll = true)
    fun setImageResource(imageView: ImageView, iconRes: Int?) {
        iconRes?.let {
            imageView.setImageResource(it)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["textColorRes"])
    fun setImageResource(textView: TextView, textColorRes: Int) {
        textView.setTextColor(ToolUtils.instance.getColor(textView.context, textColorRes))
    }
}