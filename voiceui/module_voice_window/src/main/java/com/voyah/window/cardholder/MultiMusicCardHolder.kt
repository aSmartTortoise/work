package com.voyah.window.cardholder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.blankj.utilcode.util.LogUtils
import com.voyah.cockpit.window.model.Contact
import com.voyah.cockpit.window.model.MultiMusicInfo
import com.voyah.cockpit.window.model.MultimediaInfo
import com.voyah.voice.card.indicator.VerticalDotsIndicator
import com.voyah.window.R

/**
 *  author : jie wang
 *  date : 2024/6/21 15:29
 *  description :
 */
class MultiMusicCardHolder(context: Context) : BasePageCardHolder<MultiMusicInfo>(context) {

    public val multimediaCardLargeHeight: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_1040)
    }

    public val multimediaCardLargeWidth: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_1390)
    }

    private val multimediaCardHeight: Int by lazy(LazyThreadSafetyMode.NONE) {
        context.resources.getDimensionPixelSize(R.dimen.dp_396)
    }

    private val columnNum = 2

    var vpAttachStateChangeListener: View.OnAttachStateChangeListener? = null

    var ivMusic: ImageView? = null
    var tvMusic: TextView? = null

    companion object {
        const val TAG = "MultimediaCardHolder"
    }


    override fun initView(rootView: View) {
        super.initView(rootView)
        vp?.apply {
            addOnAttachStateChangeListener(getAttachStateChangeListener())
        }
        ivMusic = rootView.findViewById<ImageView>(R.id.iv_music)
        tvMusic = rootView.findViewById<TextView>(R.id.tv_music)
    }


    override fun bindData(data: List<MultiMusicInfo>) {
        LogUtils.d("bindData")
        chunkSize = 8
        if(data.get(0).sourceType == MultiMusicInfo.SourceType.WY_MUSIC){
            ivMusic?.setImageResource(R.drawable.icon_multimusic_tag_wy)
            tvMusic?.text = context.getString(R.string.music_source_wy)
        } else {
            ivMusic?.setImageResource(R.drawable.icon_multimusic_tag_qq)
            tvMusic?.text = context.getString(R.string.music_source_qq)
        }
        super.bindData(data)
    }

    override fun onCardCollapse() {
       super.onCardCollapse()
        if (vp != null && vpAttachStateChangeListener != null) {
            vp!!.removeOnAttachStateChangeListener(vpAttachStateChangeListener)
        }
        vpAttachStateChangeListener = null
    }

    private fun getAttachStateChangeListener() : View.OnAttachStateChangeListener {
        if (vpAttachStateChangeListener == null) {
            vpAttachStateChangeListener = object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    LogUtils.d("onViewAttachedToWindow")

                }

                override fun onViewDetachedFromWindow(v: View) {
                    LogUtils.d("onViewDetachedFromWindow")
                }

            }
        }
        return vpAttachStateChangeListener!!
    }

    override fun getCardHeight(data: List<MultiMusicInfo>, parentViewWidth: Int): Int {
        val rowNum = (data.size + columnNum - 1) / columnNum
        LogUtils.d("getCardHeight rowNum:$rowNum")
        val cardHeight = multimediaCardLargeHeight
        val cardWidth = multimediaCardLargeWidth
        LogUtils.d("getCardHeight:$cardHeight")
        rootView?.let {
            val layoutParams = it.layoutParams
            layoutParams.height = cardHeight
            layoutParams.width = cardWidth
            it.layoutParams = layoutParams
        }
        return cardHeight
    }

    override fun onUIModeChange(nightModeFlag: Boolean) {
    }

    override fun getCardView(): View {
        val cardView = LayoutInflater.from(context).inflate(R.layout.card_music_page, null, false)
        initView(cardView)
        rootView = cardView
        return cardView
    }

}