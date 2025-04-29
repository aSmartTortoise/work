package com.voyah.network.manager

import com.voyah.network.api.ApiInterface


/**
 * @author jackie wong
 * @date   2023/2/27 21:14
 * @desc   API管理器
 */
object ApiManager {
    val api by lazy(LazyThreadSafetyMode.NONE) { HttpManager.create(ApiInterface::class.java) }
}