package com.voyah.aiwindow.ui.widget.vpa

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.blankj.utilcode.util.LogUtils
import com.voyah.aiwindow.R
import com.voyah.aiwindow.common.ViewSizeChangeAnimation

class VpaAnimationView : ConstraintLayout {

    private var vpaSurfaceView: VpaSurfaceView?
    private var asrTextView: TextView
    private var asrLayout: LinearLayout
    private var skillLayout: LinearLayout
    private var viewLine: View

    @Volatile
    private var currTag: String? = ""

    var vpaClickCallback: (() -> Unit)? = null
    var skillShowCallback: ((show: Boolean) -> Unit)? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        val view: View = LayoutInflater.from(context).inflate(R.layout.view_vpa, this, true)
        vpaSurfaceView = view.findViewById(R.id.iv_asr_anim)
        asrLayout = view.findViewById(R.id.layout_asr)
        asrTextView = view.findViewById(R.id.tv_desc)
        skillLayout = view.findViewById(R.id.layout_skill)
        viewLine = view.findViewById(R.id.view_line)

        asrLayout.setOnClickListener {
            vpaClickCallback?.invoke()
        }

        skillLayout.setOnClickListener {}
    }

    fun startSpeaking() {
        vpaSurfaceView?.startDraw(VpaState.SPEAKING)
    }

    fun startListening() {
        vpaSurfaceView?.startDraw(VpaState.LISTENING)
    }

    fun startAsr() {
        LogUtils.v("startAsr() called")
        vpaSurfaceView?.startDraw(VpaState.LISTENING)
        dismissSkillCard()
        changeWidthAnim(
            asrLayout,
            VpaWindowConfig.getVpaInitWidth(), 300
        )
        asrTextView.postDelayed({ asrTextView.text = resources.getText(R.string.vpa_listening) }, 100)
    }

    fun stop() {
        LogUtils.d("stop")
        vpaSurfaceView?.stop()
        vpaSurfaceView?.visibility = GONE
    }

    fun release() {
        LogUtils.d("release")
        removeAllViews()
        vpaSurfaceView?.release()
    }

    fun setText(tag: String?) {
        val str = tag ?: ""
        LogUtils.v("setText=$str")
        changeWidthAnim(asrLayout, VpaWindowConfig.getVpaExpandWidth(), 300)
        asrTextView.postDelayed({ asrTextView.text = str }, 100)

    }

    /**
     * 显示技能
     */
    @Synchronized
    fun showSkillCard(skillParam: SkillParams) {
        if (skillParam.tag != currTag) {
            currTag = skillParam.tag
            skillLayout.removeAllViews()
            skillLayout.addView(
                skillParam.view,
                LinearLayout.LayoutParams(skillParam.w!!, skillParam.h!!)
            )

            skillLayout.startAnimation(createInsertAnim())
            controlSkillShow(true)
            val width =
                if (skillParam.w!! < 0) VpaWindowConfig.getVpaExpandWidth() else skillParam.w
            changeWidthAnim(asrLayout, width!!)
        }
    }

    /**
     * 隐藏技能
     */
    @Synchronized
    fun dismissSkillCard(tag: String="") {
        LogUtils.d("dismissSkillCard tag=$tag,currTag=$currTag")
        if (TextUtils.isEmpty(tag) || tag == currTag) {
            skillLayout.removeAllViews()
            currTag = ""
            controlSkillShow(false)
        }
    }

    @Synchronized
    fun isSkillCardShow(tag: String = ""): Boolean {
        LogUtils.v("isSkillCardShow tag=$tag ,currTag=$currTag")
        if (TextUtils.isEmpty(currTag)) {
            return false
        }
        if (TextUtils.isEmpty(tag)) {
            return !TextUtils.isEmpty(currTag)
        }
        return tag == currTag
    }

    /**
     * 显示/隐藏 技能ui
     * @param show Boolean
     */
    private fun controlSkillShow(show: Boolean) {
        if (show) {
            skillLayout.visibility = View.VISIBLE
            viewLine.visibility = View.VISIBLE
            asrLayout.setBackgroundResource(R.drawable.vpa_bg_asr)
        } else {
            skillLayout.visibility = View.GONE
            viewLine.visibility = View.GONE
            asrLayout.setBackgroundResource(R.drawable.vpa_bg)
        }
        skillShowCallback?.invoke(show)
    }


    /**
     * 改变打字机宽度动画
     * @param view view
     * @param vpaWidth Int
     */
    private fun changeWidthAnim(
        view: View, vpaWidth: Int,
        duration: Long = 160
    ) {
        view.animation?.let {
            it.cancel()
        }
        LogUtils.v("view.w=${view.width}, vpaWidth=$vpaWidth")
        if (vpaWidth != view.width) {
            val animation: Animation =
                ViewSizeChangeAnimation(view, vpaWidth)
            animation.duration = duration
            view.startAnimation(animation)
        }
    }

    /**
     * 技能view 进入动画
     * @return ScaleAnimation
     */
    private fun createInsertAnim(): ScaleAnimation {
        //平移动画
        return ScaleAnimation(
            0F,
            1F,
            0F,
            1F
        ).apply {
            this.duration = 160
        }
    }
}