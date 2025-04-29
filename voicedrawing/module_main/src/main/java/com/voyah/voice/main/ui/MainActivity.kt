package com.voyah.voice.main.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.LogUtils
import com.lib.common.voyah.ServiceFactory
import com.vcos.common.widgets.vcostoast.VcosToastManager
import com.voice.drawing.api.model.DrawingInfoDatabase
import com.voyah.vcos.EVENT_TYPE_DRAWING_HISTORY
import com.voyah.vcos.EVENT_TYPE_REMAIN_TIME
import com.voyah.vcos.EVENT_TYPE_SELECT_STYLE
import com.voyah.viewcmd.VoiceViewCmdUtils
import com.voyah.voice.framework.report.Report
import com.voyah.voice.framework.base.BaseMvvmActivity
import com.voyah.voice.framework.util.NetworkUtil
import com.voyah.voice.main.R
import com.voyah.voice.main.adapter.BottomImageAdapter
import com.voyah.voice.main.adapter.OnSelectedChanged
import com.voyah.voice.main.adapter.PhoneViewAdapter
import com.voyah.voice.main.anim.HomeViewTransformer
import com.voyah.voice.main.constant.TAG_DRAWING_LOADING
import com.voyah.voice.main.constant.TAG_DRAWING_TIMES_GOODS
import com.voyah.voice.main.databinding.ActivityMainBinding
import com.voyah.voice.main.interfce.IShowDrawingOkWindow
import com.voyah.voice.main.model.AiDrawDataStore
import com.voyah.voice.main.model.DrawingTimesGoods
import com.voyah.voice.main.model.RemainTimes
import com.voyah.voice.main.ui.dialog.DrawingTimesGoodsFragment
import com.voyah.voice.main.ui.dialog.LoadingDialogFragment
import com.voyah.voice.main.viewmodel.DrawingGoodsViewModel
import com.voyah.voice.main.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.TimerTask


