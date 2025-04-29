package com.voyah.voice.main.presenter

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ScreenUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lib.common.voyah.ServiceFactory
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.vcos.common.widgets.vcostoast.VcosToastManager
import com.voice.drawing.api.APIResult
import com.voice.drawing.api.model.DrawingInfo
import com.voice.drawing.api.model.DrawingInfoDatabase
import com.voice.drawing.api.model.DrawingState
import com.voyah.ai.sdk.manager.TTSManager
import com.voyah.ds.common.entity.status.StreamMode
import com.voyah.vcos.EVENT_TYPE_DRAWING_RESULT
import com.voyah.viewcmd.VoiceViewCmdManager
import com.voyah.voice.framework.ext.normalScope
import com.voyah.voice.framework.helper.AppHelper
import com.voyah.voice.framework.util.SystemConfigUtil
import com.voyah.voice.main.R
import com.voyah.voice.main.anim.FlyInOutAnimator
import com.voyah.voice.main.interfce.IShowDrawingOkWindow
import com.voyah.voice.main.manager.LifeStateManager
import com.voyah.voice.main.ui.DrawingInfoActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.util.Timer
import java.util.TimerTask

@SuppressWarnings("unused")
class DrawingAPIPresenter(val context: Context) {

    companion object {
        const val WINDOW_TAG_OUTER = "window_tag_outer"
        const val NOTIFICATION_LIVE_TIME = 10_000L
        const val TIME_DRAWING_TIME_OUT = 20
        const val TIME_DRAWING_PROGRESS_TIME = 10_000
        const val PROGRESS_DRAWING_WAIT_SERVER = 90 //从开始绘图到云端下发URL期间等待的最大进度
        const val KEY_SPLIT_SCREEN_STATUS = "split_screen_status"
        const val SPLIT_SCREEN_STATUS_EXIT = 0
        const val SPLIT_SCREEN_STATUS_ENTER = 1
        val SET_REQUEST_ID = HashSet<String>()
        var startTime = 0L
        var timeStartDrawing = 0L
    }

