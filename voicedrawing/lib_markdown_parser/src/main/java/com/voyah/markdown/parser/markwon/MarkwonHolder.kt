package com.voyah.markdown.parser.markwon

import com.voyah.voice.framework.helper.AppHelper
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImagesPlugin

/**
 *  author : jie wang
 *  date : 2024/5/15 15:19
 *  description :
 */
class MarkwonHolder private constructor() {

    val markwon by lazy(LazyThreadSafetyMode.NONE) {
        Markwon.builder(AppHelper.getApplication())
            .usePlugin(HtmlPlugin.create { plugin ->
                plugin.addHandler(
                    DetailsTagHandler()
                )
            })
            .usePlugin(ImagesPlugin.create())
            .build()
    }

    companion object {
        fun getInstance(): MarkwonHolder = Holder.instance
    }

    private object Holder {
        val instance = MarkwonHolder()
    }
}