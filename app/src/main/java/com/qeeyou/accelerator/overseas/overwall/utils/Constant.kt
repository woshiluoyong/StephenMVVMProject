package com.qeeyou.accelerator.overseas.overwall.utils

import com.qeeyou.accelerator.overseas.overwall.BuildConfig

//配置信息
object Constant {
    const val ParamBase = "ParamBase"
    const val ParamIndex = "ParamIndex"
    const val ParamTitle = "ParamTitle"
    const val ParamUpdateTitle = "ParamUpdateTitle"
    const val ParamBool = "ParamBoolean"
    const val ParamBundle = "ParamBundle"
    const val ParamObj1 = "ParamObj1"
    const val ParamObj2 = "ParamObj2"
    const val ParamObj3 = "ParamObj3"
    const val ParamObj4 = "ParamObj4"
    const val ParamObj5 = "ParamObj5"
    const val ParamObj6 = "ParamObj6"
    const val EXTRA_IS_FROM_LAUNCHER = "extra_is_from_launcher" // Intent参数，是否来自桌面

    const val SERVER_URL = BuildConfig.BASE_URL //服务器地址
    const val BASE_URL_GP_APP = "https://play.google.com/store/apps/details?id=%s"

    const val ReqCode_AccResult = 100

    const val MODE_AUTO = 1 //连接协议模式auto
    const val MODE_SS = 2 //连接协议模式ss
    const val MODE_UDP = 3 //连接协议模式udp
    const val MODE_TCP = 4 //连接协议模式tcp

    const val USER_ACC_HAND_MODE = "USER_ACC_HAND_MODE"// 用户当前手动模式(否则为默认的自动模式)
    const val ACC_BEFORE_IP_INFO = "ACC_BEFORE_IP_INFO"// 加速前ip信息
    const val ACC_AFTER_IP_INFO = "ACC_AFTER_IP_INFO"// 加速后ip信息

    const val KEY_VPN_PROTOCOL_MODE = "vpn_protocol_mode"

}