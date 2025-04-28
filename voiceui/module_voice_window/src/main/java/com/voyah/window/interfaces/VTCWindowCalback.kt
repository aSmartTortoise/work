package com.voyah.window.interfaces

/**
 *  author : jie wang
 *  date : 2024/7/25 20:15
 *  description :
 */
interface VTCWindowCallback {

    fun onVTCDismiss()
    fun onCardCollapse(domainType: String?, sessionId: String?)

    fun onDomainItemClick(position: Int, viewType: Int, action: String?)

    fun onCardCanScroll(cardType: String, direction: Int, canScroll: Boolean)

    fun interruptStreamInput(domainType: String)

    fun onInteractionInCard(domainType: String?, sessionId: String?)

    fun onVPATypeInit()

}