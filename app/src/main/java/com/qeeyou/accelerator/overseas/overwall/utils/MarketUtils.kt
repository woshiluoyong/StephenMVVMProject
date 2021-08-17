package com.qeeyou.accelerator.overseas.overwall.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import java.util.*

object MarketUtils {
    /**
     * 跳转应用商店.
     *
     * @param context   [Context]
     * @param appPkg    包名
     * @param marketPkg 应用商店包名
     * @return `true` 跳转成功 <br></br> `false` 跳转失败
     */
    fun toMarket(context: Context, appPkg: String?, marketPkg: String?): Boolean {
        return if (appPkg == null) {
            val intent = Intent("android.intent.action.MAIN")
            intent.addCategory("android.intent.category.APP_MARKET")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(intent)
                true
            } catch (ex: Exception) {
                ex.printStackTrace()
                false
            }
        } else {
            val uri = Uri.parse("market://details?id=$appPkg")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (marketPkg != null) { // 如果没给市场的包名，则系统会弹出市场的列表让你进行选择。
                intent.setPackage(marketPkg)
            }
            try {
                context.startActivity(intent)
                true
            } catch (ex: Exception) {
                ex.printStackTrace()
                false
            }
        }
    }

    /**
     * 跳转三星应用商店
     *
     * @param context     [Context]
     * @param packageName 包名
     * @return `true` 跳转成功 <br></br> `false` 跳转失败
     */
    fun toSamsungMarket(context: Context, packageName: String): Boolean {
        val uri = Uri.parse("http://www.samsungapps.com/appquery/appDetail.as?appId=$packageName")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.sec.android.app.samsungapps")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 直接跳转至应用宝
     *
     * @param context [Context]
     * @param appPkg  包名
     * @return `true` 跳转成功 <br></br> `false` 跳转失败
     */
    fun toYingyongbaoMarket(context: Context, appPkg: String?): Boolean {
        return toMarket(context, appPkg, "com.tencent.android.qqdownloader")
    }

    /**
     * 直接跳转至360手机助手
     *
     * @param context [Context]
     * @param appPkg  包名
     * @return `true` 跳转成功 <br></br> `false` 跳转失败
     */
    fun to360Market(context: Context, appPkg: String?): Boolean {
        return toMarket(context, appPkg, "com.qihoo.appstore")
    }

    /**
     * 直接跳转至豌豆荚
     *
     * @param context [Context]
     * @param appPkg  包名
     * @return `true` 跳转成功 <br></br> `false` 跳转失败
     */
    fun toWandoujiaMarket(context: Context, appPkg: String?): Boolean {
        return toMarket(context, appPkg, "com.wandoujia.phoenix2")
    }

    /**
     * 直接跳转至华为应用
     *
     * @param context [Context]
     * @param appPkg  包名
     * @return `true` 跳转成功 <br></br> `false` 跳转失败
     */
    fun toHuaweiMarket(context: Context, appPkg: String?): Boolean {
        return toMarket(context, appPkg, "com.huawei.appmarket")
    }

    /**
     * 直接跳转至小米应用商店
     *
     * @param context [Context]
     * @param appPkg  包名
     * @return `true` 跳转成功 <br></br> `false` 跳转失败
     */
    fun toXiaoMiMarket(context: Context, appPkg: String?): Boolean {
        return toMarket(context, appPkg, "com.xiaomi.market")
    }

    /**
     * 直接跳转至ViVO应用商店
     *
     * @param context [Context]
     * @param appPkg  包名
     * @return `true` 跳转成功 <br></br> `false` 跳转失败
     */
    fun toViVOMarket(context: Context, appPkg: String?): Boolean {
        return toMarket(context, appPkg, "com.bbk.appstore")
    }

    /**
     * 直接跳转至PP助手
     *
     * @param context [Context]
     * @param appPkg  包名
     * @return `true` 跳转成功 <br></br> `false` 跳转失败
     */
    fun toPPMarket(context: Context, appPkg: String?): Boolean {
        return toMarket(context, appPkg, "com.pp.assistant")
    }

    /**
     * 直接跳转至oppo应用商店
     *
     * @param context [Context]
     * @param appPkg  包名
     * @return `true` 跳转成功 <br></br> `false` 跳转失败
     */
    fun toOppoMarket(context: Context, appPkg: String?): Boolean {
        return toMarket(context, appPkg, "com.oppo.market")
    }

    /**
     * 直接跳转至魅族应用商店
     *
     * @param context [Context]
     * @param appPkg  包名
     * @return `true` 跳转成功 <br></br> `false` 跳转失败
     */
    fun toMeizuMarket(context: Context, appPkg: String?): Boolean {
        return toMarket(context, appPkg, "com.meizu.mstore")
    }

    /**
     * 直接跳转至谷歌应用商店
     *
     * @param context [Context]
     * @param appPkg  包名
     * @return `true` 跳转成功 <br></br> `false` 跳转失败
     */
    fun toGooglePlayMarket(context: Context, appPkg: String?): Boolean {
        return toMarket(context, appPkg, "com.android.vending")
    }

    /**
     * 直接跳转至乐视应用商店
     *
     * @param context [Context]
     * @param appPkg  包名
     * @return `true` 跳转成功 <br></br> `false` 跳转失败
     */
    fun toToLeTVMarket(context: Context, appPkg: String?): Boolean {
        val intent = Intent()
        intent.setClassName("com.letv.app.appstore", "com.letv.app.appstore.appmodule.details.DetailsActivity")
        intent.action = "com.letv.app.appstore.appdetailactivity"
        intent.putExtra("packageName", appPkg)
        return try {
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 检查已安装的应用商店
     * 但是小米商店目前检测不出，先定义为bug
     *
     * @param context [Context]
     * @return 返回包名列表
     */
    fun checkMarket(context: Context): List<String> {
        val packageNames: MutableList<String> = ArrayList()
        val intent = Intent()
        intent.action = "android.intent.action.MAIN"
        intent.addCategory("android.intent.category.APP_MARKET")
        val pm = context.packageManager
        val infos = pm.queryIntentActivities(intent, 0)
        val size = infos.size
        for (i in 0 until size) {
            val activityInfo = infos[i].activityInfo
            val packageName = activityInfo.packageName
            println("============>packageName : $packageName")
            packageNames.add(packageName)
        }
        return packageNames
    }
}