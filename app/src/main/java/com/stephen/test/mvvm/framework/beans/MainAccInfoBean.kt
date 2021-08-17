package com.stephen.test.mvvm.framework.beans

data class MainAccInfoBean(
    var `data`: List<Data?>? = null,
    var password: String? = null, // xxxxxx
    var username: String? = null, // xsdxasdasdad
    var version: String? = null // 12345646
) {
    data class Data(
        var country: String? = null, // us
        var icon_url: String? = null, // https://xxxx.com
        var nodes: MutableList<Node?>? = arrayListOf()
    ) {
        data class Node(
            var acct_port: Int? = null, // 122
            var ip: String? = null, // 35.216.3.14
            var name: String? = null, // 节点a-1223
            var ping_port: Int? = null, // 3231
            //自定义赋值的
            var logo_url: String? = null,
            var is_selected: Boolean? = false,
            var node_status: Int = -1,//延迟（0~300为绿色，300~500为橙色，500以上为红色）
            var signal_val: Float = 9999F,
            var belong_country: String? = null
        )
    }
}