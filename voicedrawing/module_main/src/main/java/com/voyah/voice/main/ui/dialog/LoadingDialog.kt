package com.voyah.voice.main.ui.dialog

import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.LogUtils
import com.voyah.voice.framework.base.BaseDialog
import com.voyah.voice.framework.base.BaseDialogFragment
import com.voyah.voice.main.databinding.DialogLoadingBinding
import com.voyah.voice.main.ui.dialog.DrawingTimesGoodsDialog.Builder
import com.voyah.voice.main.ui.dialog.DrawingTimesGoodsDialog.Builder.Companion

/**
 *  author : jie wang
 *  date : 2024/12/28 16:41
 *  description :
 */
class LoadingDialog {

    class Builder(activity: FragmentActivity) : BaseDialogFragment.Builder<Builder>(activity) {

        companion object {
            const val DIALOG_TAG = "drawing_loading"
        }

        private val dataBinding = DialogLoadingBinding.inflate(LayoutInflater.from(activity))

        init {
            initView()
        }

        private fun initView() {
            setContentView(dataBinding.root)
            setAnimStyle(BaseDialog.AnimStyle.DEFAULT)
        }

        override fun show(): BaseDialog {
            fragmentTag = DIALOG_TAG
            return super.show().apply {
                addOnDismissListener(object : BaseDialog.OnDismissListener{
                    override fun onDismiss(dialog: BaseDialog?) {

                    }

                })
                LogUtils.d("show....")
            }
        }


    }
}