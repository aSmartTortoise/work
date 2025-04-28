package com.voyah.window.windowholder

import android.content.Context
import android.os.Build
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.lib.common.voyah.ServiceFactory
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.voyah.cockpit.window.model.ScreenType
import com.voyah.cockpit.window.model.VoiceLocation
import com.voyah.cockpit.window.model.WindowType
import com.voyah.voice.common.manager.MegaDisplayHelper
import com.voyah.voice.framework.helper.AppHelper
import com.voyah.window.R
import com.voyah.window.util.SystemConfigUtil
import com.voyah.window.view.wave.WaveState
import com.voyah.window.view.wave.WaveSurfaceView
import org.libpag.PAGFile

/**
 *  author : jie wang
 *  date : 2024/3/25 19:55
 *  description : 音浪的holder。
 */
class VoiceWaveWindowHolder(context: Context) : BaseWindowHolder(context) {

    private val windowWidth: Int by lazy(LazyThreadSafetyMode.NONE) {
        600
    }

    private val windowMargin: Int by  lazy(LazyThreadSafetyMode.NONE) {
        0
    }

    private var sfvWave: WaveSurfaceView? = null
    private var floatingView: View? = null
    private val pagListeningNight: PAGFile by lazy(LazyThreadSafetyMode.NONE) {
        PAGFile.Load(context.assets, "wave_voice_listening_night_1.pag")
    }

    private val pagListeningLight: PAGFile by lazy(LazyThreadSafetyMode.NONE) {
        PAGFile.Load(context.assets, "wave_voice_listening_light_1.pag")
    }


    var windowTag = WindowType.WINDOW_TYPE_VOICE_WAVE_MAIN_SCREEN

    companion object {
        const val TAG = "VoiceWaveWindowHolder"
    }

    override fun getDisplayId(): Int {
        LogUtils.d("getDisplayId wakeVoiceLocation:$wakeVoiceLocation")
        return when (wakeVoiceLocation) {
            VoiceLocation.FRONT_LEFT -> {
                MegaDisplayHelper.getVoiceDisplayId(VoiceLocation.FRONT_LEFT)
            }

            VoiceLocation.FRONT_RIGHT -> {
                if (screenStateProvider?.getScreenState(ScreenType.PASSENGER) == 1) {
                    MegaDisplayHelper.getVoiceDisplayId(VoiceLocation.FRONT_RIGHT)
                } else {
                    MegaDisplayHelper.getVoiceDisplayId(VoiceLocation.FRONT_LEFT)
                }
            }

            else -> {
                if (screenStateProvider?.getScreenState(ScreenType.CEILING) == 1 && wakeVoiceLocation >= VoiceLocation.REAR_LEFT) {
                    MegaDisplayHelper.getVoiceDisplayId(VoiceLocation.REAR_LEFT)
                } else {
                    MegaDisplayHelper.getVoiceDisplayId(VoiceLocation.FRONT_LEFT)
                }
            }
        }
    }

    fun showWave(voiceLocation: Int) {
        LogUtils.d("showWave, previous location:$wakeVoiceLocation, " +
                "previous location parsed :${getLocationParsed(wakeVoiceLocation)}," +
                "\ncurrent location:$voiceLocation, " +
                "current location parsed:${getLocationParsed(voiceLocation)}")
        if (!isWaveWindowShow()) {
            wakeVoiceLocation = voiceLocation
            showWindow()
        } else {
            if (voiceLocation != wakeVoiceLocation) {
                onVoiceLocationChange(voiceLocation)
            }
        }
    }

    //TODO
    // 根据 voiceLocation wakeVoiceLocation 判断前后音浪是都相同屏
    // 1. 如果相同屏幕，直接更新位置
    // 2. 如果不同屏幕，重新showWindow
    private fun onVoiceLocationChange(voiceLocation: Int) {
        val multipleDisplayFlag = ServiceFactory.getInstance().voiceService.hasMultipleDisplay()
        LogUtils.i("onVoiceLocationChange multipleDisplayFlag:$multipleDisplayFlag")
        if (multipleDisplayFlag) {
            if (screenStateProvider?.getScreenState(ScreenType.CEILING) == 1) {
                if (voiceLocation <= VoiceLocation.FRONT_RIGHT) {// 新的唤醒音区在前排
                    wakeVoiceLocation = voiceLocation
                    showWindow()
                } else {// 新的唤醒音区在后排
                    if (wakeVoiceLocation <= VoiceLocation.FRONT_RIGHT) {//当前音区在前排
                        wakeVoiceLocation = voiceLocation
                        showWindow()
                    } else {//当前音区在后排
                        onRearLocationChanged(voiceLocation)
                    }
                }
            } else {
                wakeVoiceLocation = voiceLocation
                showWindow()
            }
        } else {
            val preLocation = wakeVoiceLocation
            wakeVoiceLocation = voiceLocation
            var x = windowMargin
            LogUtils.i("onVoiceLocationChanged: voice previous location: " +
                    "${getLocationParsed(preLocation)}, \n" +
                    "current location:${getLocationParsed(wakeVoiceLocation)}")
            when (wakeVoiceLocation) {
                VoiceLocation.FRONT_LEFT -> {
                    when (preLocation) {
                        VoiceLocation.FRONT_RIGHT -> {
                        }

                        VoiceLocation.REAR_LEFT,
                        VoiceLocation.REAR_RIGHT -> {

                        }
                    }
                }

                VoiceLocation.FRONT_RIGHT -> {
                    x = screenWidth - windowWidth
                    when (preLocation) {
                        VoiceLocation.FRONT_LEFT -> {
                        }

                        VoiceLocation.REAR_LEFT,
                        VoiceLocation.REAR_RIGHT-> {
                        }

                    }
                }

                VoiceLocation.REAR_LEFT,
                VoiceLocation.REAR_RIGHT -> {
                    x = (screenWidth - windowWidth) / 2
                    when (preLocation) {
                        VoiceLocation.FRONT_LEFT -> {

                        }
                        VoiceLocation.FRONT_RIGHT -> {
                        }
                    }
                }
            }

            EasyFloat.updateFloat(windowTag, x = x)
        }
    }

