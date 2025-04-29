package com.voyah.network.response

/**
 * @author jackie wong
 * @date   2023/2/24 13:10
 * @desc   通用数据类
 */
data class DrawingResponse<out T>(
    val result: T?,
    val code: String?,//服务器状态码 这里0表示请求成功
    val msg: String = ""//错误信息
) {

    /**
     * 判定接口返回是否正常
     */
    fun isFailed(): Boolean {
        return code != "0"
    }
}
