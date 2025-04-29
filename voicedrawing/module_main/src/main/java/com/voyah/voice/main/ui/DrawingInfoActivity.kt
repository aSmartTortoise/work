package com.voyah.voice.main.ui

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.vcos.common.widgets.vcostoast.VcosToastManager
import com.voice.drawing.api.model.DrawingInfo
import com.voice.drawing.api.model.DrawingInfoDatabase
import com.voice.drawing.api.model.DrawingState
import com.voyah.ai.sdk.manager.TTSManager
import com.voyah.common.model.DrawingTimesInfo
import com.voyah.viewcmd.VoiceViewCmdUtils
import com.voyah.viewcmd.aspect.VoiceAutoRefresh
import com.voyah.voice.framework.base.BaseMvvmActivity
import com.voyah.voice.framework.helper.AppHelper
import com.voyah.voice.framework.util.NetworkUtil
import com.voyah.voice.main.R
import com.voyah.voice.main.constant.TAG_DRAWING_LOADING
import com.voyah.voice.main.constant.TAG_DRAWING_TIMES_GOODS
import com.voyah.voice.main.databinding.ActivityDrawingInfoBinding
import com.voyah.voice.main.ext.setStateAnimatorList
import com.voyah.voice.main.image.DrawingTarget
import com.voyah.voice.main.model.DrawingTimesGoods
import com.voyah.voice.main.model.RemainTimes
import com.voyah.voice.main.presenter.DrawingStateCallback
import com.voyah.voice.main.service.DrawingAPIBinder
import com.voyah.voice.main.ui.dialog.DrawingTimesGoodsFragment
import com.voyah.voice.main.ui.dialog.LoadingDialogFragment
import com.voyah.voice.main.ui.view.TitleBarListener
import com.voyah.voice.main.viewmodel.DrawingGoodsViewModel
import com.voyah.voice.main.viewmodel.DrawingInfoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.TimerTask


class DrawingInfoActivity : BaseMvvmActivity<ActivityDrawingInfoBinding, DrawingInfoModel>() {

    private var drawingInfo: DrawingInfo? = null

