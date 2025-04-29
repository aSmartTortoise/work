package com.voyah.voice.main.ui.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.voyah.viewcmd.VoiceViewCmdUtils
import com.voyah.voice.framework.base.BaseDialog
import com.voyah.voice.framework.base.BaseDialogFragment
import com.voyah.voice.framework.util.SystemConfigUtil
import com.voyah.voice.main.R
import com.voyah.voice.main.adapter.DrawingGoodsAdapter
import com.voyah.voice.main.databinding.DialogDrawingTimesGoodsBinding
import com.voyah.voice.main.decoration.DrawingGoodsItemDecoration
import com.voyah.vcos.EVENT_TYPE_TO_GOODS_DETAIL
import com.voyah.vcos.postReport
import com.voyah.voice.main.ui.view.TitleBarListener
import com.xiaoma.xmsdk.XmSdk
import com.xiaoma.xmsdk.constant.RequestType
import com.xiaoma.xmsdk.model.GoodsBean

/**
 *  author : jie wang
 *  date : 2024/8/27 19:23
 *  description :
 */
class DrawingTimesGoodsDialog {

    companion object {

    }

    class Builder(activity: FragmentActivity) : BaseDialogFragment.Builder<Builder>(activity) {

        companion object {
            const val DIALOG_TAG = "drawing_times_goods"
        }

        private val dataBinding = DialogDrawingTimesGoodsBinding.inflate(LayoutInflater.from(activity))

        init {
            initView()
        }

        lateinit var goodsAdapter: DrawingGoodsAdapter

        private fun initView() {
            setContentView(dataBinding.root)
            val dialogWidth = activity.resources.getDimension(R.dimen.dp_900)
            setWidth(dialogWidth.toInt())
            setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
            setAnimStyle(BaseDialog.AnimStyle.DEFAULT)

            dataBinding.titleBar.let {
                VoiceViewCmdUtils.setViewCmd(it.getLeftBackContainer(), "关闭")
                it.setTitleBarListener(object : TitleBarListener {
                    override fun onLeftClick() {
                        dismiss()
                    }
                })
                it.post {
                    LogUtils.d("title bar height:${dataBinding.titleBar.height}")
                }
            }

            goodsAdapter = DrawingGoodsAdapter().apply {
                onItemClickListener = {itemView, position ->
                    LogUtils.d("onItemClick position:$position")
                    XmSdk.getAppStoreHandler()?.startDetailActivity(
                        0,
                        RequestType.GODDS_AI_DRAWING,
                        getData()[position].id)
                    postReport(itemView, EVENT_TYPE_TO_GOODS_DETAIL)
                }
            }
            dataBinding.rvGoods.apply {
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(DrawingGoodsItemDecoration(activity))
                adapter = goodsAdapter
            }
        }

        override fun show(): BaseDialog {
            fragmentTag = DIALOG_TAG
            return super.show().apply {
                LogUtils.d("show....")
            }
        }

        fun bindData(goodsList: List<GoodsBean>?) {
            if (goodsList?.isNotEmpty() == true) {
                dataBinding.rvGoods.visibility = View.VISIBLE
                dataBinding.llEmpty.visibility = View.GONE
//                goodsAdapter.setData(goodsList)
            } else {
                dataBinding.rvGoods.visibility = View.GONE
                dataBinding.llEmpty.visibility = View.VISIBLE
                dataBinding.ivEmpty.setImageResource(getPlaceHolderEmptyRes())
            }

        }

        private fun getPlaceHolderEmptyRes(): Int {
            val nightModeFlag = SystemConfigUtil.isNightMode(context)
            LogUtils.d("getPlaceHolderEmptyRes:$nightModeFlag")
            val res = if (nightModeFlag) R.drawable.icon_place_holder_empty_night
            else R.drawable.icon_place_holder_empty_light
            return res
        }

    }



}