package com.qeeyou.accelerator.overseas.overwall.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Process
import android.text.TextUtils
import android.util.Log
import java.util.*
import kotlin.system.exitProcess

/**
 * @description: 语言工具类
 *
 * @author: wyh
 * @time: 2021/7/14
 */
object LanguageUtils {

    fun applyLanguage(context: Context, newLocale: Locale, restartApp: Boolean): Boolean {
        val appResources: Resources = context.resources
        val configuration = appResources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(newLocale)
            // 当在activity onCreate使用，可能存在语言切换无效，需使用updateConfiguration
            context.applicationContext.createConfigurationContext(configuration)
        } else {
            configuration.locale = newLocale
        }
        context.resources.updateConfiguration(configuration, appResources.displayMetrics)

        val curLocale = getAppLocale(context)
        if (curLocale.language != newLocale.language || curLocale.country != newLocale.country) {
            return false
        }
        if (restartApp) {
            relaunchApp(context, false)
        }
        return true
    }

    /**
     * 获取应用local
     */
    fun getAppLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            context.resources.configuration.locale
        }
    }

    fun getSystemLocale(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales[0]
        } else {
            Resources.getSystem().configuration.locale
        }
    }

    /**
     * Relaunch the application.
     *
     * @param isKillProcess True to kill the process, false otherwise.
     */
    fun relaunchApp(context: Context, isKillProcess: Boolean) {
        val intent = getLauncherAppIntent(context, context.packageName)
        if (intent == null) {
            Log.e("AppUtils", "Didn't exist launcher activity.")
            return
        }
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        )
        context.startActivity(intent)
        if (!isKillProcess) return
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }

    fun getLauncherActivity(context: Context, pkg: String): String? {
        if (pkg.isEmpty()) return null
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.setPackage(pkg)
        val pm: PackageManager = context.packageManager
        val info = pm.queryIntentActivities(intent, 0)
        return if (info.size == 0) {
            null
        } else info[0].activityInfo.name
    }

    fun getLauncherAppIntent(context: Context, pkgName: String): Intent? {
        val launcherActivity = getLauncherActivity(context, pkgName)
        if (launcherActivity.isNullOrEmpty()) return null
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.setClassName(pkgName, launcherActivity)
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    /**
     * 通过语言码获取Local对象
     */
    fun getLocaleByCode(lanCode: String): Locale {
        var language = ""
        var country = ""
        val lc = lanCode.split("_")
        if (lc.isNotEmpty()) {
            language = lc[0]
            if (lc.size > 1) {
                country = lc[1].toUpperCase()
            }
        }
        return Locale(language, country)
    }

    /**
     * 获取系统语言码
     */
    fun getAppLanguageCode(context: Context): String {
        // 获取系统语言
        val sysLocale = getAppLocale(context)
        var sysCode = sysLocale.language
        val c = sysLocale.country
        if (!TextUtils.isEmpty(c)) {
            sysCode += "_$c"
        }
        return sysCode
    }
}