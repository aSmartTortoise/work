package com.voyah.aiwindow.ui.container

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inspector.WindowInspector
import androidx.core.view.isVisible
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ScreenUtils
import com.voyah.aiwindow.common.AntiShake
import java.lang.Exception

@SuppressLint("ClickableViewAccessibility")
class LiteWindow(context: Context) {

    lateinit var windowManager: WindowManager
    var params: WindowManager.LayoutParams? = null
    var callback: FlowCallback? = null
    var rootLayout: LiteRootLayout
    var id = Display.DEFAULT_DISPLAY
    var mContext: Context
    var displayIdChange = false
    private val obj = Any()
    var attaching = false

    // 获取浮窗所在的矩形
    val screenHeight by lazy {
        ScreenUtils.getScreenHeight()
    }

    init {
        LogUtils.i("LiteWindow init()")
        mContext = context
        changeDisplayEnv()
        initLayoutParams()
        rootLayout = LiteRootLayout(context)
        rootLayout.setVisibleChangeCall { visibility ->
            callback?.onVisible(rootLayout, visibility)
        }
    }

    private fun changeDisplayEnv() {
        LogUtils.i("changeDisplayEnv($id)")
        val display: Display =
                mContext.getSystemService(DisplayManager::class.java).getDisplay(id)
        mContext = mContext.createDisplayContext(display)
        windowManager = mContext.getSystemService(WindowManager::class.java)
    }

    fun updateParams(par: WindowManager.LayoutParams?) {
        params = par
    }

    fun setView(view: View) {
        rootLayout.setView(view)
    }

    fun setView(id: Int) {
        rootLayout.setView(id)
    }

    fun show(cal: FlowCallback?) {
        callback = cal
        when (params) {
            null -> {
                initLayoutParams()
            }
        }

        if (this::windowManager.isInitialized && (rootLayout.isAttachedToWindow || rootLayout.windowToken != null)) {
            LogUtils.i("removeViewImmediate()")
            try {
                windowManager.removeViewImmediate(rootLayout) // 使用removeView 后, isAttachedToWindow 还是返回true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (displayIdChange) {
            changeDisplayEnv()
        }

        val contains = WindowInspector.getGlobalWindowViews().contains(rootLayout)
        LogUtils.w("show($contains)")
        if (!contains) {
            rootLayout.removeOnAttachStateChangeListener(value)
            rootLayout.addOnAttachStateChangeListener(value)
            windowManager.addView(rootLayout, params)
        } else {
            callback?.onAttach(rootLayout.childView)
        }
    }


    private val value = object : View.OnAttachStateChangeListener {

        override fun onViewAttachedToWindow(v: View?) { //回调可能要几百ms
            val childView = rootLayout.childView
            LogUtils.i("onViewAttachedToWindow, childView=$childView")
            if (AntiShake.check(obj, 10)) {
                LogUtils.i("onViewAttachedToWindow antiShake!")
                return
            }
            callback?.onAttach(childView)

            rootLayout.setOnClickListener {
                callback?.onClick()
            }
        }

        override fun onViewDetachedFromWindow(v: View?) {
            attaching = false
            LogUtils.i("onViewDetachedFromWindow")
            callback?.onDismiss()
        }
    }


    private fun initLayoutParams() {
        LogUtils.i("initLayoutParams()")
        //type 和 flag一起可以决定 window的显示范围
        params = WindowManager.LayoutParams()
        // 默认的params, 如果外部传入将会覆盖
        params?.let {
            it.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            it.width = WindowManager.LayoutParams.WRAP_CONTENT
            it.height = WindowManager.LayoutParams.WRAP_CONTENT
            //gravity的设置会导致 layoutParams.x 、y 所在的坐标系发生变化
            it.gravity = Gravity.START or Gravity.BOTTOM
            it.format = PixelFormat.TRANSPARENT//控制背景透明
            it.x = 0
            it.y = 0
            val currentFlags = it.javaClass.getField("privateFlags").get(params) as Int
            it.javaClass.getField("privateFlags").set(params, currentFlags or 0x00000040)
            it.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        }
    }


    fun setType(type: Int) {
        LogUtils.i("setType()")
        params?.let {
            it.type = type
        }
    }


    fun setWindowType(type: Int) {
        LogUtils.i("setWindowType($type)")

        params?.let {
            it.type = type
            if (rootLayout.isAttachedToWindow) {
                windowManager.updateViewLayout(rootLayout, it)
            }
        }
    }

    fun dismiss() {
        windowManager.removeView(rootLayout)
    }

    /**
     * 外部设置view位置
     */
    fun setLocation(x: Int, y: Int) {
        params?.let {
            it.x = x
            it.y = y
            LogUtils.i("setLocation($x,$y)")
            if (rootLayout.isAttachedToWindow) {
                windowManager.updateViewLayout(rootLayout, it)
            }
        }
    }

    fun setWindowVisible(vis: Boolean) {
        LogUtils.i("setWindowVisible($vis)")
        if (!rootLayout.isAttachedToWindow) {
            return
        }

        if (vis) {
            rootLayout.visibility = VISIBLE
        } else {
            rootLayout.visibility = GONE
        }
    }

    fun setGravity(gravity: Int) {
        params?.gravity = gravity
    }

    fun update() {
        LogUtils.i("update()")
        windowManager.updateViewLayout(rootLayout, params)
        callback?.onUpdate()
    }


    fun setWidthAndHeight(width: Int, height: Int) {
        setWidth(width)
        setHeight(height)
    }

    fun setWidth(width: Int) {
        LogUtils.v("setWidth($width)")
        params?.let {
            when (width) {
                0 -> {
                    it.width = WindowManager.LayoutParams.WRAP_CONTENT
                }
                else -> {
                    it.width = width
                }
            }
        }
    }


    fun setHeight(height: Int) {
        params?.let {
            when (height) {
                0 -> {
                    it.height = WindowManager.LayoutParams.WRAP_CONTENT
                }
                else -> {
                    it.height = height
                }
            }
        }
    }

    fun setDisplayId(displayId: Int) {
        val display: Display =
                mContext.getSystemService(DisplayManager::class.java).getDisplay(id)
        displayIdChange = display.displayId != displayId
        LogUtils.i("setDisplayId($displayId, displayIdChange=$displayIdChange)")
        this.id = displayId
    }

    fun hasAttach(): Boolean {
        return rootLayout.isAttachedToWindow
    }

    fun hasVisible(): Boolean {
        return rootLayout.isVisible
    }

    fun setIntercept(intercept: Boolean) {
        params?.flags =
                if (intercept) {
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or // 使当前view获取焦点
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                } else {
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or // 使当前view不能获取焦点
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                }

        if (rootLayout.isAttachedToWindow) {
            update()
        } else {
            LogUtils.e("setIntercept() rootLayout not attach")
        }
    }
}
