package com.qeeyou.accelerator.overseas.overwall.net

import android.content.Context
import com.qeeyou.accelerator.overseas.overwall.utils.BusinessUtil
import com.qeeyou.accelerator.overseas.overwall.utils.ToolUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created By Stephen on 2021/7/1 14:33
 */
object ApiRequestMethod {

    //给Rx版的请求包装线程切换和loading,不需要loading的话loadingMsg传null
    fun <T> wrapRequestLoadingObservable(observable: Observable<T>?, loadingMsg: String?="Loading..."): Observable<T>?{
        if(null == observable)return observable
        val tmpObservable: Observable<T> = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        return if(!loadingMsg.isNullOrBlank()) tmpObservable.doOnSubscribe {
            if(!loadingMsg.isNullOrBlank())BusinessUtil.instance.showLoading(loadingMsg)
        }.doFinally {
            if(!loadingMsg.isNullOrBlank()){
                ToolUtils.instance.delayExecute(500) {
                    BusinessUtil.instance.closeLoading()
                }
            }// end of if
        } else tmpObservable
    }

    //通用错误处理
    fun errorOperationFun(it: Throwable?, context: Context? = null, isToastErrMsg: Boolean? = true): String{
        val errMsg = it?.message ?: "请求异常,请重试"
        if(null != context && isToastErrMsg!!) ToolUtils.instance.showLongHintInfo(context, errMsg)
        return errMsg
    }
}