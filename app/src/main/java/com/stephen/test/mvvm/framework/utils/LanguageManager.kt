package com.stephen.test.mvvm.framework.utils

import android.app.Application
import android.content.Context
import com.orhanobut.hawk.Hawk
import com.stephen.test.mvvm.framework.R

class LanguageManager {
    private lateinit var app: Application

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { LanguageManager() }

        const val AUTO_LAN_CODE = "auto" //默认语言
        private const val KEY_APP_LAN = "app_lan"
    }

    // TODO: 新增支持的语言
    //英语: en_US
    //印度: hi_IN
    //印尼: in_ID
    //孟加拉: bn_BD
    //乌尔都语: ur_PK
    val supportLanCodeArr = arrayOf(AUTO_LAN_CODE, "en_US", "hi_IN", "in_ID", "bn_BD", "ur_PK")
    private val defaultLanLocale = supportLanCodeArr[1] //默认语言

    fun init(application: Application) {
        app = application
    }

    /**
     * 显示语言，除了auto其他语言无需翻译
     */
    fun displayLanguage(context: Context,lanCode: String): String = when (lanCode) {
        supportLanCodeArr[0] -> context.getString(R.string.lan_auto)
        supportLanCodeArr[1] -> "English (United States)"
        supportLanCodeArr[2] -> "हिंदी(भारत)"
        supportLanCodeArr[3] -> "Indonesia (Indonesia)"
        supportLanCodeArr[4] -> "বাংলা (বাংলাদেশ)"
        supportLanCodeArr[5] -> "اردو (پاکستان)"
        else -> LanguageUtils.getLocaleByCode(lanCode).displayName
    }

    /**
     * 保存app设置的语言码
     */
    fun saveAppLanCode(lan: String) {
        Hawk.put(KEY_APP_LAN, lan)
    }

    /**
     * 获取已经保存app设置的语言码
     */
    fun getSavedAppLanCode(): String {
        return Hawk.get(KEY_APP_LAN, AUTO_LAN_CODE)
    }

    /**
     * 是否支持该语言码
     */
    fun isSupport(lanCode: String): Boolean {
        for (supportLanCode in supportLanCodeArr) {
            val supportLocale = LanguageUtils.getLocaleByCode(supportLanCode)
            val locale = LanguageUtils.getLocaleByCode(lanCode)
            if (supportLocale.language == locale.language && supportLocale.country == locale.country) {
                return true
            }
        }
        return false
    }

    fun isAuto(lanCode: String): Boolean {
        return lanCode == AUTO_LAN_CODE
    }

    /**
     * 检查语言码是否是auto，然后检查是否支持，返回最终语言码
     */
    fun checkAutoAndSupport(lanCode: String): String {
        var targetLanCode = lanCode
        if (isAuto(lanCode)) {
            //获取系统
            targetLanCode = LanguageUtils.getSystemLocale().toString()
        }
        if (!isSupport(targetLanCode)) {
            // 不支持就使用默认语言
            targetLanCode = defaultLanLocale
        }
        return targetLanCode
    }

    /**
     * 设置app语言
     */
    fun setAppLan(lanCode: String) {
        saveAppLanCode(lanCode)
        val targetLanCode = checkAutoAndSupport(lanCode)
        // 切换语言
        LanguageUtils.applyLanguage(app, LanguageUtils.getLocaleByCode(targetLanCode), true)
    }
}