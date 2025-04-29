package com.voyah.aiwindow.ui.widget.vpa

import android.view.View

object VpaWindowConfig {

    //是否是隐私模式
    @Volatile
    var isPrivacySafeMode = true

    fun getVpaExpandWidth(): Int {
        return 900
    }

    fun getVpaInitWidth(): Int {
        return 304
    }
}


class SkillParams {
    var tag: String? = null
    var w: Int? = null
    var h: Int? = null
    var view: View? = null
}