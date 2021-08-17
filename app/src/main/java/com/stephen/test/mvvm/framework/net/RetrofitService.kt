package com.stephen.test.mvvm.framework.net

import com.stephen.test.mvvm.framework.beans.IpViewBean
import io.reactivex.Observable
import retrofit2.http.GET

//请求参数接口
interface RetrofitService {
    @GET("/api/common/current_ip/")
    fun getCurIpAddress(): Observable<IpViewBean?>?
}