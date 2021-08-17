#不混淆models 类
-keep class com.qeeyou.accelerator.overseas.overwall.beans.** {*;}
-keep class com.qeeyou.accelerator.overseas.overwall.entitys.** {*;}

-keepattributes SourceFile,LineNumberTable

# 移除打包后相关日志字符串
-assumenosideeffects class com.qeeyou.accelerator.overseas.overwall.utils.AppLogger{
 public *** v(...);
 public *** d(...);
 public *** i(...);
 public *** w(...);
 public *** e(...);
 public *** t(...);
}

#vpn
-keep class net.qyvpn.vpn.** {*;}
-keep class com.qeeyou.qyvpn.** {*;}
-keep class net.qyproxy.vpn.** {*;}
-keep class com.qeeyou.qyproxy.** {*;}

-keep class com.orhanobut.hawk.** { *; }