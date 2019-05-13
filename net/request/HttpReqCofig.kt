package com.esell.yixinfa.request


import com.esell.yixinfa.util.LogUtils
import com.esell.yixinfa.util.OrmUtil

object HttpReqCofig {
        object Host{
            val host:String = if (LogUtils.isDebug) {
                "http://dev-api3.yixinfa.cn"
            }else{
                "http://api7.yixinfa.cn"
            }
        }
        //    #易新发对应yxf；中性版对应neu；天使盒子对应angle_box
        var SPLASH_SUFFIX = "yxf"
        var URL_GET_DEVICE_AD = "${Host.host}/ad/list.shtml"
        var URL_DEVICE_REGISTER = "${Host.host}/register.shtml"
        var URL_DEVICE_ACTIVATE = "${Host.host}/activate.shtml"
        var URL_GET_CUSTOM_TEMPLATE = "${Host.host}/template/list.shtml"
        var URL_GET_CHANGE_CUSTOME_TEMPLATE = "${Host.host}/template/change.shtml"   //互动按钮模板
        var URL_GET_ButtonContent = "${Host.host}/get/button/content.shtml"   //用汇新濠哥点击按钮后获取图片内容
        var URL_INDEX_REPORT_FILE_DOWNLOAD_STATUS = "${Host.host}/ad/download/status.shtml"//下载状态上报
        var URL_INDEX_UPDATE = "${Host.host}/version.shtml"// 升级
        var URL_INDEX_GET_WEATHER = "${Host.host}/weather.shtml" //获取天气
        var URL_DEVICELOG = "${Host.host}/deviceLog/add.shtml" //日志上报

    val ButtonContentCacheKey = URL_GET_ButtonContent+ OrmUtil.getSaveDataBean().deviceUuid
}
