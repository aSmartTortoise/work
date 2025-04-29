package com.voyah.voice.framework.base

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.blankj.utilcode.util.LogUtils
import com.voyah.voice.framework.R
import com.voyah.voice.framework.toast.TipsToast
import com.voyah.voice.framework.util.LoadingUtils
import com.voyah.voice.framework.util.SystemConfigUtil


/**
 * Author jackie wong
 * Time   2023/2/20 12:33
 * Desc   Activity基类
 */
abstract class BaseActivity : AppCompatActivity() {
    protected val TAG: String? = this::class.java.simpleName

    companion object {
        var resumeActivityCount = 0
    }

    private var currentUiMode: Int = -1

    val statusBarHeight: Int by lazy(LazyThreadSafetyMode.NONE) {
        resources.getDimensionPixelSize(R.dimen.dp_88)
    }

    private val dialogUtils by lazy {
        LoadingUtils(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        LogUtils.i("onCreate")
        super.onCreate(savedInstanceState)
        val configuration = resources.configuration
        val uiModeWithMask = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        currentUiMode = uiModeManager.nightMode
        LogUtils.d("onCreate currentUiMode:$currentUiMode, uiModeWithMask:$uiModeWithMask")
        setContentLayout()
        initView(savedInstanceState)
        initData(savedInstanceState)
    }


    /**
     * 设置布局
     */
    open fun setContentLayout() {
        setContentView(getLayoutResId())
    }

    /**
     * 初始化视图
     * @return Int 布局id
     * 仅用于不继承BaseDataBindActivity类的传递布局文件
     */
    abstract fun getLayoutResId(): Int

    /**
     * 初始化布局
     * @param savedInstanceState Bundle?
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 初始化数据
     */
    open fun initData(savedInstanceState: Bundle?) {

    }

    /**
     * 加载中……弹框
     */
    fun showLoading() {
        showLoading(getString(R.string.framework_default_loading))
    }

    /**
     * 加载提示框
     */
    private fun showLoading(msg: String?) {
        dialogUtils.showLoading(msg)
    }

    /**
     * 加载提示框
     */
    fun showLoading(@StringRes res: Int) {
        showLoading(getString(res))
    }

    /**
     * 关闭提示框
     */
    fun dismissLoading() {
        dialogUtils.dismissLoading()
    }

    /**
     * Toast
     * @param msg Toast内容
     */
    fun showToast(msg: String) {
        TipsToast.showTips(msg)
    }

    /**
     * Toast
     * @param resId 字符串id
     */
    fun showToast(@StringRes resId: Int) {
        TipsToast.showTips(resId)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val newUiModeWithMask = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val newUiMode = uiModeManager.nightMode
        LogUtils.i("onConfigurationChanged new ui mode:$newUiMode, \ncurrent ui mode:$currentUiMode" +
                " \n newUiModeWithMask:$newUiModeWithMask")
        if (newUiMode == currentUiMode) {
            return
        }
        currentUiMode = newUiMode
        var mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        when (newUiModeWithMask) {
            Configuration.UI_MODE_NIGHT_NO -> {
                mode = AppCompatDelegate.MODE_NIGHT_NO
            }

            Configuration.UI_MODE_NIGHT_YES -> {
                mode = AppCompatDelegate.MODE_NIGHT_YES
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            SystemConfigUtil.setDefaultNightMode(this)
        }
        recreate()
    }


    override fun onPause() {
        resumeActivityCount--
        Log.i("PAUSE", "resumeActivityCount:$resumeActivityCount")
        super.onPause()
    }

    override fun onResume() {
        resumeActivityCount++
        Log.i("RESUME", "resumeActivityCount:$resumeActivityCount")
        super.onResume()
    }
}