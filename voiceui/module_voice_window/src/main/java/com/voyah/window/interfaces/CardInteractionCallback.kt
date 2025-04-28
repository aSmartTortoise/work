package com.voyah.window.interfaces

import android.view.View

/**
 *  author : jie wang
 *  date : 2024/8/22 11:02
 *  description :
 */
interface CardInteractionCallback {

    fun onScrollStateChange(view: View, newState: Int, domainType: String, sessionId: String?)

    fun onPageSelected(view: View, position: Int, domainType: String)
}