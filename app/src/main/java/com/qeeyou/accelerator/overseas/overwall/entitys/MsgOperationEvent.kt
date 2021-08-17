package com.qeeyou.accelerator.overseas.overwall.entitys

/**
 * Created By Stephen on 2020/4/22 13:53
 */
//eventBus操作事件标识
data class MsgOperationEvent(var flag: Int, var param0: Any? = null, var param1: Any? = null, var param2: Any? = null,
                             var param3: Any? = null, var param4: Any? = null, var param5: Any? = null, var argForInt: Int? = -1){
    companion object {
        const val MsgSwitchAccModel = 1//切换加速模式,附带节点
        const val MsgAccIntervalData = 2//加速定时器数据
    }
}