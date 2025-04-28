package com.voyah.window.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import java.util.*

/**
 *  author : jie wang
 *  date : 2024/2/28 17:18
 *  description :
 */
class TypewriterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
    ) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var content: String? = null
    private var typeTimer: Timer? = null
    private var typeTimeDelay: Int = TYPE_TIME_DELAY // 打字间隔

    var typeListener: OnTypeViewListener? = null


    companion object {
        const val TYPE_TIME_DELAY = 100
        const val TAG = "TypewriterView"

    }

    fun start(content: String, typeTimeDelay: Int = TYPE_TIME_DELAY) {
        if (TextUtils.isEmpty(content) || typeTimeDelay < 0) {
            return
        }
        text = ""
        post {
            this.content = content
            this.typeTimeDelay = typeTimeDelay
            text = ""
            startTypeTimer()
            typeListener?.onTypeStart()
        }
    }

    private fun startTypeTimer() {
        stopTypeTimer()
        typeTimer = Timer().apply {
            schedule(TypeTimerTask(), typeTimeDelay.toLong())
        }
    }

    private fun stopTypeTimer() {
        typeTimer?.cancel()
        typeTimer = null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopTypeTimer()
    }


    inner class TypeTimerTask : TimerTask() {
        override fun run() {
            post {
                val length = content?.length ?: 0
                if (text.toString().length < length) {
                    val currentText = content?.substring(0, text.toString().length + 1)
                    text = currentText
                    typeListener?.onTextChange(currentText as CharSequence)
                    startTypeTimer()
                } else {
                    stopTypeTimer()
                    typeListener?.onTypeOver()
                }
            }
        }
    }

    interface OnTypeViewListener {
        fun onTypeStart()
        fun onTypeOver()
        fun onTextChange(s: CharSequence)
    }

}