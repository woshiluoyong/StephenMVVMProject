package com.stephen.test.mvvm.framework

import android.app.Activity
import android.os.Bundle
import androidx.multidex.MultiDexApplication
import com.orhanobut.hawk.Hawk
import com.stephen.test.mvvm.framework.activitys.MainActivity
import com.stephen.test.mvvm.framework.utils.LanguageManager
import com.stephen.test.mvvm.framework.utils.LanguageUtils

class MyApplication : MultiDexApplication() {
    companion object {
        lateinit var app: MyApplication

        var curMainActivity: MainActivity? = null
        var currentActivity: Activity? = null
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        Hawk.init(this).build();
        LanguageManager.instance.init(this)

        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    private var activityStartedCount = 0 //前后台切换判断使用

    private val activityLifecycleCallbacks by lazy {
        object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                // 设置语言
                val targetLanCode = LanguageManager.instance.checkAutoAndSupport(LanguageManager.instance.getSavedAppLanCode())
                LanguageUtils.applyLanguage(activity, LanguageUtils.getLocaleByCode(targetLanCode), false)
            }

            override fun onActivityStarted(activity: Activity) {
                activityStartedCount++
                if(activityStartedCount == 1)println("======Stephen===========>恢复到前台")
            }

            override fun onActivityResumed(activity: Activity) {
                currentActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {
                activityStartedCount--
                if(activityStartedCount == 0)println("======Stephen===========>回退到后台")
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        }
    }
}