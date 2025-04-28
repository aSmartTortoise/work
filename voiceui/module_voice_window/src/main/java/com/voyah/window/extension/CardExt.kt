package com.voyah.window.extension

import com.blankj.utilcode.util.LogUtils
import com.voyah.cockpit.window.model.DomainType

/**
 *  author : jie wang
 *  date : 2024/11/23 16:46
 *  description :
 */

fun isChatCard(domainType: String?): Boolean {
    LogUtils.d("isChatCard, domainType: $domainType")
    val chatCardFlag = when (domainType) {
        DomainType.DOMAIN_TYPE_GOSSIP,
        DomainType.DOMAIN_TYPE_FAQ,
        DomainType.DOMAIN_TYPE_CAR_ENCYCLOPEDIA,
        DomainType.DOMAIN_TYPE_ENCYCLOPEDIA -> {
            true
        }

        else -> false
    }
    LogUtils.d("isChatCard, chatCardFlag: $chatCardFlag")
    return chatCardFlag
}

fun supportDeepSeek(domainType: String?):Boolean{
    LogUtils.d("isChatCard, domainType: $domainType")
    val chatCardFlag = when (domainType) {
        DomainType.DOMAIN_TYPE_GOSSIP,
        DomainType.DOMAIN_TYPE_ENCYCLOPEDIA -> {
            true
        }

        else -> false
    }
    LogUtils.d("isChatCard, chatCardFlag: $chatCardFlag")
    return chatCardFlag
}