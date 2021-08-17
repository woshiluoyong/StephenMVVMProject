package com.stephen.test.mvvm.framework.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.telephony.TelephonyManager
import java.nio.charset.StandardCharsets
import java.util.*

class DeviceUuidFactory {
    protected val PREFS_FILE = "device_id.xml"
    protected val PREFS_DEVICE_ID = "device_id"
    protected var uuid: UUID? = null

    companion object {
        private var mInstance: DeviceUuidFactory? = null

        @JvmStatic
        @Synchronized
        fun getInstance(): DeviceUuidFactory {
            if (mInstance == null) {
                mInstance = DeviceUuidFactory()
            }
            return mInstance!!
        }
    }

    @SuppressLint("HardwareIds")
    fun getDeviceUuid(context: Context?): UUID? {
        if (uuid == null && null != context) {
            synchronized(DeviceUuidFactory::class.java) {
                if (uuid == null) {
                    val prefs = context.getSharedPreferences(PREFS_FILE, 0)
                    val id = prefs.getString(PREFS_DEVICE_ID, null)
                    if (id != null) { // Use the ids previously computed and stored in the prefs file
                        uuid = UUID.fromString(id)
                    } else {
                        val androidId = Settings.Secure.getString(
                            context.contentResolver,
                            Settings.Secure.ANDROID_ID
                        )
                        uuid = if ("9774d56d682e549c" != androidId) {
                            UUID.nameUUIDFromBytes(androidId.toByteArray(StandardCharsets.UTF_8))
                        } else {
                            @SuppressLint("MissingPermission") val deviceId =
                                (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).deviceId
                            if (deviceId != null) UUID.nameUUIDFromBytes(
                                deviceId.toByteArray(StandardCharsets.UTF_8)
                            ) else UUID.randomUUID()
                        }
                        // Write the value out to the prefs file
                        prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString()).apply()
                    }
                }
            }
        }
        return uuid
    }
}