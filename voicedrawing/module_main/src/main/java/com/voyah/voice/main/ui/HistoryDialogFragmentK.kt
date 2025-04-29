package com.voyah.voice.main.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.lib.common.voyah.ServiceFactory
import com.vcos.common.widgets.vcostoast.VcosToastManager
import com.voice.drawing.api.model.DrawingInfo
import com.voice.drawing.api.model.DrawingInfoDatabase
import com.voyah.vcos.EVENT_TYPE_EDIT_HISTORY
import com.voyah.viewcmd.VoiceViewCmdUtils
import com.voyah.viewcmd.aspect.VoiceRegisterView
import com.voyah.voice.framework.report.Report
import com.voyah.voice.main.R
import com.voyah.voice.main.adapter.HistoryAdapter
import com.voyah.voice.main.databinding.DialogConfirmBinding
import com.voyah.voice.main.databinding.FragmentHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryDialogFragmentK : DialogFragment() {

    private lateinit var binding: FragmentHistoryBinding

    private lateinit var historyAdapter: HistoryAdapter

    private var lastUiMode: Int = -1
    private var alertDialog: AlertDialog? = null
    private var editState: Boolean = false

    companion object {
        const val TAG: String = "HistoryDialogFragmentK"
        fun newInstance(drawingInfoList: List<DrawingInfo>?): HistoryDialogFragmentK {
            val frag = HistoryDialogFragmentK()
            val args = Bundle()
            val arrayList = drawingInfoList?.let { ArrayList(it) }
            args.putParcelableArrayList("data", arrayList)
            frag.arguments = args
            return frag
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog.window != null) {
            dialog.window!!.setGravity(Gravity.TOP)
            dialog.window!!.setDimAmount(0.5f)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setOnCancelListener(this)
            val params = dialog.window!!.attributes
            params.width = WindowManager.LayoutParams.WRAP_CONTENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.y = (resources.getDimension(com.voyah.voice.framework.R.dimen.dp_1) * 69).toInt()
            dialog.window!!.attributes = params
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        binding = FragmentHistoryBinding.bind(view)
        binding.recycleHis.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        initView()
        return view
    }

    override fun onDetach() {
        Log.i(TAG, "onDetach")
        super.onDetach()
    }

    override fun dismiss() {
        Log.i(TAG, "dismiss")
        super.dismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        Log.i(TAG, "onCancel")
        editState = false
        historyAdapter.clear()
    }

    override fun onDismiss(dialog: DialogInterface) {
        Log.i(TAG, "onDismiss")
        super.onDismiss(dialog)
    }

    private fun initView() {
        Log.i(TAG, "initView")
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelableArrayList("data", DrawingInfo::class.java)
        } else {
            requireArguments().getParcelableArrayList("data")
        }
        historyAdapter = HistoryAdapter(data, editState)
        binding.recycleHis.adapter = historyAdapter
        if (editState) {
            binding.btnEdit.visibility = View.INVISIBLE
            binding.btnCancel.visibility = View.VISIBLE
            binding.btnDelete.visibility = View.VISIBLE
            binding.btnSelectAll.visibility = View.VISIBLE
        } else {
            binding.btnEdit.visibility = View.VISIBLE
            binding.btnCancel.visibility = View.INVISIBLE
            binding.btnDelete.visibility = View.INVISIBLE
            binding.btnSelectAll.visibility = View.INVISIBLE
        }
        binding.btnClose.setOnClickListener {
            historyAdapter.clear()
            editState = false
            dismiss()
        }
        binding.btnEdit.setOnClickListener {
            val isViewCmd = VoiceViewCmdUtils.isClickByViewCmd(binding.btnEdit)
            var clickType = Report.CLICK
            if (isViewCmd) {
                clickType = Report.VIEW_CMD
            }
            lifecycleScope.launch(Dispatchers.Default)
            {
                ServiceFactory.getInstance().voiceService.postBurialPointEvent(
                    EVENT_TYPE_EDIT_HISTORY,
                    clickType,
                    0,
                    null
                )
                withContext(Dispatchers.Main) {
                    historyAdapter.setEditState(true)
                    editState = true
                    binding.btnEdit.visibility = View.INVISIBLE
                    binding.btnCancel.visibility = View.VISIBLE
                    binding.btnDelete.visibility = View.VISIBLE
                    binding.btnSelectAll.visibility = View.VISIBLE
                    binding.txtTitle.visibility = View.INVISIBLE
                }
            }
        }
        binding.btnCancel.setOnClickListener {
            historyAdapter.setEditState(false)
            editState = false
            binding.btnEdit.visibility = View.VISIBLE
            binding.btnCancel.visibility = View.INVISIBLE
            binding.btnDelete.visibility = View.INVISIBLE
            binding.btnSelectAll.visibility = View.INVISIBLE
            binding.txtTitle.visibility = View.VISIBLE
        }
        binding.btnDelete.setOnClickListener {
            delete()
        }
        binding.btnSelectAll.setOnClickListener {
            historyAdapter.selectAll(
                true
            )
        }
        if (data.isNullOrEmpty()) {
            binding.imgEmpty.visibility = View.VISIBLE
            binding.btnEdit.isEnabled = false
        } else {
            binding.imgEmpty.visibility = View.GONE
            binding.btnEdit.isEnabled = true
        }
    }

    private fun isAllSelected(arr: BooleanArray, size: Int): Boolean {
        for (i in 0 until size.coerceAtMost(arr.size)) {
            if (!arr[i]) {
                return false
            }
        }
        return true
    }

    private fun deleteConfirm() {
        val drawingInfoList = historyAdapter.drawingInfoList
        val deleteList: MutableList<DrawingInfo> = ArrayList()
        val updateList: MutableList<DrawingInfo> = ArrayList()
        for (i in drawingInfoList.indices) {
            val selectArr = historyAdapter.selectState[i]
            val size =
                if (drawingInfoList[i].urlList != null) drawingInfoList[i].urlList.size else 0
            if (isAllSelected(selectArr, size)) {
                deleteList.add(drawingInfoList[i])
            } else {
                var update = false
                val urlList: MutableList<String> = ArrayList()
                for (j in 0 until selectArr.size.coerceAtMost(size)) {
                    if (selectArr[j]) {
                        update = true
                    } else {
                        urlList.add(drawingInfoList[i].urlList[j])
                    }
                }
                if (update) {
                    drawingInfoList[i].urlList = urlList
                    updateList.add(drawingInfoList[i])
                }
            }
        }
        if (updateList.size > 0 || deleteList.size > 0) {
            lifecycleScope.launch(Dispatchers.Default) {
                if (updateList.size > 0) {
                    DrawingInfoDatabase.getInstance(context)
                        .drawingInfoDao().update(updateList)
                }
                if (deleteList.size > 0) {
                    DrawingInfoDatabase.getInstance(context)
                        .drawingInfoDao().delete(deleteList)
                }
                val data = DrawingInfoDatabase.getInstance(
                    context
                ).drawingInfoDao().all
                val arrayList =
                    ArrayList(data)
                requireArguments().putParcelableArrayList("data", arrayList)
                editState = false
                withContext(Dispatchers.Main) {
                    initView()

                }
            }
        }
    }

    override fun onResume() {
        Log.i(TAG, "onResume:${editState}")
        super.onResume()
    }

    private fun delete() {
        val selected = historyAdapter.selectState
        var count = 0
        for (i in 0 until historyAdapter.drawingInfoList.size) {
            for (j in 0 until historyAdapter.drawingInfoList.get(i).urlList.size) {
                if (selected[i][j]) {
                    count++
                }
            }
        }
        if (count > 0) {
            showConfirmationDialog(count, requireContext(), {
                deleteConfirm()

            }, {

            })
        } else {
            VcosToastManager.instance?.showSystemToast(requireContext(), "请先选中要删除的图片")
        }
    }


    @SuppressLint("SetTextI18n")
    private fun showConfirmationDialog(
        count: Int,
        context: Context,
        onConfirm: () -> Unit,
        onCancel: () -> Unit
    ) {
        Log.i(TAG, "showConfirmationDialog")
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null)
        val dialogBinding = DialogConfirmBinding.bind(dialogView)
        dialogBinding.textCount.text = "共计删除${count}张图片，删除后不可恢复"
        alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()
        alertDialog?.show()
        if (alertDialog?.window != null) {
            val layoutParams: WindowManager.LayoutParams = alertDialog?.window!!.attributes
            layoutParams.width = (context.resources.getDimension(R.dimen.dp_1) * 600).toInt()
            layoutParams.height = (context.resources.getDimension(R.dimen.dp_1) * 360).toInt()
            alertDialog?.window!!.attributes = layoutParams
            alertDialog?.window!!.setDimAmount(0.5f)
            alertDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialogBinding.btnConfirm.setOnClickListener {
            onConfirm()
            alertDialog?.dismiss()
        }
        dialogBinding.btnCancel.setOnClickListener {
            onCancel()
            alertDialog?.dismiss()
        }
        dialogView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            @VoiceRegisterView
            override fun onViewAttachedToWindow(v: View) {
                Log.i(TAG, "onViewAttachedToWindow:$v")
            }

            override fun onViewDetachedFromWindow(v: View) {
                Log.i(TAG, "onViewDetachedFromWindow:$v")
                v.removeOnAttachStateChangeListener(this)
            }
        })
    }

    fun dismissAllDialog() {
        dismiss()
        alertDialog?.let {
            LogUtils.i("alert dialog is showing?${it.isShowing}")
            it.dismiss()
        } ?: run {
            LogUtils.i("dismissAllDialog alertDialog is null...")
        }
        alertDialog = null
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val currentMode: Int = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        Log.i(TAG, "onConfigurationChanged${currentMode}")
        if (currentMode == lastUiMode) {
            return
        }
        lastUiMode = currentMode
        var mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        when (currentMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                mode = AppCompatDelegate.MODE_NIGHT_NO
            }

            Configuration.UI_MODE_NIGHT_YES -> {
                mode = AppCompatDelegate.MODE_NIGHT_YES
            }
        }
        Log.i(TAG, "setDefaultNightMode${mode}")
        AppCompatDelegate.setDefaultNightMode(mode)
    }

}
