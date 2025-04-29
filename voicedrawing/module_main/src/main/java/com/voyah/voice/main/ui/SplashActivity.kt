package com.voyah.voice.main.ui

import android.os.Bundle
import com.blankj.utilcode.util.LogUtils
import com.voyah.voice.main.R
import com.voyah.voice.main.databinding.ActivitySplashBinding
import com.voyah.common.provider.MainServiceProvider
import com.voyah.voice.framework.base.BaseDataBindActivity

class SplashActivity : BaseDataBindActivity<ActivitySplashBinding>() {
    override fun getLayoutResId() = R.layout.activity_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.d("onCreate")
        MainServiceProvider.toMain(this)
        finish()
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

}