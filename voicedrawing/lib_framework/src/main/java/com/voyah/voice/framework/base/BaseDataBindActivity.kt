package com.voyah.voice.framework.base

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.voyah.voice.framework.ext.saveAs
import com.voyah.voice.framework.ext.saveAsUnChecked
import java.lang.reflect.ParameterizedType

/**
 * @author jackie wong
 * @date   2023/2/26 11:48
 * @desc   dataBinding Activity基类
 */
abstract class BaseDataBindActivity<DB : ViewBinding> : BaseActivity() {
    lateinit var dataBinding: DB

    override fun setContentLayout() {
//      mBinding = DataBindingUtil.setContentView(this, getLayoutResId())
        val type = javaClass.genericSuperclass
        val vbClass: Class<DB> = type!!.saveAs<ParameterizedType>().actualTypeArguments[0].saveAs()
        val method = vbClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        dataBinding = method.invoke(this, layoutInflater)!!.saveAsUnChecked()
        setContentView(dataBinding.root)
    }

    override fun getLayoutResId(): Int = 0
}