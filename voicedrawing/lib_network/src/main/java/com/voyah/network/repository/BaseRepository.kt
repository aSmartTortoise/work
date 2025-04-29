package com.voyah.network.repository

import com.voyah.network.error.ApiException
import com.voyah.network.response.BaseResponse
import com.voyah.network.response.DrawingResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

/**
 * @author jackie wong
 * @date   2023/2/23 23:31
 * @desc   基础仓库
 */
open class BaseRepository {

    /**
     * IO中处理请求
     */
    suspend fun <T> requestResponse(requestCall: suspend () -> BaseResponse<T>?): T? {
        val response = withContext(Dispatchers.IO) {
            withTimeout(30 * 1000) {
                requestCall()
            }
        } ?: return null

        if (response.isFailed()) {
            throw ApiException(response.statusCode, response.errorMsg)
        }
        return response.data
    }


    suspend fun <T> executeRequest(requestCall: suspend () -> DrawingResponse<T>?): T? {
        val response = withContext(Dispatchers.IO) {
                requestCall()
        } ?: return null

        if (response.isFailed()) {
            throw ApiException(response.code?.toInt() ?: 1080, response.msg)
        }
        return response.result
    }
}