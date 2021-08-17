package com.stephen.test.mvvm.framework.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.json.JSONObject
import java.io.*
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.pow

//通用工具方法
class ToolUtils {
    private var lastClickTime: Long = 0

    private fun readResolve(): Any {//防止单例对象在反序列化时重新生成对象
        return instance
    }

    companion object {
        @JvmStatic
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ToolUtils()
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //生成viewId
    private val sNextGeneratedId = AtomicInteger(100)
    fun generateViewId(): Int {
        while (true) {
            val result: Int = sNextGeneratedId.get()
            var newValue = result + 1
            if (newValue > 0x00FFFFFF) newValue = 1 //Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) return result
        }
    }

    fun isFastClick(): Boolean {//两次点击按钮之间的点击间隔不能少于XX毫秒
        var flag = false
        val curClickTime = System.currentTimeMillis()
        if (curClickTime - lastClickTime < 1000) flag = true
        lastClickTime = curClickTime
        return flag
    }

    //调试日志输出
    fun debugPrintln(msgStr: String) {
        if (BusinessUtil.instance.isDebug()) println("===Stephen==$msgStr")
    }

    fun showLongHintInfo(context: Context?, text: String?) {
        if(null == context)return
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    fun showShortHintInfo(context: Context?, text: CharSequence?) {
        if(null == context)return
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun fromJsonToObj(json: String?): JSONObject? {
        if (json.isNullOrBlank()) return null
        try {
            return JSONObject(json)
        } catch (e: java.lang.Exception) {
        }
        return null
    }

    fun setViewVisibility(isShow: Boolean, vararg viewAry: View?) {
        if (!viewAry.isNullOrEmpty()) {
            viewAry.forEach {
                if (null != it) {
                    if (isShow) {
                        if (it.visibility != View.VISIBLE) it.visibility = View.VISIBLE
                    } else {
                        if (it.visibility != View.GONE) it.visibility = View.GONE
                    }
                }// end of if
            }
        }// end of if
    }

    fun setViewVisibility2(isShow: Boolean, vararg viewAry: View?) {
        if (!viewAry.isNullOrEmpty()) {
            viewAry.forEach {
                if (null != it) {
                    if (isShow) {
                        if (it.visibility != View.VISIBLE) it.visibility = View.VISIBLE
                    } else {
                        if (it.visibility != View.INVISIBLE) it.visibility = View.INVISIBLE
                    }
                }// end of if
            }
        }// end of if
    }

    //中文两个字符,英文一个
    fun getStringLength(value: String): Int {
        if (value.isNullOrEmpty()) return 0
        var valueLength = 0
        val chinese = "[\u4e00-\u9fa5]"
        for (i in value.indices) {
            val temp = value.substring(i, i + 1)
            valueLength += if (temp.matches(Regex(chinese))) {
                2
            } else {
                1
            }
        }
        return valueLength
    }

    //裁断超出的文字
    fun subStringWordCut(value: String, cutNum: Int): String {
        if (value.isNullOrEmpty()) return value
        val resultStr = StringBuilder()
        var valueLength = 0
        val chinese = "[\u4e00-\u9fa5]"
        for (i in value.indices) {
            val temp = value.substring(i, i + 1)
            valueLength += if (temp.matches(Regex(chinese))) {
                2
            } else {
                1
            }
            if (valueLength <= cutNum) resultStr.append(temp)
        }
        return resultStr.toString()
    }

    //系统文本分享
    fun callSystemTxtShare(context: Context, title: String, content: String) {
        val textIntent = Intent(Intent.ACTION_SEND)
        textIntent.type = "text/plain"
        textIntent.putExtra(Intent.EXTRA_TEXT, content)
        context.startActivity(Intent.createChooser(textIntent, title))
    }

    //动态设置margin
    fun dynamicSetMargin(context: Context, v: View, l: Int? = null, t: Int? = null, r: Int? = null, b: Int? = null) {
        if (v?.layoutParams == null) return
        if (v.layoutParams is ViewGroup.MarginLayoutParams) {
            val p: ViewGroup.MarginLayoutParams = v.layoutParams as ViewGroup.MarginLayoutParams
            if (null != l) p.leftMargin = dp2px(context, l.toFloat())
            if (null != t) p.topMargin = dp2px(context, t.toFloat())
            if (null != r) p.rightMargin = dp2px(context, r.toFloat())
            if (null != b) p.bottomMargin = dp2px(context, b.toFloat())
            v.requestLayout()
        }// end of if
    }

    //动态设置view宽高
    fun dynamicSetLayoutParams(v: View, wPx: Int? = null, hPx: Int? = null) {
        if (v?.layoutParams == null) return
        val lp = v.layoutParams
        if (null != wPx && lp.width != wPx) lp.width = wPx
        if (null != hPx && lp.height != hPx) lp.height = hPx
        v.layoutParams = lp
    }

    //计算字符串的宽度(像素)
    fun getStringPixelWidth(strPaint: Paint, str: String?): Int {
        return strPaint.measureText(str).toInt()
    }

    //延迟执行
    @SuppressLint("CheckResult")
    fun delayExecute(delayMill: Long, callBack: () -> Unit){
        Observable.timer(delayMill, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe {//延迟,避免状态错乱
            callBack()
        }
    }

    //获取剪切板内容
    fun getClipboardContent(context: Context): String? {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (cm != null) {
            val data = cm.primaryClip
            if (data != null && data.itemCount > 0) {
                val item = data.getItemAt(0)
                if (item != null) {
                    val sequence = item.coerceToText(context)
                    if (sequence != null) {
                        return sequence.toString()
                    }
                }
            }
        }
        return null
    }

    //清除剪切板内容
    fun clearClipboardContent(context: Context) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (cm != null) {
            try {
                cm.primaryClip?.let { cm.setPrimaryClip(it) }
                cm.text = null
            } catch (e: java.lang.Exception) {
            }
        }
    }

    //深拷贝,需传入对象implements Serializable
    fun deepCopyObj(old: Any?): Any? {
        if (null == old) return null
        try {
            // 写入字节流
            val baos = ByteArrayOutputStream()
            val oos = ObjectOutputStream(baos)
            oos.writeObject(old)
            // 读取字节流
            val bais = ByteArrayInputStream(baos.toByteArray())
            val ois = ObjectInputStream(bais)
            return ois.readObject() as Any
        } catch (e: IOException) {
            println("====Stephen==========>深拷贝对象失败")
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            println("====Stephen==========>深拷贝对象失败")
            e.printStackTrace()
        }
        return old
    }

    //将dp转换为与之相等的px
    fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    //将px转换为与之相等的dp
    fun px2dp(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    //将px转换为sp
    fun px2sp(context: Context, pxValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    //将sp转换为px
    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    fun getScreenWHToPoint(context: Activity): Point? {
        val dm = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(dm)
        return Point(dm.widthPixels, dm.heightPixels)
    }

    fun getColor(context: Context, res: Int): Int {
        return ResourcesCompat.getColor(context.resources, res, null)
    }

    //获取格式化的日期字符
    fun getFormattedDate(time: Long, pattern: String, locale: Locale = Locale.getDefault()): String {
        val date = Date(time)
        val sdf = SimpleDateFormat(pattern, locale)
        return sdf.format(date)
    }

    //Return the application's version name
    fun getAppVersionName(context: Context, packageName: String?): String {
        return if (packageName.isNullOrEmpty()) "" else try {
            val pm: PackageManager = context.packageManager
            val pi = pm.getPackageInfo(packageName, 0)
            if (pi == null) "" else pi.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }

    //Return the application's version code
    fun getAppVersionCode(context: Context, packageName: String?): Long {
        return if (packageName.isNullOrEmpty()) -1L else try {
            val pm: PackageManager = context.packageManager
            val pi = pm.getPackageInfo(packageName, 0)
            when {
                pi == null -> -1L
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                    pi.longVersionCode
                }
                else -> {
                    pi.versionCode.toLong()
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            -1L
        }
    }

    //获取指定长度的随机数字字符串
    fun getRandomNumString(len: Int): String {
        val maxValue = 10.0.pow(len.toDouble()).toInt()
        val num = (Math.random() * maxValue).toInt()
        return numToString(num, len, '0')
    }

    //将数字转化为指定长度的字符串，长度不足在前面用tag字符填充
    fun numToString(num: Int, len: Int, tag: Char): String {
        val numStr = StringBuilder().append(num)
        while (numStr.toString().length < len) {
            numStr.insert(0, tag)
        }
        return numStr.toString()
    }

    fun openUrlExternalBrowser(context: Context, url: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isRtl(context: Context, locale: Locale): Boolean {
        return TextUtilsCompat.getLayoutDirectionFromLocale(locale) == ViewCompat.LAYOUT_DIRECTION_RTL
    }

    fun getPhoneMac(): String {
        try {
            val all = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (nif.name.toLowerCase((Locale.getDefault())) != "wlan0") continue
                val macBytes = nif.hardwareAddress ?: return ""
                val res1 = StringBuilder()
                for (b in macBytes) {
                    res1.append(String.format("%02X:", b))
                }
                if (res1.isNotEmpty()) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString().toUpperCase((Locale.getDefault()))
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return ""
    }

    //判断网络类型
    fun getNetworkTypeName(context: Context): String {
        var type: String = "disconnect"
        val wifiInfo = (context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo ?: return type
        val networkInfo = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo ?: return type
        if (networkInfo.isConnected) {
            val netType = networkInfo.type
            val subType = networkInfo.subtype
            val subTypeName = networkInfo.subtypeName

            if (netType == ConnectivityManager.TYPE_WIFI) {
                type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    checkWifiGHz(wifiInfo.frequency)
                } else {
                    "Wi-Fi"
                }
            } else if (netType == ConnectivityManager.TYPE_MOBILE) {
                type = when (subType) {
                    TelephonyManager.NETWORK_TYPE_IDEN -> "2G"
                    TelephonyManager.NETWORK_TYPE_HSPAP -> "3G"
                    TelephonyManager.NETWORK_TYPE_LTE -> "4G"
                    else ->                             // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (subTypeName.equals("TD-SCDMA", ignoreCase = true) || subTypeName.equals("WCDMA", ignoreCase = true) || subTypeName.equals("CDMA2000", ignoreCase = true)) {
                            "3G"
                        } else {
                            subTypeName
                        }
                }
            }
        }
        return type
    }

    //判断wifi是否为2.4G 5G
    fun checkWifiGHz(freq: Int): String {
        return if (freq > 2400 && freq < 2500) {
            "Wi-Fi-2.4G"
        } else if (freq > 4900 && freq < 5900) {
            "Wi-Fi-5G"
        } else {
            "Wi-Fi"
        }
    }

    //判断网络是否连接
    fun isNetWorkConnected(context: Context): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivity.activeNetworkInfo
        if (null != info && info.isConnected) if (info.state == NetworkInfo.State.CONNECTED) return true
        //end of if
        //toastHintInfo(context.getString(R.string.net_work_connect_fail))
        return false
    }

    //以流的方式读取asset
    fun getFromAssets(context: Context, fileName: String): String? {
        val stringBuilder = java.lang.StringBuilder()
        try {
            val inputReader = InputStreamReader(context.resources.assets.open(fileName))
            val bufReader = BufferedReader(inputReader)
            var line: String? = ""
            while (bufReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            return stringBuilder.toString()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }
}