package com.voyah.voice.main.ui.dialog

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.lib.common.voyah.ServiceFactory
import com.voyah.vcos.manager.MegaDisplayHelper
import com.voyah.viewcmd.VoiceViewCmdUtils
import com.voyah.voice.framework.helper.AppHelper
import com.voyah.voice.framework.util.SystemConfigUtil
import com.voyah.voice.main.R
import com.voyah.voice.main.adapter.DrawingGoodsAdapter
import com.voyah.voice.main.databinding.DialogDrawingTimesGoodsBinding
import com.voyah.voice.main.decoration.DrawingGoodsItemDecoration
import com.voyah.vcos.EVENT_TYPE_TO_GOODS_DETAIL
import com.voyah.vcos.postReport
import com.voyah.voice.main.model.DrawingTimesGoods
import com.voyah.voice.main.ui.BaseDialogFragment
import com.voyah.voice.main.ui.view.TitleBarListener
import com.xiaoma.xmsdk.XmSdk
import com.xiaoma.xmsdk.constant.RequestType

/**
 *  author : jie wang
 *  date : 2025/1/11 15:53
 *  description :
 */
class DrawingTimesGoodsFragment: BaseDialogFragment() {

    private lateinit var binding:  DialogDrawingTimesGoodsBinding
    private lateinit var goodsAdapter: DrawingGoodsAdapter
    private var goodsList: ArrayList<DrawingTimesGoods>? = null

    companion object {

        fun newInstance(drawingTimesGoodsList: List<DrawingTimesGoods>?): DrawingTimesGoodsFragment {
            val frag = DrawingTimesGoodsFragment()
            val args = Bundle()
            val arrayList = drawingTimesGoodsList?.let { ArrayList(it) }
            args.putParcelableArrayList("data", arrayList)
            frag.arguments = args
            return frag
        }
    }

    override fun getParamsWidth(): Int {
        return context?.resources?.getDimensionPixelSize(R.dimen.dp_900) ?: super.getParamsWidth()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDrawingTimesGoodsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtils.d("onViewCreated")
        initView()
        initData()
    }

    private fun initView() {
        LogUtils.d("initView")
        binding.titleBar.let {
            VoiceViewCmdUtils.setViewCmd(it.getLeftBackContainer(), "关闭")
            it.setTitleBarListener(object : TitleBarListener {
                override fun onLeftClick() {
                    dismiss()
                }
            })
            it.post {
                LogUtils.d("title bar height:${binding.titleBar.height}")
            }
        }

        goodsAdapter = DrawingGoodsAdapter().apply {
            onItemClickListener = { itemView, position ->
                LogUtils.d("onItemClick position:$position")
                val displayId = ServiceFactory.getInstance().voiceService.displayId
                XmSdk.getAppStoreHandler()?.startDetailActivity(
                    displayId,
                    RequestType.GODDS_AI_DRAWING,
                    getData()[position].id
                )
                postReport(itemView, EVENT_TYPE_TO_GOODS_DETAIL)
            }
        }
        binding.rvGoods.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DrawingGoodsItemDecoration(context))
            adapter = goodsAdapter
        }
    }

    private fun initData() {
        LogUtils.d("initData")
        goodsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelableArrayList("data", DrawingTimesGoods::class.java)
        } else {
            requireArguments().getParcelableArrayList("data")
        }
        if (goodsList?.isNotEmpty() == true) {
            binding.rvGoods.visibility = View.VISIBLE
            binding.llEmpty.visibility = View.GONE
            goodsAdapter.setData(goodsList)
        } else {
            binding.rvGoods.visibility = View.GONE
            binding.llEmpty.visibility = View.VISIBLE
            binding.ivEmpty.setImageResource(getPlaceHolderEmptyRes())
        }
    }

    private fun getPlaceHolderEmptyRes(): Int {
        val nightModeFlag = SystemConfigUtil.isNightMode(AppHelper.getApplication())
        LogUtils.d("getPlaceHolderEmptyRes:$nightModeFlag")
        val res = if (nightModeFlag) R.drawable.icon_place_holder_empty_night
        else R.drawable.icon_place_holder_empty_light
        return res
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        LogUtils.d("onSaveInstanceState")
        if (goodsList?.isNotEmpty() == true) {
            outState.putParcelableArrayList("data", goodsList)
        }
    }

}