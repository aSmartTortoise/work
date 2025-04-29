package com.voyah.voice.main.service

import android.content.Context
import com.blankj.utilcode.util.LogUtils
import com.voyah.common.service.IChatService

/**
 *  author : jie wang
 *  date : 2024/4/28 15:39
 *  description :
 */

class ChatService : IChatService {

    override fun onGetChatContent(content: String) {
        LogUtils.d("onGetChatContent content:$content")
    }

    override fun init(context: Context?) {
        LogUtils.d("init.")
    }
}