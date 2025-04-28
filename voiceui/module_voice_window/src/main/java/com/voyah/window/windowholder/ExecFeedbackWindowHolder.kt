package com.voyah.window.windowholder

import android.content.Context
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.LogUtils
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.voyah.cockpit.window.model.ExecuteFeedbackInfo
import com.voyah.cockpit.window.model.VoiceLocation
import com.voyah.cockpit.window.model.WindowType
import com.voyah.window.R

/**
 *  author : jie wang
 *  date : 2024/3/28 10:57
 *  description :
 */
class ExecFeedbackWindowHolder(context: Context) : BaseWindowHolder(context) {

    private val windowWidth: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_366)
    }

    private val windowMargin: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_64)
    }

    var feedbackInfo: ExecuteFeedbackInfo? = null

    private var tvText: TextView? = null
    private var ivTag: ImageView? = null

    override fun onVoiceAwake(voiceServiceMode: Int, voiceLocation: Int) {
    }

    override fun onVoiceListening() {

    }

    override fun getLayoutRes() = R.layout.window_exec_feedback

    override fun isWindowShow(): Boolean {
        var windowType = WindowType.WINDOW_TYPE_FEEDBACK_FRONT_LEFT
        when (feedbackInfo?.location) {
            VoiceLocation.FRONT_RIGHT,
            VoiceLocation.REAR_LEFT,
            VoiceLocation.REAR_RIGHT -> {
                windowType = WindowType.WINDOW_TYPE_FEEDBACK_FRONT_RIGHT
            }
        }
        return EasyFloat.isShow(windowType)
    }

    override fun showWindow() {
        LogUtils.d("showWindow")
        if (feedbackInfo == null) return
        var x = windowMargin
        var windowType = WindowType.WINDOW_TYPE_FEEDBACK_FRONT_LEFT
        when (feedbackInfo?.location) {
            VoiceLocation.FRONT_RIGHT,
            VoiceLocation.REAR_LEFT,
            VoiceLocation.REAR_RIGHT -> {
                x = screenWidth - windowWidth - windowMargin
                windowType = WindowType.WINDOW_TYPE_FEEDBACK_FRONT_RIGHT
            }
        }
        EasyFloat.with(context)
            .setTag(windowType)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setImmersionStatusBar(true)
            .setLocation(x, 0)
            .setDragEnable(false)
            .setAnimator(null)
            .setTouchable(false)
            .setLayout(getLayoutRes()) { view ->
                tvText = view.findViewById<TextView>(R.id.tv_text).apply {
                    ellipsize = TextUtils.TruncateAt.START
                    isSingleLine = true
                    text = feedbackInfo?.text
                }
                ivTag = view.findViewById<ImageView>(R.id.iv_tag).apply {
                    setImageResource(
                        if (feedbackInfo?.isEnable == true) R.drawable.icon_exec_feedback_yes
                        else R.drawable.icon_exec_feedback_no
                    )
                }
            }
            .registerCallback {

                show {
                    LogUtils.d("execute feedback show.")
                }

                dismiss {
                    LogUtils.d("execute feedback dismiss.")
                }
            }
            .show()
    }

    override fun onVoiceSpeaking() {

    }

    override fun onVoiceExit() {
        LogUtils.d("onVoiceExit")
        EasyFloat.dismiss(WindowType.WINDOW_TYPE_FEEDBACK_FRONT_LEFT, true)
        EasyFloat.dismiss(WindowType.WINDOW_TYPE_FEEDBACK_FRONT_RIGHT, true)
    }

    fun dismissWindow(voiceLocation: Int) {
        LogUtils.d("dismissWindow voiceLocation:$voiceLocation")
        when (voiceLocation) {
            VoiceLocation.FRONT_LEFT -> {
                EasyFloat.dismiss(WindowType.WINDOW_TYPE_FEEDBACK_FRONT_LEFT, true)
            }

            VoiceLocation.FRONT_RIGHT,
            VoiceLocation.REAR_LEFT,
            VoiceLocation.REAR_RIGHT -> {
                EasyFloat.dismiss(WindowType.WINDOW_TYPE_FEEDBACK_FRONT_RIGHT, true)
            }
        }
    }
}