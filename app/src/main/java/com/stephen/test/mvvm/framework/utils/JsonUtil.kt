package com.stephen.test.mvvm.framework.utils

import android.text.TextUtils
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

object JsonUtil {
    /**
     *
     * 描述：将对象转化为json.
     */
    fun toJson(src: Any?): String? {
        var json: String? = null
        try {
            val gsonb = GsonBuilder()
            val gson = gsonb.create()
            json = gson.toJson(src)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return json
    }

    /**
     *
     * 描述：将列表转化为json.
     * @param list
     * @return
     */
    fun toJson(list: List<*>?): String? {
        var json: String? = null
        try {
            val gsonb = GsonBuilder()
            val gson = gsonb.create()
            json = gson.toJson(list)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return json
    }

    /**
     *
     * 描述：将json转化为列表.
     * @param json
     * @param typeToken new TypeToken<ArrayList></ArrayList>>() {}.getType();
     * @return
     */
    fun fromJson(json: String?, typeToken: TypeToken<*>): List<*>? {
        var list: List<*>? = null
        try {
            val gsonb = GsonBuilder()
            val gson = gsonb.create()
            val type = typeToken.type
            list = gson.fromJson<List<*>>(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    /**
     *
     * 描述：将json转化为对象.
     * @param json
     * @param clazz
     * @return
     */
    fun fromJson(json: String?, clazz: Class<*>?): Any? {
        if (TextUtils.isEmpty(json)) return null
        var obj: Any? = null
        try {
            if ((clazz as Class).simpleName == "String") return json
            val builder = GsonBuilder()
            //builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
            val gson = builder.create()
            obj = gson.fromJson<Any>(json, clazz)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return obj
    }
}