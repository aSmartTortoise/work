package com.voyah.network.interceptor

import com.blankj.utilcode.util.LogUtils
import com.voyah.network.constant.KEY_LOGIN
import com.voyah.network.constant.KEY_SET_COOKIE
import com.voyah.network.manager.CookiesManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author jackie wong
 * @date   2023/3/27 07:26
 * @desc   Cookies拦截器
 */
class CookiesInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newBuilder = request.newBuilder()

        val response = chain.proceed(newBuilder.build())
        val url = request.url().toString()
        val host = request.url().host()

        // set-cookie maybe has multi, login to save cookie
        if ((url.contains(KEY_LOGIN))
                && response.headers(KEY_SET_COOKIE).isNotEmpty()
        ) {
            val cookies = response.headers(KEY_SET_COOKIE)
            val cookiesStr = CookiesManager.encodeCookie(cookies)
            CookiesManager.saveCookies(cookiesStr)
            LogUtils.d("CookiesInterceptor:cookies:$cookies")
        }
        return response
    }
}