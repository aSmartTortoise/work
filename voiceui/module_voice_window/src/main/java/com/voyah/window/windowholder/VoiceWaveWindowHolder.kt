package com.voyah.window.windowholder

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.blankj.utilcode.util.LogUtils
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.voyah.cockpit.window.model.VoiceLocation
import com.voyah.cockpit.window.model.VoiceMode
import com.voyah.cockpit.window.model.WindowType
import com.voyah.window.R
import org.libpag.PAGFile
import org.libpag.PAGImageView

/**
 *  author : jie wang
 *  date : 2024/3/25 19:55
 *  description : 音浪的holder。
 */
class VoiceWaveWindowHolder(context: Context) : BaseWindowHolder(context) {

    private val windowWidth: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_570)
    }
    private val windowHeight: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_325)
    }
    private val windowMargin: Int by  lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_58)
    }

    private var pagWave: PAGImageView? = null
    private var flWave: FrameLayout? = null
    private val pagListening: PAGFile by lazy(LazyThreadSafetyMode.NONE) {
        PAGFile.Load(context.assets, "wave_voice_listening.pag")
    }

    companion object {
        const val TAG = "VoiceWaveWindowHolder"
    }

    override fun onVoiceAwake(voiceServiceMode: Int, voiceLocation: Int) {
        LogUtils.d("onVoiceAwake, voiceServiceMode:$voiceServiceMode, voiceLocation:$voiceLocation")

        voiceMode = voiceServiceMode
        if (!isWindowShow()) {
            wakeVoiceLocation = voiceLocation
            showWindow()
        } else {
            if (voiceLocation != wakeVoiceLocation) {
                onVoiceLocationChanged(voiceLocation)
            }
        }
    }

    override fun onVoiceListening() {
        LogUtils.d("onVoiceListening")
    }

    override fun isWindowShow() = EasyFloat.isShow(WindowType.WINDOW_TYPE_VOICE_WAVE)
    override fun getLayoutRes() = R.layout.window_voice_wave

    override fun showWindow() {
        var x = windowMargin
        var y = -statusBarHeight
        LogUtils.d("showWindow: voice location:${wakeVoiceLocation}")
        LogUtils.d("showWindow, navigate bar height:${navigationBarHeight}")
        var rotateX = 0f
        var rotateY = 0f
        when (wakeVoiceLocation) {
            VoiceLocation.FRONT_RIGHT -> {
                x = screenWidth - windowWidth
                rotateY = 180f
            }
            VoiceLocation.REAR_LEFT -> {
                y = screenHeight - windowHeight - navigationBarHeight - statusBarHeight
                rotateX = 180f
            }
            VoiceLocation.REAR_RIGHT -> {
                x = screenWidth - windowWidth
                y = screenHeight - windowHeight - navigationBarHeight - statusBarHeight
                rotateY = 180f
                rotateX = 180f
            }
        }

        LogUtils.d("showWindow move location, x:$x, y:$y")
        EasyFloat.with(context)
            .setTag(WindowType.WINDOW_TYPE_VOICE_WAVE)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setImmersionStatusBar(true)
            .setLocation(x, y)
            .setDragEnable(false)
            .setAnimator(null)
            .setTouchable(false)
            .setLayout(getLayoutRes()) { view ->
                pagWave = view.findViewById<PAGImageView>(R.id.pag_wave).apply {
                    LogUtils.d(" rotateX > 0f?${rotateX > 0f}, rotateY > 0f ? ${rotateY > 0f}")
                    rotationX = rotateX
                    rotationY = rotateY

                    composition = getListeningAnimRes()
                    setRepeatCount(-1)
                    play()
                }

                flWave = view.findViewById<FrameLayout>(R.id.fl_wave).apply {
//                    LogUtils.d(" rotateX > 0f?${rotateX > 0f}, rotateY > 0f ? ${rotateY > 0f}")
//                        rotationX = rotateX
//                        rotationY = rotateY
                }
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

    private fun getListeningAnimRes(): PAGFile {
        return pagListening
    }

    override fun onVoiceSpeaking() {
        LogUtils.d("onTTSPlaying")
    }

    override fun onVoiceExit() {
        LogUtils.d("onVoiceExit")
        EasyFloat.dismiss(WindowType.WINDOW_TYPE_VOICE_WAVE, true)
    }

    private fun onVoiceLocationChanged(voiceLocation: Int) {
        val preLocation = wakeVoiceLocation
        wakeVoiceLocation = voiceLocation

        var x = windowMargin
        var y = -statusBarHeight
        LogUtils.d("onVoiceLocationChanged: voice previous location: $preLocation, " +
                "current location:${wakeVoiceLocation}")
        var rotateX = 0f
        var rotateY = 0f
        when (wakeVoiceLocation) {
            VoiceLocation.FRONT_LEFT -> {
                when (preLocation) {
                    VoiceLocation.FRONT_RIGHT -> {
                        rotateY = 360f
                    }

                    VoiceLocation.REAR_LEFT -> {
                        rotateX = 360f
                    }

                    VoiceLocation.REAR_RIGHT -> {
                        rotateX = 360f
                    }
                }
            }

            VoiceLocation.FRONT_RIGHT -> {
                x = screenWidth - windowWidth
                when (preLocation) {
                    VoiceLocation.FRONT_LEFT -> {
                        rotateY = 180f
                    }

                    VoiceLocation.REAR_LEFT -> {
                        rotateX = 360f
                    }

                    VoiceLocation.REAR_RIGHT -> {
                        rotateX = 360f
                        rotateY = 180f
                    }
                }
            }

            VoiceLocation.REAR_LEFT -> {
                y = screenHeight - windowHeight - navigationBarHeight - statusBarHeight
                rotateX = 180f
            }

            VoiceLocation.REAR_RIGHT -> {
                x = screenWidth - windowWidth
                y = screenHeight - windowHeight - navigationBarHeight - statusBarHeight
                rotateY = 180f
                rotateX = 180f
            }
        }

        pagWave?.visibility = View.INVISIBLE

        EasyFloat.updateFloat(
            WindowType.WINDOW_TYPE_VOICE_WAVE,
            x = x, y = y
        )

        pagWave?.apply {
            LogUtils.d("onVoiceLocationChanged rotateX > 0f?${rotateX > 0f}, rotateY > 0f ? ${rotateY > 0f}")
            rotationX = rotateX
            rotationY = rotateY
            visibility = View.VISIBLE
            //                    composition = getListeningAnimRes()
            //                    setRepeatCount(-1)
            //                    play()
        }

        flWave?.apply {
            //                    LogUtils.d("onVoiceAwake rotateX > 0f?${rotateX > 0f}, rotateY > 0f ? ${rotateY > 0f}")
            //                    rotationX = rotateX
            //                    rotationY = rotateY
        }
    }


}