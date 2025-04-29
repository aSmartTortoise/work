package com.voyah.voice.main.ui

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.LogUtils
import com.voyah.voice.framework.base.BaseDialog.AnimStyle

open class BaseDialogFragment : DialogFragment() {


    private var currentUiMode: Int = -1

    companion object {


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUiMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        LogUtils.d("onCreate currentUiMode:$currentUiMode")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        LogUtils.d("onCreateDialog")
        dialog.window?.apply {
            setDimAmount(0.5f)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setOnCancelListener(this@BaseDialogFragment)
            val params = attributes
            params?.apply {
                gravity = Gravity.CENTER
                width = getParamsWidth()
                height = getParamsHeight()
                windowAnimations = AnimStyle.DEFAULT
            }

            attributes = params
        }
        return dialog
    }

    open fun getParamsWidth() = WindowManager.LayoutParams.WRAP_CONTENT

    open fun getParamsHeight() = WindowManager.LayoutParams.WRAP_CONTENT

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val newUiMode: Int = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        LogUtils.d("onConfigurationChanged newUiMode:${newUiMode}")
        if (newUiMode == currentUiMode) {
            return
        }
        currentUiMode = newUiMode
        LogUtils.d("onConfigurationChanged uiMode change, and reset default night mode...")
        var mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        when (newUiMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                mode = AppCompatDelegate.MODE_NIGHT_NO
            }

            Configuration.UI_MODE_NIGHT_YES -> {
                mode = AppCompatDelegate.MODE_NIGHT_YES
            }
        }
        LogUtils.d("onConfigurationChanged, setDefaultNightMode ${mode}")
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    override fun onDetach() {
        LogUtils.d("onDetach")
        super.onDetach()
    }

    override fun dismiss() {
        LogUtils.d("dismiss")
        super.dismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        LogUtils.d("onCancel")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        LogUtils.d("onDismiss")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LogUtils.d("onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d("onDestroy")
    }

}
