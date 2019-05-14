package com.esell.yixinfa

import android.os.Bundle
import android.text.TextUtils
import com.esell.yixinfa.bean.ComponentsBean
import com.esell.yixinfa.bean.ComponentsBean.Companion.InnerType_Back
import com.esell.yixinfa.bean.ComponentsBean.Companion.InnerType_Img
import com.esell.yixinfa.bean.ComponentsBean.Companion.InnerType_Tab
import com.esell.yixinfa.bean.ComponentsBean.Companion.InnerType_Template
import com.esell.yixinfa.bean.ComponentsBean.Companion.InnerType_Video
import com.esell.yixinfa.bean.ComponentsBean.Companion.InnerType_Web
import com.esell.yixinfa.bean.Template
import com.esell.yixinfa.bean.TopicBean
import com.esell.yixinfa.request.HttpReqCofig
import com.esell.yixinfa.request.okhttp.OkhttpManager
import com.esell.yixinfa.request.okhttp.ResponseCache
import com.esell.yixinfa.rxEvents.RxEventTemplateListUpdate
import com.esell.yixinfa.service.PahClient
import com.esell.yixinfa.util.LogUtils
import com.esell.yixinfa.util.OrmUtil
import com.esell.yixinfa.util.rxbus.RxBus
import com.esell.yixinfa.view.BaseTemplateView
import com.esell.yixinfa.view.TemplateView
import com.esell.yixinfa.work.WorkEngine
import com.google.gson.Gson
import io.reactivex.functions.Consumer
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONArray
import java.util.*


class DisplayAdActivity : BaseActivity() {
    lateinit var rTemplateView: TemplateView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rTemplateView = TemplateView(this)
        setContentView(rTemplateView)
        rTemplateView.setOClickInnerButtonListener(object : BaseTemplateView.Companion.ClickInnerButtonListener {
            override fun onClick(bean: ComponentsBean) {
                cancel()
                registerTemplateListUpdate()
                if (bean.interType != InnerType_Back && TextUtils.isEmpty(bean.interTypeVal)) {
                    toast("您没有选择内容，请在后台设置跳转内容")
                    return
                }
                when (bean.interType) {
                    InnerType_Img,InnerType_Video,InnerType_Web -> startActivity<DetailAdActivity>("data" to bean)
                    InnerType_Template -> {
                        if (!bean.validateInterTempleteId()) {
                            toast("您没有选择内容，请在后台设置跳转内容")
                        } else {
                            changeTemplate(bean.getInterTempleteId())
                        }
                    }
                    InnerType_Back -> backTemplate(rTemplateView.getLastTemplateId())
                    InnerType_Tab -> {
                        if (bean.isAuto()) return
                        if (!bean.validateInterTempleteId()) {
                            toast("您没有选择内容，请在后台设置跳转内容")
                        } else {
                            changeTemplate(bean.getInterTempleteId())
                        }

                    }
                }
            }
        })
        registerTemplateListUpdate()
        WorkEngine.templateListUpdate()
        var topicBean: TopicBean? = PahClient.getTopicBean()
        if (null != topicBean) {
            PahClient.connect(topicBean)
        }
    }

    fun registerTemplateListUpdate() {
        add(RxBus.getInstance().register(RxEventTemplateListUpdate::class.java) {
            if (it.list.size > 0) {
                rTemplateView.setTemplate(it.list.get(0))
                autoChange(it.list.get(index = 0))
                WorkEngine.adUpdate()
                if (it.list.size > 1) {
                    //设置其他副屏

                }
            }
        })
    }

    private fun autoChange(template: Template) {
        rTemplateView.removeCallbacks(lAutoChangeRunnable)
        template.components?.let {
            for (i in it) {
                if (i.isInter() && i.isAuto()) {
                    if (i.interCarouselTime > 0) {
                        lAutoChangeRunnable = Runnable { changeTemplate(i.getInterTempleteId()) }
                        rTemplateView.postDelayed(lAutoChangeRunnable, i.interCarouselTime * 1000)
                    }
                }
            }
        }
    }

    private var lAutoChangeRunnable: Runnable? = null

    override fun onDestroy() {
        super.onDestroy()
        rTemplateView.removeCallbacks(lAutoChangeRunnable)
    }

    private fun changeTemplate(templateId: Int) {
        val hashMap = HashMap<String, Any>()
        hashMap["device-uuid"] = OrmUtil.getSaveDataBean().deviceUuid
        hashMap["template-id"] = templateId
        add(OkhttpManager.post().url(HttpReqCofig.URL_GET_CHANGE_CUSTOME_TEMPLATE)
                .params(hashMap)
                .cacheKey(HttpReqCofig.URL_GET_CHANGE_CUSTOME_TEMPLATE + OrmUtil.getSaveDataBean().deviceUuid + templateId)
                .cacheSeconds(Long.MAX_VALUE)
                .callBack(object : OkhttpManager.ResultCallBack<String>() {
                    override fun onFailed(errorMsg: String) {
                        toast("TemplateViewModel onFailed  $errorMsg")
                    }

                    override fun onSuccess(t: String) {
                        val ja = JSONArray(t)
                        if (ja.length() > 0) {
                            try {
                                var template: Template = Gson().fromJson(ja.getJSONObject(0).toString(), Template::class.java)
                                template.init()
                                LogUtils.e("test", "TemplateViewModel  ${ja.getJSONObject(0).toString()}")
                                rTemplateView.changeTemplate(template)
                                autoChange(template)
                                WorkEngine.adUpdate()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }).build().execute())
    }

    private fun backTemplate(templateId: Int) {
        val hashMap = HashMap<String, Any>()
        hashMap["device-uuid"] = OrmUtil.getSaveDataBean().deviceUuid
        hashMap["template-id"] = templateId
        add(OkhttpManager.post().url(HttpReqCofig.URL_GET_CHANGE_CUSTOME_TEMPLATE).params(hashMap)
                .cacheKey(HttpReqCofig.URL_GET_CHANGE_CUSTOME_TEMPLATE + OrmUtil.getSaveDataBean().deviceUuid + templateId)
                .cacheSeconds(Long.MAX_VALUE).callBack(object : OkhttpManager.ResultCallBack<String>() {
                    override fun onSuccess(t: String) {
                        val ja = JSONArray(t)
                        if (ja.length() > 0) {
                            try {
                                var template: Template = Gson().fromJson(ja.getJSONObject(0).toString(), Template::class.java)
                                template.init()
                                LogUtils.e("test", "TemplateViewModel  ${ja.getJSONObject(0).toString()}")
                                rTemplateView.backTemplate(template)
                                autoChange(template)
                                WorkEngine.adUpdate()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    override fun onFailed(errorMsg: String) {
                        toast("TemplateViewModel onFailed  $errorMsg")
                    }
                }).build().execute())
    }
}