    private val screenWidth: Int by lazy(LazyThreadSafetyMode.NONE) {
        ScreenUtils.getScreenWidth()
    }
    private val outerWindowWidth by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_799)
    }

    private var drawingStateCallback: DrawingStateCallback? = null

    private val normalScope by lazy(LazyThreadSafetyMode.NONE) {
        normalScope()
    }

    private var windowShowCountDownJob: Job? = null

    private lateinit var outerOKView: View
    private var timerDrawing: Timer? = null
    private var timerDrawingProgress: Timer? = null
    private var drawingTimeoutFlag = false
    private var currentSplitScreenStatus = SPLIT_SCREEN_STATUS_EXIT


    init {
        currentSplitScreenStatus = getSplitScreenState()
        LogUtils.d("init, split screen status is:${getScreenStateStr(currentSplitScreenStatus)}")
    }


    fun setDrawingStateCallback(callback: DrawingStateCallback?) {
        drawingStateCallback = callback
    }

    fun openApp(): Int {
        var result: Int

        try {
            val mapNaviUnderway = isMapNaviUnderway()
            val splitScreenSwitch = isSettingsSplitScreenSwitchOpen()
            if (mapNaviUnderway && splitScreenSwitch) {
                LogUtils.d("openApp, send split screen broadcast to open app")
                sendSplitScreenBroadcast()
                result = APIResult.SUCCESS
            } else {
                LogUtils.d("openApp, just start activity to open app")
                val packageManager: PackageManager = context.packageManager
                val intent = packageManager.getLaunchIntentForPackage(context.packageName)
                result = if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    APIResult.SUCCESS
                } else {
                    APIResult.NO_RESOURCE
                }
            }
        } catch (e: Exception) {
            result = APIResult.ERROR
            LogUtils.e("openApp error:$e")
        }

        LogUtils.d("openApp result:$result")
        return result
    }

    fun openAppAsDisplayId(displayId: Int): Int {
        var result: Int

        try {
            val mapNaviUnderway = isMapNaviUnderway()
            val splitScreenSwitch = isSettingsSplitScreenSwitchOpen()
            if (mapNaviUnderway && splitScreenSwitch) {
                LogUtils.d("openApp, send split screen broadcast to open app")
                sendSplitScreenBroadcast()
                result = APIResult.SUCCESS
            } else {
                LogUtils.d("openApp, just start activity to open app")
                val packageManager: PackageManager = context.packageManager
                val intent = packageManager.getLaunchIntentForPackage(context.packageName)
                result = if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent, ActivityOptions.makeBasic().apply {
                        setLaunchDisplayId(displayId)
                    }.toBundle())
                    APIResult.SUCCESS
                } else {
                    APIResult.NO_RESOURCE
                }
            }
        } catch (e: Exception) {
            result = APIResult.ERROR
            LogUtils.e("openApp error:$e")
        }

        LogUtils.d("openApp result:$result")
        return result
    }

    fun closeApp(): Int {
        var result: Int = APIResult.INIT
        val splitScreenState = Settings.System.getInt(
            context.contentResolver,
            "split_screen_status",
            0
        )
        if (splitScreenState == 1) {//处于分屏状态
            LogUtils.d("closeApp, send split screen broadcast to exit split screen...")
            sendDismissSplitBroadcast()
            result = APIResult.SUCCESS
        } else {
            val packageManager: PackageManager = context.packageManager
            var intent: Intent? = null
            try {
                intent = packageManager.getLaunchIntentForPackage("com.voyah.cockpit.launcher")
            } catch (e: java.lang.Exception) {
                LogUtils.e("closeApp error:$e")
                result = APIResult.ERROR
            }

            if (intent != null) {
                LogUtils.d("closeApp back to launcher...")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                result = APIResult.SUCCESS
            }
        }

        LogUtils.d("closeApp result:$result")
        return result
    }

    fun isAppForeground(): Int {
        val result = if (LifeStateManager.getInstance().isForeground()) APIResult.UI_FOREGROUND
        else APIResult.UI_NOT_FOREGROUND
        LogUtils.d("isAppForeground result:$result")
        return result
    }

    private fun sendDismissSplitBroadcast(dismissLeft: Boolean = false) {
        LogUtils.d("sendDismissSplitBroadcast dismissLeft:$dismissLeft")
        val intent = Intent().apply {
            putExtra("dissmissLeft", dismissLeft)
            setAction("com.android.systemui.split_screen_dismiss")
        }
        context.sendBroadcast(intent)
    }

    fun openDrawingInfo(drawingStateJson: String, displayId: Int): Int {
        LogUtils.d("openDrawingInfo displayId:$displayId")
        val mapNaviUnderway = isMapNaviUnderway()
        val splitScreenSwitch = isSettingsSplitScreenSwitchOpen()
        if (mapNaviUnderway && splitScreenSwitch) {
            LogUtils.d("openDrawingInfo, send split screen broadcast to start.")
            sendSplitScreenBroadcast(
                className = "com.voyah.voice.main.ui.DrawingInfoActivity",
                drawingStateJson = drawingStateJson
            )
        } else {
            LogUtils.d("openDrawingInfo, just start activity to start.")
            DrawingInfoActivity.startDrawingInfo(context, drawingStateJson, displayId)
        }

        return APIResult.SUCCESS
    }

    fun postDrawingState(drawingStateJson: String, displayId: Int) {
        try {
            val jsonObject = JSONObject(drawingStateJson)
            if (jsonObject.has("drawingState")) {
                val drawingState = jsonObject.getInt("drawingState")
                LogUtils.d("postDrawingState drawingState:$drawingState")
                var streamMode = StreamMode.NOT_STREAM_MODE
                if (jsonObject.has("streamMode")) {
                    streamMode = jsonObject.getInt("streamMode")
                    LogUtils.d("postDrawingState streamMode:$streamMode")
                }
                when (drawingState) {
                    DrawingState.START -> {
                        startTime = System.currentTimeMillis()
                        timeStartDrawing = System.currentTimeMillis()
                        if (LifeStateManager.getInstance().isForeground()) {
                            LogUtils.d("app is foreground.")
                            val topActivity = LifeStateManager.getInstance().getTopActivity()
                            if (topActivity != null) {
                                LogUtils.d("postDrawingState topActivity:$topActivity")
                                if (topActivity !is DrawingInfoActivity) {
                                    LogUtils.d("top activity not null, and is not DrawingInfoActivity, to openDrawingInfo.")
                                    openDrawingInfo(drawingStateJson, displayId)
                                } else {
                                    LogUtils.d("top activity not null, and is DrawingInfoActivity, to onDrawingStateChanged.")
                                    drawingStateCallback?.onDrawingStateChanged(drawingStateJson)
                                }
                            }
                        } else {
                            LogUtils.d("app is background, to openDrawingInfo.")
                            openDrawingInfo(drawingStateJson, displayId)
                        }
                        cancelDrawingTimerTask()
                        executeDrawingTimeTask {
                            LogUtils.d("drawing state timer task end...")
                            normalScope.launch {
                                drawingTimeoutFlag = true
                                drawingStateCallback?.onDrawingTimeout()
                                val tipError = context.getString(R.string.load_picture_error)
                                LogUtils.d("currentSplitScreenStatus:$currentSplitScreenStatus")

                                val contextDisplay = ServiceFactory
                                    .getInstance()
                                    .voiceService
                                    .getDisplayContext(context, displayId)

                                if (currentSplitScreenStatus == SPLIT_SCREEN_STATUS_EXIT) {
                                    VcosToastManager.Companion.instance?.showSystemToast(
                                        contextDisplay,
                                        tipError
                                    )
                                } else {
                                    VcosToastManager.Companion.instance?.showSystemToastInSplitScreen(
                                        contextDisplay,
                                        tipError
                                    )
                                }
                                var time = 0L
                                if (startTime != 0L) {
                                    time = System.currentTimeMillis() - startTime;
                                    startTime = 0;
                                }
                                ServiceFactory.getInstance().voiceService.postBurialPointEvent(
                                    EVENT_TYPE_DRAWING_RESULT,
                                    "",
                                    time,
                                    "-2001"
                                )

                                TTSManager.speak(tipError)
                            }
                        }
                        cancelChangeDrawingProgressTask()
                        executeChangeDrawingProgressTask(
                            progressChangeBlock = {
                                normalScope.launch {
                                    drawingStateCallback?.onProgressChange(it)
                                }

                            },
                            timerTaskEnd = {
                                LogUtils.d("drawing progress timer task end...")
                            }
                        )
                    }

                    DrawingState.COMPLETION -> {
                        LogUtils.d("postDrawingState drawing state completion")
                        var urlList: List<String>? = null
                        if (jsonObject.has("urlList")) {
                            urlList = Gson().fromJson<List<String>>(
                                jsonObject.getJSONArray("urlList").toString(),
                                object : TypeToken<List<String>>() {}.type
                            )
                            normalScope.launch(Dispatchers.Default) {
                                val drawingInfo = Gson().fromJson(drawingStateJson, DrawingInfo::class.java)
                                if (drawingInfo != null) {
                                    LogUtils.d(
                                        "postDrawingState drawing state completion, " +
                                                "insert drawing history table."
                                    )
                                    DrawingInfoDatabase
                                        .getInstance(context.applicationContext)
                                        .drawingInfoDao()
                                        .insert(drawingInfo)
                                    downloadDrawingPics(drawingInfo)
                                }
                            }
                        }
                        LogUtils.d("postDrawingState, drawingTimeoutFlag:$drawingTimeoutFlag")
                        if (drawingTimeoutFlag) {
                            drawingTimeoutFlag = false
                            return
                        }

                        cancelDrawingTimerTask()
                        cancelChangeDrawingProgressTask()
                        drawingStateCallback?.onProgressChange(PROGRESS_DRAWING_WAIT_SERVER.toFloat())

                        if (urlList?.isNotEmpty() == true) {
                            if (LifeStateManager.getInstance().isForeground()) {
                                LogUtils.d("postDrawingState app is foreground.")
                                val topActivity = LifeStateManager.getInstance().getTopActivity()

                                if (topActivity is IShowDrawingOkWindow) {
                                    val windowShowFlag =
                                        EasyFloat.isShow(WINDOW_TAG_OUTER).apply {
                                            LogUtils.d("postDrawingState windowShowFlag:$this")
                                        }
                                    if (!windowShowFlag) {
                                        showDrawingOkOuterWindow(
                                            context,
                                            openDrawingBlock = {
                                                openDrawingInfo(drawingStateJson, displayId)
                                            })
                                    }
                                } else {
                                    LogUtils.d("postDrawingState not show drawing ok window.")
                                    if (topActivity is DrawingInfoActivity) {
                                        LogUtils.d("postDrawingState topActivity is DrawingInfoActivity.")
                                        drawingStateCallback?.onDrawingStateChanged(drawingStateJson)
                                    }

                                }

                            } else {
                                LogUtils.d("postDrawingState app is not foreground.")
                                showDrawingOkOuterWindow(
                                    context,
                                    openDrawingBlock = {
                                        openDrawingInfo(drawingStateJson, displayId)
                                    })
                            }
                        }
                    }

                    DrawingState.FAIL -> {
                        LogUtils.d("DrawingState.FAIL$drawingStateJson")
                        var code = jsonObject.getInt("code")
                        val requestId = jsonObject.getString("requestId")
                        if (code != -4022 && code != -6666 && code != -8888) {
                            code = -2022
                        }
                        if (SET_REQUEST_ID.add(requestId)) {
                            var time = 0L
                            if (startTime != 0L) {
                                time = System.currentTimeMillis() - startTime;
                                startTime = 0;
                            }
                            ServiceFactory.getInstance().voiceService.postBurialPointEvent(
                                EVENT_TYPE_DRAWING_RESULT,
                                "",
                                time,
                                code.toString()
                            )
                        }
                        LogUtils.d("postDrawingState drawing state fail...drawingTimeoutFlag:$drawingTimeoutFlag")
                        if (drawingTimeoutFlag) {
                            drawingTimeoutFlag = false
                            return
                        }
                        cancelDrawingTimerTask()
                        cancelChangeDrawingProgressTask()
                        drawingStateCallback?.let {
                            it.onProgressChange(PROGRESS_DRAWING_WAIT_SERVER.toFloat())
                            if (streamMode == StreamMode.STREAM_MODE_IN) {
                                it.onDrawingStateChanged(drawingStateJson)
                            }
                        } ?: run {
                            LogUtils.d(
                                "postDrawingState drawing state fail, drawingStateCallback" +
                                        "=null"
                            )
                            if (streamMode == StreamMode.STREAM_MODE_IN) {
                                openDrawingInfo(drawingStateJson, displayId)
                            }
                        }

                    }
                }
            }

        } catch (e: Exception) {
            LogUtils.w("postDrawingState parse drawingStateJson error:$e")
        }

    }

    private fun downloadDrawingPics(drawingInfo: DrawingInfo) {
        val urlList = drawingInfo.urlList
        LogUtils.i("downloadDrawingPics urlList:${urlList.size}")
        var completeCount = 0
        for (imageUrl in urlList) {
            LogUtils.i("load url:$imageUrl")
            Glide.with(context)
                .asFile()
                .load(imageUrl)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA))
                .into(object : CustomTarget<File>() {
                    override fun onResourceReady(
                        resource: File,
                        transition: Transition<in File>?
                    ) {
                        LogUtils.i("onResourceReady:${imageUrl}")
                        completeCount++
                        if (completeCount == urlList.size) {
                            onDownloadComplete()
                        }

                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        LogUtils.i("onLoadFailed:${imageUrl}")
                        completeCount++
                        if (completeCount == urlList.size) {
                            onDownloadComplete()
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        LogUtils.i("onLoadCleared:")
                    }
                })
        }
    }

    private fun onDownloadComplete() {
        LogUtils.d("onDownloadComplete")
        val drawingTotalTime = System.currentTimeMillis() - timeStartDrawing
        ServiceFactory.getInstance().voiceService.postBurialPointEvent(
            EVENT_TYPE_DRAWING_RESULT,
            "",
            drawingTotalTime,
            "200"
        )
    }

    private fun showDrawingOkOuterWindow(context: Context, openDrawingBlock: () -> Unit) {
        LogUtils.d("showDrawingOkOuterWindow. screenWidth:$screenWidth, outerWindowWidth:$outerWindowWidth")
        val y = 0
        val x = (screenWidth - outerWindowWidth) / 2
        LogUtils.d("showDrawingOkOuterWindow x:$x")
        EasyFloat.with(context)
            .setTag(WINDOW_TAG_OUTER)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setImmersionStatusBar(true)
            .setLocation(x, y)
            .setDragEnable(false)
            .setAnimator(FlyInOutAnimator())
            .setLayout(R.layout.outer_window_drawing_ok) { view ->
                outerOKView = view
                LogUtils.i("outer window view layout outerOKView:$outerOKView")
                view.findViewById<View>(R.id.tv_ignore).setOnClickListener {
                    cancelWindowShowCountDownJob()
                    EasyFloat.dismiss(WINDOW_TAG_OUTER, true)
                }

                view.findViewById<ConstraintLayout>(R.id.cl_check).setOnClickListener {
                    cancelWindowShowCountDownJob()
                    EasyFloat.dismiss(WINDOW_TAG_OUTER, true)
                    LogUtils.d("to Drawing info page...")
                    openDrawingBlock.invoke()
                }
                // 注册可见
                VoiceViewCmdManager.getInstance().register(outerOKView, false, true, null);
            }
            .registerCallback {
                createResult { result, s, _ ->
                    LogUtils.d("createResult outer window is success:$result, reason:$s")
                    if (result) {
                        windowShowCountDownJob = normalScope.launch(Dispatchers.Default) {
                            delay(NOTIFICATION_LIVE_TIME)
                            LogUtils.i("window show time out, then dismiss...")
                            withContext(Dispatchers.Main) {
                                EasyFloat.dismiss(WINDOW_TAG_OUTER, true)
                            }
                        }
                    }
                }

                show {
                    LogUtils.d("on outer window  show.")
                }

                dismiss {
                    LogUtils.d("on outer window dismiss.")
                }

            }
            .show()
    }

    private fun cancelWindowShowCountDownJob() {
        windowShowCountDownJob?.cancel()
        windowShowCountDownJob = null
    }

    private fun getMapNaviState(): Int {
        val mapNaviState = Settings.System.getInt(
            context.applicationContext.contentResolver,
            "persist.sys.map.app_status",
            0
        )
        LogUtils.d("getMapNaviState mapNaviState:$mapNaviState")
        return mapNaviState
    }

    private fun isMapNaviUnderway(): Boolean {
        var naviUnderwayFlag = false
        val mapNaviState = getMapNaviState()
        if (mapNaviState == 2
            || (mapNaviState == 3)
        ) {
            naviUnderwayFlag = true
        }
        LogUtils.d("isMapNaviUnderway naviUnderwayFlag:$naviUnderwayFlag")
        return naviUnderwayFlag
    }


    /**
     * 1:打开 2:关闭
     */
    private fun getSettingsSplitScreenSwitchState(): Int {
        val splitScreenSwitchState = Settings.Global.getInt(
            context.applicationContext.contentResolver,
            "application.layering",
            1
        )
        LogUtils.d("getSettingsSplitScreenSwitchState splitScreenSwitchState:$splitScreenSwitchState")
        return splitScreenSwitchState
    }

    private fun isSettingsSplitScreenSwitchOpen(): Boolean {
        val switchOpenFlag = getSettingsSplitScreenSwitchState() == 1
        return switchOpenFlag
    }

    private fun sendSplitScreenBroadcast(
        className: String? = null,
        position: Int = 1,
        drawingStateJson: String? = null
    ) {
        val packageName = AppUtils.getAppPackageName()
        LogUtils.d("sendSplitScreenBroadcast, packageName:$packageName")
        val bundle = Bundle().apply {
            putString("packageName", packageName)
            if (!className.isNullOrEmpty()) {
                putString("className", className)
            }
            putInt("position", position)
            if (!drawingStateJson.isNullOrEmpty()) {
                putString(DrawingInfoActivity.KEY_DRAWING_STATE, drawingStateJson)
            }
        }
        val intent = Intent().apply {
            // com.android.systemui.split_screen_dismiss
            setAction("com.android.systemui.split_screen_enter")
            putExtras(bundle)
        }
        context.sendBroadcast(intent)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun onConfigurationChanged() {
        val windowShowFlag = EasyFloat.isShow(WINDOW_TAG_OUTER)
        LogUtils.d("onConfigurationChanged windowShowFlag:$windowShowFlag")

        if (windowShowFlag) {
            val enterAnimRunning = EasyFloat.isEnterAnimRunning(WINDOW_TAG_OUTER)
            LogUtils.i("onConfigurationChanged enterAnimRunning:$enterAnimRunning")

            val nightModeFlag = SystemConfigUtil.isNightMode(AppHelper.getApplication())

            val bgLLContent = if (nightModeFlag) R.drawable.outer_window_drawing_ok_bg_night
            else R.drawable.outer_window_drawing_ok_bg_light
            outerOKView.findViewById<LinearLayout>(R.id.ll_content).setBackgroundResource(bgLLContent)

            val resPen = if (nightModeFlag) R.drawable.icon_drawing_pen_night
            else R.drawable.icon_drawing_pen_light
            outerOKView.findViewById<ImageView>(R.id.aiv_pen).setImageResource(resPen)

            val colorOKTip = if (nightModeFlag) R.color.drawing_completion_tip_night
            else R.color.drawing_completion_tip_light
            outerOKView.findViewById<TextView>(R.id.tv_ok_tip).setTextColor(context.getColor(colorOKTip))

            val colorGuide = if (nightModeFlag) R.color.drawing_completion_guide_night
            else R.color.drawing_completion_guide_light
            outerOKView.findViewById<TextView>(R.id.tv_guide).setTextColor(context.getColor(colorGuide))


            val colorIgnore = if (nightModeFlag) R.color.drawing_completion_ignore_night
            else R.color.drawing_completion_ignore_light
            outerOKView.findViewById<TextView>(R.id.tv_ignore).setTextColor(context.getColor(colorIgnore))

            val colorRes = if (nightModeFlag) R.color.drawing_completion_divider_night
            else R.color.drawing_completion_divider_light
            outerOKView.findViewById<View>(R.id.v_divider).setBackgroundColor(context.getColor(colorRes))
        }
    }

    private fun executeDrawingTimeTask(timerTaskEnd: () -> Unit) {
        LogUtils.d("executeDrawingTimeTask")
        timerDrawing = Timer().apply {
            schedule(object : TimerTask() {
                var countDown = TIME_DRAWING_TIME_OUT
                override fun run() {
                    if (countDown >= 0) {
                        countDown--
                    } else {
                        timerTaskEnd.invoke()
                        cancelDrawingTimerTask()
                    }
                }
            }, 0, 1000L)
        }
    }

    private fun cancelDrawingTimerTask() {
        LogUtils.d("cancelDrawingTimerTask")
        timerDrawing?.cancel()
        timerDrawing = null
        drawingTimeoutFlag = false
    }

    private fun executeChangeDrawingProgressTask(
        progressChangeBlock: (progress: Float) -> Unit,
        timerTaskEnd: () -> Unit
    ) {
        LogUtils.d("executeChangeDrawingProgressTask")
        var progrss = 0f
        progressChangeBlock.invoke(progrss)
        timerDrawingProgress = Timer().apply {
            schedule(object : TimerTask() {
                val step = PROGRESS_DRAWING_WAIT_SERVER * 100 * 1.0f / TIME_DRAWING_PROGRESS_TIME
                var countDown = TIME_DRAWING_PROGRESS_TIME
                override fun run() {
                    LogUtils.d("drawing progress change, countDown:$countDown")
                    if (countDown > 0) {
                        countDown -= 100
                        progrss += step
                        LogUtils.d("drawing progress change, progress:$progrss")
                        progressChangeBlock.invoke(progrss)
                    } else {
                        timerTaskEnd.invoke()
                        cancelChangeDrawingProgressTask()
                    }
                }
            }, 100, 100L)
        }
    }

    private fun cancelChangeDrawingProgressTask() {
        LogUtils.d("cancelChangeDrawingProgressTask")
        timerDrawingProgress?.cancel()
        timerDrawingProgress = null
    }

    private fun getSplitScreenState(): Int {
        val splitScreenState = Settings.System.getInt(
            context.contentResolver,
            KEY_SPLIT_SCREEN_STATUS,
            SPLIT_SCREEN_STATUS_EXIT
        )
        LogUtils.d("getSplitScreenState splitScreenState:$splitScreenState")
        return splitScreenState
    }

    private fun getScreenStateStr(splitScreenState: Int): String {
        return if (splitScreenState == SPLIT_SCREEN_STATUS_ENTER) "分屏" else "全屏"
    }

    fun onSplitScreenStateChange() {
        val splitScreenState = getSplitScreenState()
        if (currentSplitScreenStatus != splitScreenState) {
            currentSplitScreenStatus = splitScreenState
            LogUtils.d(
                "onSplitScreenStateChange, split screen change, and now is:" +
                        getScreenStateStr(splitScreenState)
            )
        }
    }
}

interface DrawingStateCallback {

    fun onDrawingStateChanged(drawingStateJson: String)

    fun onDrawingTimeout()

    fun onProgressChange(progress: Float)

}