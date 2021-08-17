package com.stephen.test.mvvm.framework.net

import com.stephen.test.mvvm.framework.beans.IpViewBean
import com.stephen.test.mvvm.framework.utils.BusinessUtil
import com.stephen.test.mvvm.framework.utils.Constant
import com.stephen.test.mvvm.framework.utils.ToolUtils
import io.reactivex.Observable
import okhttp3.*
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

//Retrofit helper class
class RetrofitHelper private constructor() {
    //////////////////////////////////接口调用方法////////////////////////////////////////////

    //获取ip地址
    fun getCurIpAddress(): Observable<IpViewBean?>? {
        return retrofitService.getCurIpAddress()
    }

    //////////////////////////////////初始化相关////////////////////////////////////////////
    private lateinit var retrofitService: RetrofitService

    companion object {
        private var retrofitHelper: RetrofitHelper? = null
        val instance: RetrofitHelper
            get() = if (retrofitHelper == null) RetrofitHelper().also {
                retrofitHelper = it
            } else retrofitHelper!!
    }

    init {
        initRetrofitService()
    }

    //初始化Retrofit,提出来方便切换语言
    private fun initRetrofitService() {
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.addInterceptor { chain ->
            val originRequest = chain.request()
            val requestBuilder = originRequest.newBuilder()
            requestBuilder.addHeader("UA", BusinessUtil.instance.getUa())//公共头带上自定义Ua
            val afterRequest = requestBuilder.build()
            logRequest(afterRequest)
            val response = chain.proceed(afterRequest)
            logResponse(response)
        }

        val retrofit = Retrofit.Builder().baseUrl(Constant.SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create()) // json解析
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
            .client(httpClientBuilder.build()).build()
        retrofitService = retrofit.create(RetrofitService::class.java)
    }

    private fun logRequest(request: Request) {
        try {
            var requestBodyStr = "NULL"
            if(null != request.body()) {
                val mediaType = request.body()!!.contentType()
                if(null != mediaType) {
                    requestBodyStr = if (isTextType(mediaType)) {
                        bodyToString(request)
                    } else {
                        "maybe [file part], too large too print, ignored!"
                    }
                }// end of if
            }// end of if
            val headers = request.headers()
            ToolUtils.instance.debugPrintln("===Stephen==NetApi==开始请求==(${request.url().toString()})==Method:${request.method()}==" +
                    "${if(null != headers && headers.size() > 0) "Header:${headers.toString()}" else ""}==>参数:${if("GET" == request.method()) "Get请求参数请看链接" else requestBodyStr}")
        } catch (e: Exception) {
            e.printStackTrace()
            ToolUtils.instance.debugPrintln("===Stephen==NetApi==开始请求==(${request?.url()})==Method:${request?.method()}==出现异常==>${e.message}")
        }
    }

    private fun logResponse(response: Response): Response {
        try {
            val clone = response.newBuilder().build()

            var responseBody = clone.body()
            var responseBodyStr = "NULL"
            if(null != responseBody) {
                val mediaType = responseBody.contentType()
                if (mediaType != null) {
                    if (isTextType(mediaType)) {
                        responseBodyStr = responseBody.string()
                        responseBody = ResponseBody.create(mediaType, responseBodyStr)
                        ToolUtils.instance.debugPrintln("===Stephen==NetApi==请求结束==(${response.request()?.url()})==Method:${response.request()?.method()}==code:${clone?.code()}==>$responseBodyStr")

                        when(clone.code()){
                            403 -> {//用户占线
                            }
                            401 -> {//token过期
                            }
                        }
                        return response.newBuilder().body(responseBody).build()
                    } else {
                        responseBodyStr = "maybe [file part], too large too print, ignored!"
                        ToolUtils.instance.debugPrintln("===Stephen==NetApi==请求结束==(${response.request()?.url()})==Method:${response.request()?.method()}==code:${clone?.code()}====>$responseBodyStr")
                    }
                }// end of if
            }// end of if
            //if(clone.code() == 401)
        } catch (e: Exception) {
            e.printStackTrace()
            ToolUtils.instance.debugPrintln("===Stephen==NetApi==请求结束==(${response.request()?.url()})==Method:${response.request()?.method()}==出现异常==>${e.message}")
        }
        return response
    }

    private fun isTextType(mediaType: MediaType?): Boolean {
        return if (null == mediaType || mediaType.subtype().isNullOrEmpty()) false else "text" == mediaType.subtype() || "json" == mediaType.subtype()
                || "xml" == mediaType.subtype() || "html" == mediaType.subtype() || "webviewhtml" == mediaType.subtype()
                || "x-www-form-urlencoded" == mediaType.subtype()
    }

    private fun bodyToString(request: Request): String {
        return try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body()!!.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            "something error when show requestBody."
        }
    }
}