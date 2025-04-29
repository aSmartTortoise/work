package com.voyah.common.service

import com.alibaba.android.arouter.facade.template.IProvider

/**
 *  author : jie wang
 *  date : 2024/4/28 14:25
 *  description :
 */
interface IChatService : IProvider {

    fun onGetChatContent(content: String)
}