class MainActivity : BaseMvvmActivity<ActivityMainBinding, MainViewModel>(),
    ViewPager.OnPageChangeListener, OnSelectedChanged, IShowDrawingOkWindow, View.OnClickListener,
    View.OnTouchListener {
    companion object {
        var selectPage: Int = 0
        var selectIndex: Int = 0
        var indexAdd: Boolean = true
        const val TIME_GET_GOODS_LOADING_TIME_OUT = 500
        const val TIME_GET_GOODS_TIME_OUT = 2000
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }


    private var map: MutableMap<Int, Int> = mutableMapOf()

    private val pageTransformer by lazy(LazyThreadSafetyMode.NONE) {
        HomeViewTransformer(applicationContext)
    }

    private val drawingGoodsViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this)[DrawingGoodsViewModel::class.java]
    }

    private var timerGetGoodsLoading: Timer? = null
    private var timerGetGoods: Timer? = null

    @Volatile
    private var getGoodsTimeOutFlag = false
    private var loadingDialog: LoadingDialogFragment? = null
    private var goodsDialog: DrawingTimesGoodsFragment? = null

    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            var index = dataBinding.viewPager.currentItem
            if (index == 0) {
                indexAdd = true
                index++
            } else if (index == dataBinding.viewPager.adapter!!.count - 1) {
                indexAdd = false
                index--
            } else {
                if (indexAdd) {
                    index++
                } else {
                    index--
                }
            }
            dataBinding.viewPager.setCurrentItem(index, true)
            removeCallbacksAndMessages(null)
            sendEmptyMessageDelayed(0, 5000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.d("onCreate savedInstanceState == null ? ${savedInstanceState == null}")
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        LogUtils.d("initView")
        val viewPaperAdapter =
            PhoneViewAdapter(
                this,
                AiDrawDataStore.getInstance(applicationContext).photoItemArr,
                layoutInflater,
                this
            )
        dataBinding.viewPager.setPageTransformer(true, pageTransformer)
        dataBinding.viewPager.adapter = viewPaperAdapter
        dataBinding.viewPager.offscreenPageLimit = 1
        dataBinding.viewPager.addOnPageChangeListener(this)
        windowResized()

        dataBinding.recyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        dataBinding.recyclerView.adapter =
            BottomImageAdapter(
                this, AiDrawDataStore.getInstance(applicationContext).photoItemArr[0],
                this,
                selectIndex
            )
        dataBinding.btnHistory.setOnClickListener {
            val isViewCmd = VoiceViewCmdUtils.isClickByViewCmd(dataBinding.btnHistory)
            var clickType = Report.CLICK
            if (isViewCmd) {
                clickType = Report.VIEW_CMD
            }
            lifecycleScope.launch(Dispatchers.IO) {
                val all = DrawingInfoDatabase.getInstance(applicationContext).drawingInfoDao().all
                Log.i(TAG, "isViewCmd:$isViewCmd")
                ServiceFactory.getInstance().voiceService.postBurialPointEvent(
                    EVENT_TYPE_DRAWING_HISTORY,
                    clickType,
                    0,
                    null
                )
                Log.i(TAG, "all${all?.size}")
                withContext(Dispatchers.Main) {
                    val dialogFragment = HistoryDialogFragmentK.newInstance(all)
                    dialogFragment.show(supportFragmentManager, "history")
                }
            }
        }
        updatePage(selectPage)

        lifecycleScope.launch(Dispatchers.Default) {
            AiDrawDataStore.getInstance(applicationContext)
                .performHomeDataHttpGetRequest(applicationContext)
        }

        ClickUtils.applySingleDebouncing(dataBinding.layoutCount, 1000L) {
            ServiceFactory.getInstance().voiceService.postBurialPointEvent(
                EVENT_TYPE_REMAIN_TIME,
                Report.CLICK,
                0,
                null
            )
            val netConnectedFlag = NetworkUtil.isConnected(this)
            LogUtils.d("drawing remain time netConnectedFlag:$netConnectedFlag")
            if (!netConnectedFlag) {
                VcosToastManager.Companion.instance?.showToast(
                    this@MainActivity,
                    getString(R.string.network_error)
                )
            } else {
                drawingGoodsViewModel.getDrawingGoods()
                cancelGetGoodsLoadingTimerTask()
                executeGetGoodsLoadingTimeTask {
                    LogUtils.d("get goods loading time out...")
                    lifecycleScope.launch {
                        loadingDialog = LoadingDialogFragment().apply {
                            show(supportFragmentManager, TAG_DRAWING_LOADING)
                        }
                    }
                }
                getGoodsTimeOutFlag = false
                cancelGetGoodsTimerTask()
                executeGetGoodsTimeTask {
                    LogUtils.d("get goods task time out...")
                    getGoodsTimeOutFlag = true
                    lifecycleScope.launch {
                        cancelGetGoodsLoadingTimerTask()
                        loadingDialog?.dismiss()
                        VcosToastManager.Companion.instance?.showToast(
                            this@MainActivity,
                            getString(R.string.network_error)
                        )
                    }
                }
            }
        }

        dataBinding.viewPager.setOnTouchListener(this@MainActivity)
        dataBinding.tvWakeUp.text = ServiceFactory.getInstance().voiceService.wakeUpWord
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        drawingGoodsViewModel.drawingGoodsCallback =
            object : DrawingGoodsViewModel.DrawingGoodsCallback {
                override fun onGetDrawingGoods(goods: List<DrawingTimesGoods>?) {
                    LogUtils.d("onGetDrawingGoods getGoodsTimeOutFlag:$getGoodsTimeOutFlag.")
                    cancelGetGoodsLoadingTimerTask()
                    loadingDialog?.dismiss()
                    loadingDialog = null
                    goodsDialog?.let {
                        LogUtils.d("onGetDrawingGoods goods dialog visible:${it.isVisible}")
                        it.dismiss()
                    }
                    goodsDialog = null
                    if (!getGoodsTimeOutFlag) {
                        goodsDialog = DrawingTimesGoodsFragment.newInstance(goods).apply {
                            show(supportFragmentManager, TAG_DRAWING_TIMES_GOODS)
                        }

                        cancelGetGoodsTimerTask()
                    }
                }
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        LogUtils.d("onBackPressed")
        moveTaskToBack(true)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        updatePage(position)
    }

    @SuppressLint("SetTextI18n")
    private fun updatePage(page: Int) {
        selectPage = page
        (dataBinding.recyclerView.adapter as BottomImageAdapter).setData(
            AiDrawDataStore.getInstance(applicationContext).photoItemArr[page]
        )
        (dataBinding.viewPager.adapter as PhoneViewAdapter).setSelect(selectIndex, selectPage)
        map[selectPage] = selectIndex
        dataBinding.textDesc.text =
            "“${AiDrawDataStore.getInstance(applicationContext).photoItemArr[selectPage][selectIndex].style}风格的${
                AiDrawDataStore.getInstance(
                    applicationContext
                ).photoItemArr[selectPage][0].desc
            }”"
    }


    private fun windowResized() {
        LogUtils.i(TAG, "isInMultiWindowMode$isInMultiWindowMode")
        if (isInMultiWindowMode) {
            pageTransformer.setMask(0.1347729f)
            dataBinding.viewPager.setPadding(
                (resources.getDimension(R.dimen.dp_148_5)).toInt(),
                0,
                (resources.getDimension(R.dimen.dp_148_5)).toInt(),
                0
            )
        } else {
            pageTransformer.setMask(0.3173691f)
            dataBinding.viewPager.setPadding(
                (resources.getDimension(R.dimen.dp_406)).toInt(),
                0,
                (resources.getDimension(R.dimen.dp_406)).toInt(),
                0
            )
        }
        dataBinding.viewPager.setCurrentItem(0, true)
    }

    @Deprecated("Deprecated in Java")
    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        super.onMultiWindowModeChanged(isInMultiWindowMode)
        LogUtils.i(TAG, "onMultiWindowModeChanged")
        windowResized()
    }


    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onSelectedChanged(selected: Int, style: String, isViewCmd: Boolean) {
        selectIndex = selected
        (dataBinding.viewPager.adapter as PhoneViewAdapter).setSelect(selected, selectPage)
        map[selectPage] = selected
        dataBinding.textDesc.text =
            "“${AiDrawDataStore.getInstance(applicationContext).photoItemArr[selectPage][selectIndex].style}风格的${
                AiDrawDataStore.getInstance(
                    applicationContext
                ).photoItemArr[selectPage][0].desc
            }”"
        var type = Report.CLICK
        if (isViewCmd) {
            type = Report.VIEW_CMD
        }
        Log.i(TAG, "onSelectedChanged:$isViewCmd")
        ServiceFactory.getInstance().voiceService.postBurialPointEvent(
            EVENT_TYPE_SELECT_STYLE, type, 0, style
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        lifecycleScope.launch(Dispatchers.Default)
        {
            val remainTimes: RemainTimes? = AiDrawDataStore.getInstance(applicationContext)
                .performRemainTimeHttpGetRequest(applicationContext)
            withContext(Dispatchers.Main)
            {
                if (remainTimes != null && remainTimes.result != null) {
                    if (remainTimes.result.totalRemainTimes > 5) {
                        dataBinding.layoutCount.visibility = View.GONE
                    } else {
                        dataBinding.layoutCount.visibility = View.VISIBLE
                    }
                    if (remainTimes.result.totalRemainTimes <= 0) {
                        dataBinding.textCount.setTextColor(
                            resources.getColor(
                                R.color.home_count_text_color2,
                                null
                            )
                        )
                    } else {
                        dataBinding.textCount.setTextColor(
                            resources.getColor(
                                R.color.home_count_text_color,
                                null
                            )
                        )
                    }
                    dataBinding.textCount.text =
                        "${remainTimes.result.totalRemainTimes}/${remainTimes.result.totalTimes}"
                } else {
                    dataBinding.layoutCount.visibility = View.GONE
                }
            }
        }
        handler.removeCallbacksAndMessages(null)
        handler.sendEmptyMessageDelayed(0, 5000)
        super.onResume()
    }

    override fun onClick(v: View?) {
        val index: Int = v?.getTag(R.id.custom_tag) as Int
        val data = AiDrawDataStore.getInstance(applicationContext).photoItemArr
        val url: ArrayList<String> = arrayListOf()
        var i = 0
        while (i < data.size) {
            url.add(data[i][map.getOrDefault(i, 0)].res.toString())
            i++
        }
        ViewActivity.start(this, url, index, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN || event?.action == MotionEvent.ACTION_MOVE) {
            handler.removeCallbacksAndMessages(null)
        }
        if (event?.action == MotionEvent.ACTION_OUTSIDE || event?.action == MotionEvent.ACTION_UP) {
            handler.removeCallbacksAndMessages(null)
            handler.sendEmptyMessageDelayed(0, 5000)
        }
        return false
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d("onDestroy")
        cancelGetGoodsLoadingTimerTask()
        cancelGetGoodsTimerTask()
    }

    private fun removeDialogFragment() {
        val manager = supportFragmentManager
        val fragment =
            manager.findFragmentByTag(TAG_DRAWING_TIMES_GOODS)
        if (fragment != null) {
            LogUtils.d("removeDialogFragment fragment != null")
            val ft: FragmentTransaction = manager.beginTransaction()
            ft.remove(fragment)
            ft.commitAllowingStateLoss()
        }
    }

    private fun executeGetGoodsLoadingTimeTask(timerTaskEnd: () -> Unit) {
        LogUtils.d("executeGetGoodsLoadingTimeTask")
        timerGetGoodsLoading = Timer().apply {
            schedule(object : TimerTask() {
                var countDown = TIME_GET_GOODS_LOADING_TIME_OUT / 100
                override fun run() {
                    LogUtils.d("get goods loading countDown:$countDown")
                    if (countDown > 0) {
                        countDown--
                    } else {
                        cancelGetGoodsLoadingTimerTask()
                        timerTaskEnd.invoke()
                    }
                }
            }, 0, 100L)
        }
    }

    private fun cancelGetGoodsLoadingTimerTask() {
        LogUtils.d("cancelGetGoodsLoadingTimerTask")
        timerGetGoodsLoading?.cancel()
        timerGetGoodsLoading = null
    }

    private fun executeGetGoodsTimeTask(timerTaskEnd: () -> Unit) {
        LogUtils.d("executeGetGoodsTimeTask")
        timerGetGoods = Timer().apply {
            schedule(object : TimerTask() {
                var countDown = TIME_GET_GOODS_TIME_OUT / 1000
                override fun run() {
                    LogUtils.d("get goods task countDown:$countDown")
                    if (countDown > 0) {
                        countDown--
                    } else {
                        cancelGetGoodsTimerTask()
                        timerTaskEnd.invoke()
                    }
                }
            }, 0, 1000L)
        }
    }

    private fun cancelGetGoodsTimerTask() {
        LogUtils.d("cancelGetGoodsTimerTask")
        timerGetGoods?.cancel()
        timerGetGoods = null
    }

}