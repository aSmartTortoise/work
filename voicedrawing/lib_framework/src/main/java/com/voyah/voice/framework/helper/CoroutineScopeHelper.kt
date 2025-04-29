package com.voyah.voice.framework.helper

import android.content.Context
import com.voyah.voice.framework.ext.normalScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 *  author : jie wang
 *  date : 2024/4/25 16:44
 *  description :
 */
class CoroutineScopeHelper private constructor() {

    companion object {
        fun getInstance() = Holder.getInstance()
    }

    private object Holder {
        fun getInstance() = CoroutineScopeHelper()
    }

    var normalScope: CoroutineScope? = null

    init {
        normalScope = CoroutineScope(Dispatchers.Main.immediate)
    }
}