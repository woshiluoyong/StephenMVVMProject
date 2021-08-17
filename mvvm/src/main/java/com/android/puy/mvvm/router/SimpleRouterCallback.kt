package com.android.puy.mvvm.router

import android.app.Activity

class SimpleRouterCallback : RouterCallback {
    override fun onBefore(from: Activity?, to: Class<*>?) {}
    override fun onNext(from: Activity?, to: Class<*>?) {}
    override fun onError(from: Activity?, to: Class<*>?, throwable: Throwable?) {}
}