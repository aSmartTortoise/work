package com.voyah.window.windowholder

import android.content.Context
import android.view.View
import android.view.WindowManager
import com.blankj.utilcode.util.LogUtils
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.interfaces.OnInvokeView
import com.voyah.cockpit.window.model.ScreenType
import com.voyah.cockpit.window.model.VoiceLocation
import com.voyah.cockpit.window.model.VoiceState
import com.voyah.cockpit.window.model.WindowType
import com.voyah.voice.common.manager.MegaDisplayHelper
import com.voyah.voice.framework.helper.AppHelper
import com.voyah.window.R
import com.voyah.window.anim.FadeInOutAnimator
import com.voyah.window.manager.CardDataRepo

/**
 *  author : jie wang
 *  date : 2024/3/8 16:23
 *  description : vpa、typewriter、card 悬浮窗 的holder。
 */
class CeilingScreenWindowHolder(context: Context): BaseVTCWindowHolder(context) {

    override fun getCardWindowTag() = WindowType.WINDOW_TYPE_CARD_CEILING_SCREEN

    override fun getVPATypeWindowTag() = WindowType.WINDOW_TYPE_VPA_TYPE_CEILING_SCREEN

    override fun getDisplayId() = MegaDisplayHelper.getVoiceDisplayId(VoiceLocation.REAR_LEFT)

    override fun showCardWindow(showVA: Boolean) {
        super.showCardWindow(showVA)
        val x = getVPATypeX()
        LogUtils.d("showCardWindow x:$x, showVA:$showVA")
        EasyFloat.with(context)
            .setTag(getCardWindowTag())
            .setShowPattern(ShowPattern.ALL_TIME)
            .setImmersionStatusBar(true)
            .setLocation(x, windowTop)
            .setDragEnable(false)
            .setAnimator(null)
            .setWindowParamsType(WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW + 21)
            .setDisplayId(getDisplayId())
            .setLayout(R.layout.window_vpa_typewriter_card, object : OnInvokeView {
                override fun invoke(view: View) {
                    LogUtils.d("card root view onLayout, voiceState:$voiceState")
                    if (voiceState == VoiceState.VOICE_STATE_EXIT) {
                        LogUtils.d("card root view onLayout, not show vpa, and dismiss card window.")
                        EasyFloat.dismiss(getCardWindowTag(), true)
                    } else {
                        initCardView(view)
                        LogUtils.d("card root view onLayout, showCardFlag:$showCardFlag, " +
                                "cardInfoToDisplay:$cardInfoToDisplay")
                        if (showCardFlag && cardInfoToDisplay != null) {
                            tryExpandCard(cardInfoToDisplay!!)
                        }

                        if (showVA) {
                            showVAWindow()
                        }
                    }
                }

            })
            .registerCallback {
                createResult { b, s, _ ->
                    LogUtils.d("createResult card window is success:$b, reason:$s")
                }

                show {
                    LogUtils.d("show card window.")
                }

                dismiss {
                    LogUtils.d("card window dismiss.")
                }

                outsideTouch { _, _ ->
                    LogUtils.d("outsideTouch，click window outside.")
                    CardDataRepo.remove(ScreenType.CEILING)
                    recoverWindowSmall(true)
                }
            }
            .show()

    }

    override fun showVAWindow() {
        val x = getVPATypeX()
        LogUtils.d("showVAWindow x:$x")
        EasyFloat.with(context)
            .setTag(getVPATypeWindowTag())
            .setShowPattern(ShowPattern.ALL_TIME)
            .setImmersionStatusBar(true)
            .setLocation(x, windowTop)
            .setDragEnable(false)
            .setAnimator(FadeInOutAnimator())
            .setWindowParamsType(WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW + 21)
            .setDisplayId(getDisplayId())
            .setLayout(R.layout.window_vpa_asr, object : OnInvokeView {
                override fun invoke(view: View) {
                    LogUtils.d("vpa type window layout.")
                    initVAView(view)
                    onVoiceFocusChange(AppHelper.locationVoiceFocus == VoiceLocation.FRONT_RIGHT)
                }

            })
            .registerCallback {
                createResult { b, s, _ ->
                    LogUtils.d("vpa type window create result is success:$b, reason:$s")
                }

                show {
                    LogUtils.d("vpa type window showed, showCardFlag:$showCardFlag, " +
                            "cardInfoToDisplay:$cardInfoToDisplay")
                    if (showCardFlag && cardInfoToDisplay != null) {
                        tryExpandCard(cardInfoToDisplay!!)
                    }
                }

                dismiss {
                    LogUtils.d("va window dismiss.")
                    onVpaTypeWindowDismiss()
                }
            }
            .show()
    }

    override fun setVoiceFocusLocation() {
        super.setVoiceFocusLocation()
        onVoiceFocusChange(AppHelper.locationVoiceFocus >= VoiceLocation.REAR_LEFT)
    }


}