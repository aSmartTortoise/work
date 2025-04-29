package com.voyah.aiwindow.ui.container

import android.content.Context
import android.view.View
import android.view.WindowManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import java.util.*

class LiteWindowManager {
    companion object {
        private val windowStack: HashMap<String, Builder> = HashMap();
        fun with(context: Context, tag: String): Builder {
            val value = windowStack[tag]
            LogUtils.w("window exist: $value")
            if (value == null) {
                val builder = Builder(context)
                windowStack[tag] = builder
                return builder
            }

            return value;
        }

        fun getWindow(tag: String): Builder? {
            return windowStack[tag]
        }

        fun dismiss(tag: String) {
            LogUtils.v("dismiss($tag)")
            val windowManager = Utils.getApp().getSystemService(WindowManager::class.java)
            windowStack[tag]?.voiceWindow?.rootLayout?.let {
                if (it.isAttachedToWindow) windowManager.removeView(it) else LogUtils.e("dismiss view not attach")
            }
            windowStack.remove(tag)
        }

        fun hide(tag: String) {
            LogUtils.v("hide($tag)")
            windowStack[tag]?.voiceWindow?.rootLayout?.let {
                it.visibility = View.GONE
            }
        }
    }


    fun dismissAll(context: Context) {
        val windowManager = context.getSystemService(WindowManager::class.java)

        for ((k, v) in windowStack) {
            windowManager.removeView(v.voiceWindow.rootLayout)
        }
        windowStack.clear()
    }
}

class Builder constructor(ctx: Context) {
    var voiceWindow: LiteWindow
    var context = ctx

    init {
        LogUtils.i("Builder init")
        voiceWindow = LiteWindow(context)
    }

    fun setLayoutParams(ct: WindowManager.LayoutParams): Builder {
        voiceWindow.updateParams(ct)
        return this
    }

    fun setLayoutId(id: Int): Builder {
        voiceWindow.setView(id)
        return this
    }

    fun setView(view: View): Builder {
        voiceWindow.setView(view)
        return this
    }

    fun setWindowType(type: Int): Builder {
        voiceWindow.setType(type)
        return this
    }

    fun setDisplayId(displayId: Int): Builder {
        voiceWindow.setDisplayId(displayId)
        return this
    }

    fun setLocation(x: Int, y: Int): Builder {
        voiceWindow.setLocation(x, y)
        return this
    }

    fun updateLayoutParams(ct: WindowManager.LayoutParams) {
        voiceWindow.updateParams(ct)
        voiceWindow.update()
    }

    fun show(callback: FlowCallback?) {
        LogUtils.d("show() called with: callback = $callback")
        if (!voiceWindow.hasAttach() || voiceWindow.displayIdChange) {
            voiceWindow.show(callback)
        } else {
            if (!voiceWindow.hasVisible()) {
                voiceWindow.setWindowVisible(true)
            }
            voiceWindow.update()
        }
    }

    fun setVisible(vis: Boolean) {
        voiceWindow.setWindowVisible(vis)
    }

    fun isVisible(): Boolean {
        return voiceWindow.hasVisible()
    }

    fun upWindowLayer(up: Boolean) {
        voiceWindow.setWindowType(
                if (up) WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
                else WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        )
    }

    fun setWidthAndHeight(width: Int, height: Int): Builder {
        voiceWindow.setWidthAndHeight(width, height)
        return this
    }

    fun setGravity(gravity: Int): Builder {
        voiceWindow.setGravity(gravity)
        return this
    }

    fun setIntercept(intercept: Boolean): Builder {
        voiceWindow.setIntercept(intercept)
        return this
    }

}