    companion object {
        const val KEY_DRAWING_STATE = "drawingState"
        const val KEY_DRAWING_INFO = "drawingInfo"
        const val KEY_LOADING_FAIL_FLAG = "loadingFailFlag"
        const val TIME_REDRAW_LOADING_TIME_OUT = 500
        const val TIME_REDRAW_TIME_OUT = 2000

        fun startDrawingInfo(context: Context, drawingStateJson: String, displayId: Int) {
            LogUtils.i("startDrawingInfo displayId:$displayId")
            val intent = Intent(context, DrawingInfoActivity::class.java).apply {
                putExtra(KEY_DRAWING_STATE, drawingStateJson)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            try {
                context.startActivity(intent, ActivityOptions.makeBasic().apply {
                    setLaunchDisplayId(displayId)
                }.toBundle())
            } catch (e: Exception) {
                LogUtils.w("startDrawingInfo e:$e")
            }
        }
    }

    private val gson by lazy { Gson() }
    private var historyDialog: HistoryDialogFragmentK? = null
    private var goodsDialog: DrawingTimesGoodsFragment? = null
    private var loadingDialog: LoadingDialogFragment? = null
    private var timerRedrawLoading: Timer? = null
    private var timerRedraw: Timer? = null

    @Volatile
    private var redrawTaskTimeOutFlag = false

    private val drawingGoodsViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this)[DrawingGoodsViewModel::class.java]
    }

    private val drawingStateCallback = object : DrawingStateCallback {

        override fun onDrawingStateChanged(drawingStateJson: String) {
            LogUtils.d("onDrawingStateChanged")
            bindDrawingInfoData(drawingStateJson)
        }

        override fun onDrawingTimeout() {
            LogUtils.d("onDrawingTimeout")
            setDrawingFail()
            drawingInfo?.drawingState = DrawingState.TIMEOUT
        }

        @SuppressLint("SetTextI18n")
        override fun onProgressChange(progress: Float) {
            val progressDrawing = progress.toInt()
            LogUtils.d("onProgressChange progress:$progressDrawing")
            dataBinding.pb.progress = progressDrawing
            dataBinding.tvProgress.text = "$progressDrawing%"
        }
    }

    private fun bindDrawingInfoData(drawingStateJson: String) {
        LogUtils.d("bindDrawingInfoData drawingStateJson:$drawingStateJson")
        drawingInfo = gson.fromJson(drawingStateJson, DrawingInfo::class.java)
        drawingInfo?.let {
            bindDrawingInfoWithState(it, false)
        } ?:run {
            LogUtils.w("bindDrawingInfoData gson format failed...")
        }

    }

    @SuppressLint("SetTextI18n")
    @VoiceAutoRefresh
    private fun setDrawingStart() {
        dismissHistoryDialog()
        goodsDialog?.dismiss()
        goodsDialog = null
        dataBinding.btnRedraw.visibility = View.GONE
        dataBinding.btnDrawHistory.visibility = View.GONE
        dataBinding.llProgress.visibility = View.VISIBLE

        dataBinding.ivPic0.setImageResource(R.drawable.drawing_state_img_bg)
        dataBinding.ivPic1.setImageResource(R.drawable.drawing_state_img_bg)
        dataBinding.ivPic2.setImageResource(R.drawable.drawing_state_img_bg)
        dataBinding.ivPic3.setImageResource(R.drawable.drawing_state_img_bg)
        dataBinding.ivPicBig.setImageResource(R.drawable.drawing_state_img_bg)

        switchPicSize()
    }

    private fun switchPicSize() {
        drawingInfo?.let {
            if (it.picSize == 1) {
                dataBinding.ivPicBig.visibility = View.VISIBLE
                dataBinding.glPics.visibility = View.GONE
            } else if (it.picSize == 4) {
                dataBinding.ivPicBig.visibility = View.GONE
                dataBinding.glPics.visibility = View.VISIBLE
            }
        }
    }

    @VoiceAutoRefresh
    private fun setDrawingFail() {
        LogUtils.d("setDrawingFail")
        dataBinding.ivPic0.setImageResource(R.drawable.img_fail)
        dataBinding.ivPic1.setImageResource(R.drawable.img_fail)
        dataBinding.ivPic2.setImageResource(R.drawable.img_fail)
        dataBinding.ivPic3.setImageResource(R.drawable.img_fail)
        dataBinding.ivPicBig.setImageResource(R.drawable.img_fail)

        dataBinding.btnRedraw.visibility = View.VISIBLE
        dataBinding.btnDrawHistory.visibility = View.VISIBLE
        dataBinding.llProgress.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        LogUtils.d("onCreate, set drawing state callback, savedInstanceState == null ?:${savedInstanceState == null}")
        LogUtils.d("onCreate app version name is:${AppUtils.getAppVersionName()}")
        DrawingAPIBinder.getInstance().apiPresenter.setDrawingStateCallback(drawingStateCallback)
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        LogUtils.d("initView")
        dataBinding.titleBar.setPadding(0, statusBarHeight, 0, 0)

        dataBinding.titleBar.let {
            it.setTitleBarListener(object : TitleBarListener {
                override fun onLeftClick() {
                    LogUtils.d("onLeftClick")
                    MainActivity.start(this@DrawingInfoActivity)
                }
            })
            VoiceViewCmdUtils.setViewCmd(it.getLeftBackContainer(), "返回")
        }


        dataBinding.btnRedraw.let { btn ->
            btn.setStateAnimatorList()
            ClickUtils.applySingleDebouncing(btn, 1000L) {
                val netConnectedFlag = NetworkUtil.isConnected(this)
                LogUtils.d("redraw click netConnectedFlag:$netConnectedFlag")
                if (!netConnectedFlag) {
                    VcosToastManager.Companion.instance?.showToast(
                        this@DrawingInfoActivity,
                        getString(R.string.network_error)
                    )
                } else {
                    drawingInfo?.let {
                        LogUtils.d("redraw prompt:${it.prompt}")
                        if (!TextUtils.isEmpty(it.prompt)) {
                            viewModel.getRemainingDrawingTimesNew()
                            cancelRedrawLoadingTimerTask()
                            executeRedrawLoadingTimeTask {
                                LogUtils.d("redraw loading time out...")
                                lifecycleScope.launch {
                                    loadingDialog = LoadingDialogFragment().apply {
                                        show(supportFragmentManager, TAG_DRAWING_LOADING)
                                    }
                                }

                            }
                            redrawTaskTimeOutFlag = false
                            cancelRedrawTimerTask()
                            executeRedrawTimeTask {
                                LogUtils.d("redraw task time out...")
                                redrawTaskTimeOutFlag = true
                                lifecycleScope.launch {
                                    cancelRedrawLoadingTimerTask()
                                    loadingDialog?.dismiss()
                                    VcosToastManager.Companion.instance?.showToast(
                                        this@DrawingInfoActivity,
                                        getString(R.string.network_error)
                                    )
                                }
                            }
                        }
                    }
                }
                viewModel.postRedrawEvent(dataBinding.btnRedraw)
            }
        }


        dataBinding.btnDrawHistory.let { btn ->
            btn.setStateAnimatorList()
            ClickUtils.applySingleDebouncing(btn, 1000L) {
                lifecycleScope.launch(Dispatchers.Default) {
                    val all =
                        DrawingInfoDatabase.getInstance(applicationContext).drawingInfoDao().all
                    withContext(Dispatchers.Main) {
                        historyDialog = HistoryDialogFragmentK.newInstance(all)
                        historyDialog?.show(supportFragmentManager, "history")
                    }
                }
                viewModel.postDrawingHistoryEvent(dataBinding.btnDrawHistory)
            }
        }

        dataBinding.ivPic0.setOnClickListener {
            drawingInfo?.let {
                val index = 0
                toBrowseImg(it, index)
            }
        }

        dataBinding.ivPic1.setOnClickListener {
            drawingInfo?.let {
                val index = 1
                toBrowseImg(it, index)
            }
        }

        dataBinding.ivPic2.setOnClickListener {
            drawingInfo?.let {
                val index = 2
                toBrowseImg(it, index)
            }
        }

        dataBinding.ivPic3.setOnClickListener {
            drawingInfo?.let {
                val index = 3
                toBrowseImg(it, index)
            }
        }

        dataBinding.ivPicBig.setOnClickListener {
            drawingInfo?.let {
                val index = 0
                toBrowseImg(it, index)
            }
        }

    }

    private fun toBrowseImg(it: DrawingInfo, index: Int) {
        if (it.urlList != null && it.urlList.size > index) {
            val imgList = arrayListOf<String>().apply {
                addAll(it.urlList)
            }

            ViewActivity.start(
                this@DrawingInfoActivity,
                imgList,
                index,
                true
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadImgs(imgUrlArr: List<String>, totalSize: Int) {
        LogUtils.d("loadImgs totalSize:$totalSize")
        switchPicSize()
        if (totalSize == 4) {
            setUrlRound(dataBinding.ivPic0, imgUrlArr[0])
            setUrlRound(dataBinding.ivPic1, imgUrlArr[1])
            setUrlRound(dataBinding.ivPic2, imgUrlArr[2])
            setUrlRound(dataBinding.ivPic3, imgUrlArr[3])
        } else if (totalSize == 1) {
            setUrlRound(dataBinding.ivPicBig, imgUrlArr[0])
        }

    }

    private fun setUrlRound(iv: ImageView, url: String?) {
        Glide.with(AppHelper.getApplication())
            .asFile()
            .load(url)
            .apply(RequestOptions()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.DATA))
            .into(DrawingTarget(
                drawingInfo!!,
                onLoadStart = {drawingInfoTarget ->
                    LogUtils.d("onLoadStart")
                    if (!checkDrawingInfoTarget(drawingInfoTarget)) {

                    }
                },
                onLoadFailed = {drawingInfoTarget ->
                    LogUtils.d("onLoadFailed")
                    if (!checkDrawingInfoTarget(drawingInfoTarget)) {
                        val loadFailFlag = drawingInfoTarget.isLoadFailFlag
                        val playTTSFlag = drawingInfoTarget.isPlayTTSFlag
                        LogUtils.i("onLoadFailed loadFailFlag:$loadFailFlag, " +
                                "playTTSFlag:$playTTSFlag")
                        if (!loadFailFlag) {
                            drawingInfoTarget.isLoadFailFlag = true
                            VcosToastManager.Companion.instance?.showToast(
                                this@DrawingInfoActivity,
                                getString(R.string.network_error)
                            )
                            if (playTTSFlag) {
                                TTSManager.speak(getString(R.string.load_picture_error))
                            }
                        }
                        iv.setImageResource(R.drawable.img_fail)
                        onTargetLoadCompletion(drawingInfoTarget)
                    }
                },
                onResourceReady = {drawingInfoTarget, file ->
                    LogUtils.d("onResourceReady")
                    if (!checkDrawingInfoTarget(drawingInfoTarget)) {
                        Glide.with(this)
                            .load(file)
                            .skipMemoryCache(false)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .into(iv)
                        onTargetLoadCompletion(drawingInfoTarget)
                    }
                }
            ))
    }

    private fun checkDrawingInfoTarget(drawingInfoTarget: DrawingInfo): Boolean {
        var requestTargetInvalid = false
        if (!TextUtils.equals(drawingInfoTarget.requestId, drawingInfo?.requestId)) {
            requestTargetInvalid = true
        }
        LogUtils.i("checkDrawingInfoTarget drawingInfoTarget is invalid ? $requestTargetInvalid")
        return requestTargetInvalid
    }

    @SuppressLint("SetTextI18n")
    private fun onTargetLoadCompletion(drawingInfoTarget: DrawingInfo) {
        drawingInfoTarget.completeCount += 1

        val completeCount = drawingInfoTarget.completeCount
        LogUtils.d("onTargetLoadCompletion completionCount:$completeCount")
        if (drawingInfoTarget.picSize == 4) {
            dataBinding.pb.let {
                val progress = it.progress + 10 / 4
                it.progress = progress
                dataBinding.tvProgress.text = "$progress%"
            }
        } else if (drawingInfoTarget.picSize == 1) {
            dataBinding.pb.let {
                val progress = 100
                it.progress = progress
                dataBinding.tvProgress.text = "$progress%"
            }
        }

        if (completeCount == drawingInfoTarget.picSize) {
            onAllLoadCompletion()
            val loadFailFlag = drawingInfoTarget.isLoadFailFlag
            val playTTSFlag = drawingInfoTarget.isPlayTTSFlag
            LogUtils.d("onTargetLoadCompletion loadFailFlag:$loadFailFlag, playTTSFlag:$playTTSFlag")
            if (!loadFailFlag && playTTSFlag) {
                val ttsText = drawingInfoTarget.ttsText
                LogUtils.i("onTargetLoadCompletion target drawingInfo tts is:$ttsText")
                if (!TextUtils.isEmpty(ttsText)) {
                    TTSManager.speak(ttsText)
                }
            }
        }
    }

    @VoiceAutoRefresh
    private fun onAllLoadCompletion() {
        dataBinding.btnRedraw.visibility = View.VISIBLE
        dataBinding.btnDrawHistory.visibility = View.VISIBLE
        dataBinding.llProgress.visibility = View.GONE
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        LogUtils.d("initData... savedInstanceState == null? ${(savedInstanceState == null)}")
        savedInstanceState?.let {
            val parcelable = it.getParcelable<DrawingInfo>(KEY_DRAWING_INFO)
            if (parcelable is DrawingInfo) {
                drawingInfo = parcelable
                bindDrawingInfoWithState(drawingInfo!!, true)
            }
        } ?: run {
            LogUtils.d("initData, getIntent")
            intent?.let {
                val drawingStateJson = it.getStringExtra(KEY_DRAWING_STATE)
                if (!TextUtils.isEmpty(drawingStateJson)) {
                    bindDrawingInfoData(drawingStateJson!!)
                }
            }
        }

        drawingGoodsViewModel.drawingGoodsCallback =
            object : DrawingGoodsViewModel.DrawingGoodsCallback {
                override fun onGetDrawingGoods(goods: List<DrawingTimesGoods>?) {
                    LogUtils.d("onGetDrawingGoods redrawTaskTimeOutFlag:$redrawTaskTimeOutFlag")
                    cancelRedrawLoadingTimerTask()
                    loadingDialog?.dismiss()
                    if (!redrawTaskTimeOutFlag) {
                        goodsDialog = DrawingTimesGoodsFragment.newInstance(goods).apply {
                            show(supportFragmentManager, TAG_DRAWING_TIMES_GOODS)
                        }

                        cancelRedrawTimerTask()
                    }
                }
            }

        viewModel.drawingTimesCallback = object : DrawingInfoModel.DrawingTimesCallback {
            override fun onGetDrawingTimes(drawingTimes: DrawingTimesInfo?) {

            }

            override fun onGetRemainTimes(remainTimes: RemainTimes.ResultDTO) {
                LogUtils.d("onGetRemainTimes redrawTaskTimeOutFlag:$redrawTaskTimeOutFlag")
                if (!redrawTaskTimeOutFlag) {
                    if (remainTimes.totalRemainTimes <= 0) {
                        LogUtils.d("getDrawingGoods")
                        drawingGoodsViewModel.getDrawingGoods()
                    } else {
                        cancelRedrawLoadingTimerTask()
                        cancelRedrawTimerTask()
                        loadingDialog?.dismiss()
                        drawingInfo?.let { drawingInfo ->
                            LogUtils.d("prompt:${drawingInfo.prompt}")
                            DrawingAPIBinder.getInstance().redraw(drawingInfo.prompt)
                        }
                    }
                }
            }

            override fun onError() {
                LogUtils.d("onError redrawTaskTimeOutFlag:$redrawTaskTimeOutFlag")
                cancelRedrawLoadingTimerTask()
                loadingDialog?.dismiss()
                if (!redrawTaskTimeOutFlag) {
                    cancelRedrawTimerTask()
                    VcosToastManager.Companion.instance?.showToast(
                        this@DrawingInfoActivity,
                        getString(R.string.network_error)
                    )
                }
            }
        }
    }

    private fun bindDrawingInfoWithState(drawingInfo: DrawingInfo, isRestoreState: Boolean) {
        val drawingState = drawingInfo.drawingState
        LogUtils.d("bindDrawingInfoWithState drawingState:$drawingState, isRestoreState:$isRestoreState")
        when (drawingState) {
            DrawingState.START -> {
                setDrawingStart()
            }

            DrawingState.COMPLETION -> {
                val urlList = drawingInfo.urlList
                if (urlList != null) {
                    if (isRestoreState) {
                        drawingInfo.isPlayTTSFlag = false
                        drawingInfo.completeCount = 0
                    }

                    loadImgs(urlList, drawingInfo.picSize)
                }
            }

            DrawingState.TIMEOUT,
            DrawingState.FAIL -> {
                setDrawingFail()
            }
        }
    }

    private fun executeRedrawLoadingTimeTask(timerTaskEnd: () -> Unit) {
        LogUtils.d("executeRedrawLoadingTimeTask")
        timerRedrawLoading = Timer().apply {
            schedule(object : TimerTask() {
                var countDown = TIME_REDRAW_LOADING_TIME_OUT / 100
                override fun run() {
                    LogUtils.d("redraw loading countDown:$countDown")
                    if (countDown > 0) {
                        countDown--
                    } else {
                        cancelRedrawLoadingTimerTask()
                        timerTaskEnd.invoke()
                    }
                }
            }, 0, 100L)
        }
    }

    private fun cancelRedrawLoadingTimerTask() {
        LogUtils.d("cancelRedrawLoadingTimerTask")
        timerRedrawLoading?.cancel()
        timerRedrawLoading = null
    }

    private fun executeRedrawTimeTask(timerTaskEnd: () -> Unit) {
        LogUtils.d("executeRedrawTimeTask")
        timerRedraw = Timer().apply {
            schedule(object : TimerTask() {
                var countDown = TIME_REDRAW_TIME_OUT / 1000
                override fun run() {
                    LogUtils.d("redraw task countDown:$countDown")
                    if (countDown > 0) {
                        countDown--
                    } else {
                        cancelRedrawTimerTask()
                        timerTaskEnd.invoke()
                    }
                }
            }, 0, 1000L)
        }
    }

    private fun cancelRedrawTimerTask() {
        LogUtils.d("cancelRedrawTimerTask")
        timerRedraw?.cancel()
        timerRedraw = null
    }

    override fun onRestart() {
        super.onRestart()
        LogUtils.d("onRestart")
    }

    override fun onStart() {
        super.onStart()
        LogUtils.d("onStart")
    }

    override fun onResume() {
        super.onResume()
        LogUtils.d("onResume.")
        drawingInfo?.let {
            if (it.drawingState == DrawingState.START) {
                dismissHistoryDialog()
            }
        }
    }

    private fun dismissHistoryDialog() {
        LogUtils.i("dismissHistoryDialog")
        historyDialog?.dismissAllDialog() ?: run {
            LogUtils.i("dismissHistoryDialog history dialog is null...")
        }
        historyDialog = null
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        LogUtils.d("onNewIntent")
        intent?.let {
            val drawingStateJson = it.getStringExtra(KEY_DRAWING_STATE)
            if (!TextUtils.isEmpty(drawingStateJson)) {
                bindDrawingInfoData(drawingStateJson!!)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        LogUtils.d("onPause")
    }

    override fun onStop() {
        super.onStop()
        LogUtils.d("onStop")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        LogUtils.d("onRestoreInstanceState")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LogUtils.d("onConfigurationChanged")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        LogUtils.d("onSaveInstanceState")
        if (drawingInfo != null) {
            outState.putParcelable(KEY_DRAWING_INFO, drawingInfo)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        LogUtils.d("onBackPressed")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d("onDestroy app version name is:${AppUtils.getAppVersionName()}")
        DrawingAPIBinder.getInstance().apiPresenter.setDrawingStateCallback(null)
        cancelRedrawLoadingTimerTask()
        cancelRedrawTimerTask()
    }

}