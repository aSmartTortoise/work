package com.voyah.voice.framework.base

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.LogUtils
import java.lang.reflect.ParameterizedType


/**
 * @author jackie wong
 * @date   2023/2/27 12:18
 * @desc   DataBinding+ViewModel基类
 */
abstract class BaseMvvmActivity<DB : ViewBinding, VM : ViewModel> : BaseDataBindActivity<DB>() {
    lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility())
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        initViewModel()
        super.onCreate(savedInstanceState)
    }

    protected open fun getSystemUiVisibility(): Int {
        var systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        if (getDarkModeStatus()) {
            systemUiVisibility = systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        return systemUiVisibility
    }

    open fun getDarkModeStatus(): Boolean {
        val currentMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isNight = currentMode == Configuration.UI_MODE_NIGHT_YES
        return isNight
    }


    private fun initViewModel() {
        val argument = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
        viewModel = ViewModelProvider(this)[argument[1] as Class<VM>]
    }
}