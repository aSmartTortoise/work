package com.voyah.cockpit.window.util

import android.util.Log

class ParamUtil {
    inline fun <reified T> T.getCallerInfo(): String {
        val stackTrace = Thread.currentThread().stackTrace
        // 0: getStackTrace, 1: currentThread, 2: getCallerInfo, 3: 调用者
        val caller = stackTrace[3] ?: return "无法获取调用信息"
        val kClass = this!!::class

        val method = caller.methodName

        Log.i("ParamUtil", "Caller class: ${caller.className}, method: $method")
        return ""
    }
}