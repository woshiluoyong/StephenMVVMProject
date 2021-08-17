package com.stephen.test.mvvm.framework.beans

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.stephen.test.mvvm.framework.R

data class ResultBean(val state: Int, val acceleratedCountry: String? = "", val acceleratedTimeStr: String? = "") :
    Parcelable {
    companion object {
        const val STATE_CONNECTED = 1 //vpn连接成功
        const val STATE_DISCONNECTED = 2 //断开vpn连接
        const val STATE_CONNECT_FAILED = 3 //连接成功
        const val STATE_SWITCH_CONNECT = 4 //切换vpn连接

        @JvmField
        val CREATOR = object : Creator<ResultBean> {
            override fun createFromParcel(source: Parcel): ResultBean {
                return ResultBean(source)
            }

            override fun newArray(size: Int): Array<ResultBean?> {
                return arrayOfNulls<ResultBean>(size)
            }
        }
    }

    constructor(source: Parcel) : this(source.readInt(), source.readString(), source.readString())

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.run {
            writeInt(state)
            writeString(acceleratedCountry)
            writeString(acceleratedTimeStr)
        }
    }

    val getStateIconRes: Int by lazy {
        when (state) {
            STATE_CONNECTED -> {
                R.mipmap.ic_result_connected
            }
            STATE_DISCONNECTED, STATE_SWITCH_CONNECT -> {
                R.mipmap.ic_result_disconnected
            }
            else -> {
                R.mipmap.ic_result_connect_failed
            }
        }
    }

    val getStateStrRes: Int by lazy {
        when (state) {
            STATE_CONNECTED -> {
                R.string.vpn_connected
            }
            STATE_DISCONNECTED, STATE_SWITCH_CONNECT -> {
                R.string.vpn_disconnected
            }
            else -> {
                R.string.vpn_connect_failed
            }
        }
    }

    fun getDescStr(context: Context): String {
        return when (state) {
            STATE_CONNECTED -> {
                context.getString(R.string.connected_country, acceleratedCountry)
            }
            STATE_DISCONNECTED, STATE_SWITCH_CONNECT -> {
                context.getString(R.string.accelerated_time, acceleratedTimeStr)
            }
            else -> {
                context.getString(R.string.re_accelerate_after_changing_line)
            }
        }
    }
}
