package com.voyah.voice.main.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.LogUtils
import com.voyah.voice.main.databinding.DialogLoadingBinding
import com.voyah.voice.main.ui.BaseDialogFragment

/**
 *  author : jie wang
 *  date : 2025/1/11 15:02
 *  description :
 */
class LoadingDialogFragment: BaseDialogFragment() {

    private lateinit var binding: DialogLoadingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogLoadingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtils.d("onViewCreated")
    }
}