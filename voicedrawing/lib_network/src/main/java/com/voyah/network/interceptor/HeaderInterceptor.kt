package com.voyah.network.interceptor

import com.blankj.utilcode.util.LogUtils
import com.voyah.ds.common.auth.DsAuthUtil
import com.voyah.network.constant.COLLECTION_WEBSITE
import com.voyah.network.constant.KEY_COOKIE
import com.voyah.network.manager.CookiesManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author jackie wong
 * @date   2023/3/27 07:25
 * @desc   头信息拦截器
 * 添加头信息
 */
class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newBuilder = request.newBuilder()
//        newBuilder.addHeader("Content-type", "application/json; charset=utf-8")

        val host = request.url().host()
        val url = request.url().toString()

        //给有需要的接口添加Cookies
        if (!host.isNullOrEmpty() && url.contains("/ds/v1/api/drawing/times")) {
            val headerMap = DsAuthUtil.authHeaders("/ds/v1/api/drawing/times", "GET")
            if (headerMap != null) {
                for ((key, value) in headerMap) {
                    newBuilder.addHeader(key, value)
                }
            }
        }
        return chain.proceed(newBuilder.build())
    }
}