    override fun onVoiceListening() {
        LogUtils.d("onVoiceListening")
    }

    override fun isWindowShow() = EasyFloat.isShow(WindowType.WINDOW_TYPE_VOICE_WAVE)

    private fun isWaveWindowShow(): Boolean {
        LogUtils.i("isWaveWindowShow current windowTag:$windowTag")
        return EasyFloat.isShow(windowTag).apply {
            LogUtils.d("isWaveWindowShow result:$this")
        }
    }

    override fun getLayoutRes() = R.layout.window_voice_wave

    override fun setVoiceFocusLocation() {
        LogUtils.i("setVoiceFocusLocation location of voice focus:${AppHelper.locationVoiceFocus}")
    }

    private fun getWindowTag(location: Int): String {
        return ServiceFactory.getInstance().voiceService.getWaveWindowTag(
            location,
            screenStateProvider?.getScreenState(ScreenType.PASSENGER) == 1,
            screenStateProvider?.getScreenState(ScreenType.CEILING) == 1,
        )
    }

    private fun getWaveX(location: Int): Int {
        return ServiceFactory.getInstance().voiceService.getWaveX(
            location,
            windowMargin,
            screenStateProvider?.getScreenState(ScreenType.PASSENGER) == 1,
            screenStateProvider?.getScreenState(ScreenType.CEILING) == 1,
            screenWidth,
            windowWidth
        )
    }

    override fun showWindow() {
        dismissVoiceWaveView()
        LogUtils.i("showWindow screenWidth:$screenWidth")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            SystemConfigUtil.setDefaultNightMode(context)
        }
        currentNightModeFlag = SystemConfigUtil.isNightMode(context)
        LogUtils.i("showWindow currentNightModeFlag:$currentNightModeFlag")
        LogUtils.d("showWindow: voice location:${getLocationParsed(wakeVoiceLocation)}")

        windowTag = getWindowTag(wakeVoiceLocation)
        val x = getWaveX(wakeVoiceLocation)
        val y = 0

        LogUtils.d("showWindow move location, x:$x, y:$y")
        EasyFloat.with(context)
            .setTag(windowTag)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setImmersionStatusBar(false)
            .setLocation(x, y)
            .setDragEnable(false)
            .setAnimator(null)
            .setTouchable(false)
            .setDisplayId(getDisplayId())
            .setLayout(getLayoutRes()) { view ->
                LogUtils.d("voice wave onLayout.")
                initView(view)
            }
            .registerCallback {

                show {
                    LogUtils.d("voice wave show.")
                }

                dismiss {
                    LogUtils.d("voice wave dismiss.")
                }
            }
            .show()
    }

    private fun initView(view: View) {
        sfvWave = view.findViewById<WaveSurfaceView>(R.id.sfv_wave).apply {
            setDirection(wakeVoiceLocation)
            LogUtils.d("initView, nightModeFlag:$currentNightModeFlag")
            LogUtils.d("initView wave width:${width}")
            val waveSate = getListeningWaveState(currentNightModeFlag)
            startDraw(waveSate)
        }
    }

    private fun getListeningWaveState(nightModeFlag: Boolean): WaveState {
        val waveState = if (nightModeFlag) WaveState.LISTENING_NIGHT
        else WaveState.LISTENING_LIGHT
        return waveState
    }

    override fun onVoiceSpeaking() {
        LogUtils.d("onVoiceSpeaking")
    }

    override fun onVoiceExit() {
        LogUtils.d("onVoiceExit")
        dismissVoiceWaveView()
    }

    fun dismissVoiceWaveView() {
        LogUtils.d("dismissVoiceWaveView windowTag:$windowTag")
        EasyFloat.dismiss(windowTag, true)
    }

    private fun onRearLocationChanged(voiceLocation: Int) {
        val preLocation = wakeVoiceLocation
        wakeVoiceLocation = voiceLocation
        var x = windowMargin
        LogUtils.d("onRearLocationChanged: previous location:$preLocation, " +
                "previous location parsed: ${getLocationParsed(preLocation)},\n" +
                "current location:$wakeVoiceLocation, " +
                "current location parsed:${getLocationParsed(wakeVoiceLocation)}")

        if (wakeVoiceLocation % 2 == 1) {
            x = screenWidth - windowWidth - windowMargin + 320
        }

        EasyFloat.updateFloat(windowTag, x = x)
    }

    override fun setVoiceServiceMode(voiceMode: Int) {
    }

    override fun setVoiceLanguageType(languageType: Int) {
    }

    fun onScreenStateChange() {
        LogUtils.i("onScreenStateChange reshow wave")
        if (isWaveWindowShow()) {
            showWindow()
        }
    }

    override fun onUIModeChange(nightModeFlag: Boolean) {
        LogUtils.d("onUIModeChange nightModeFlag:$nightModeFlag")
        if (currentNightModeFlag != nightModeFlag) {
            LogUtils.d("onUIModeChange change...")
            currentNightModeFlag = nightModeFlag
            val waveState = getListeningWaveState(nightModeFlag)
            sfvWave?.startDraw(waveState)
        } else {
            LogUtils.d("onUIModeChange not change...")
        }

    }


}