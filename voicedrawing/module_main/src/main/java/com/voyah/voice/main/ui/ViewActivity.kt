package com.voyah.voice.main.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.LogUtils
import com.lib.common.voyah.ServiceFactory
import com.mega.nexus.os.MegaUserHandle
import com.vcos.common.widgets.vcostoast.VcosToastManager
import com.voyah.cockpit.common.gallery.GalleryManager
import com.voyah.vcos.EVENT_TYPE_SAVE_PHOTO
import com.voyah.vcos.manager.MegaDisplayHelper
import com.voyah.viewcmd.VoiceViewCmdUtils
import com.voyah.voice.framework.base.BaseMvvmActivity
import com.voyah.voice.framework.report.Report
import com.voyah.voice.framework.report.ReportHelp
import com.voyah.voice.framework.report.TrackOther
import com.voyah.voice.framework.util.DateUtil
import com.voyah.voice.main.R
import com.voyah.voice.main.adapter.BigPhoneAdapter
import com.voyah.voice.main.databinding.ActivityViewBinding
import com.voyah.voice.main.interfce.IShowDrawingOkWindow
import com.voyah.voice.main.manager.FileDownloadManager
import com.voyah.voice.main.viewmodel.ViewActivityModel
import kotlinx.coroutines.launch
import java.io.File


class ViewActivity : BaseMvvmActivity<ActivityViewBinding, ViewActivityModel>(),
    ViewPager.OnPageChangeListener, IShowDrawingOkWindow {
    companion object {

        const val URL_LIST: String = "url_list"

        const val POSITION: String = "position"

        private const val IS_URL: String = "is_url"

        const val FILE_PREFIX = "AIP_"
        const val FILE_SUFFIX = ".png"
        const val PERMISSION_REQUEST_CODE = 100

        fun start(context: Context, url: ArrayList<String>, position: Int, isUrl: Boolean) {
            val intent = Intent(context, ViewActivity::class.java)
            intent.putStringArrayListExtra(URL_LIST, url)
            intent.putExtra(POSITION, position)
            intent.putExtra(IS_URL, isUrl)
            context.startActivity(intent)
        }

    }

    private var currentPosition: Int = 0

    override fun initView(savedInstanceState: Bundle?) {
        LogUtils.d("initView")
        VoiceViewCmdUtils.setDialogActivity(this)
        val btnBack = findViewById<View>(R.id.btn_back)
        btnBack.setOnClickListener {
            LogUtils.d("back button click...")
            finish()
        }

        showViewPaper(intent)
        ClickUtils.applySingleDebouncing(dataBinding.btnSave, object : View.OnClickListener {

            override fun onClick(v: View?) {
                val isViewCmd = VoiceViewCmdUtils.isClickByViewCmd(dataBinding.btnSave)
                var type = Report.CLICK
                if (isViewCmd) {
                    type = Report.VIEW_CMD
                }
                Log.i(TAG, "btnSave isViewCmd:$isViewCmd")
                ServiceFactory.getInstance().voiceService.postBurialPointEvent(
                    EVENT_TYPE_SAVE_PHOTO,
                    type,
                    0,
                    ""
                )

                val adapter = dataBinding.viewPager.adapter
                if (adapter is BigPhoneAdapter) {
                    if (!adapter.data.isNullOrEmpty()) {
                        val storageFullFlag = GalleryManager.getInstance().isGalleryStorageFull
                        LogUtils.i("btnSave click, storageFullFlag:$storageFullFlag")
                        if (storageFullFlag) {
                            VcosToastManager.Companion.instance?.showToast(
                                this@ViewActivity,
                                this@ViewActivity.getString(R.string.tip_gallery_space_full)
                            )
                        } else {
                            if (ContextCompat.checkSelfPermission(
                                    this@ViewActivity,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                ActivityCompat.requestPermissions(
                                    this@ViewActivity,
                                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    PERMISSION_REQUEST_CODE
                                )
                            } else {
                                val url = adapter.data[dataBinding.viewPager.currentItem]
                                val dateForGallery =
                                    DateUtil.getDateForGallery(System.currentTimeMillis())
                                val fileName = "$FILE_PREFIX$dateForGallery$FILE_SUFFIX"
                                FileDownloadManager().downloadOriginalFile(
                                    this@ViewActivity,
                                    url,
                                    fileName,
                                    onSuccess = { destFile ->
                                        notifyGalleryPicSaved(destFile, this@ViewActivity)
                                        lifecycleScope.launch {
                                            VcosToastManager.Companion.instance?.showToast(
                                                this@ViewActivity,
                                                this@ViewActivity.getString(R.string.save_success)
                                            )
                                        }
                                    },
                                    onFail = { exception ->
                                        LogUtils.w("downloadOriginalFile exception:$exception")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("SetWorldWritable", "SetWorldReadable")
    private fun notifyGalleryPicSaved(destFile: File, activity: AppCompatActivity) {
        LogUtils.i("notifyGalleryPicSaved destFile path:${destFile.path} isExist:${destFile.exists()}")
        destFile.let {
            it.setWritable(true, false)
            it.setReadable(true, false)
        }

        val intent = Intent("com.voyah.cockpit.gallery.refresh").apply {
            component = ComponentName(
                "com.voyah.cockpit.gallery",
                "com.voyah.cockpit.gallery.receiver.RefreshReceiver"
            )
            putExtra("refreshType", "ForceRefreshLocal")
        }

        ServiceFactory.getInstance().voiceService.sendBroadcastToGallery(activity, intent)
    }

    private fun showViewPaper(intent: Intent?) {
        val url = intent?.getStringArrayListExtra(URL_LIST)
        val position = intent?.getIntExtra(POSITION, 0)
        currentPosition = position!!
        dataBinding.viewPager.adapter =
            BigPhoneAdapter(this, url, layoutInflater, !intent.getBooleanExtra(IS_URL, true))
        (dataBinding.viewPager.adapter as BigPhoneAdapter).setIsFullScreen(!isInMultiWindowMode)
        (dataBinding.viewPager.adapter as BigPhoneAdapter).setIsFullScreen(!isInMultiWindowMode)
        val params = dataBinding.btnSave.layoutParams as ViewGroup.MarginLayoutParams
        if (!isInMultiWindowMode) {
            params.bottomMargin = resources.getDimension(R.dimen.dp_108).toInt()
        } else {
            params.bottomMargin = resources.getDimension(R.dimen.dp_226).toInt()
        }
        dataBinding.btnSave.layoutParams = params
        dataBinding.viewPager.currentItem = currentPosition
        dataBinding.viewPager.offscreenPageLimit = 1
        dataBinding.viewPager.addOnPageChangeListener(this)
        dataBinding.imgLeft.setOnClickListener {
            if (currentPosition > 0) {
                currentPosition--
                dataBinding.viewPager.setCurrentItem(currentPosition, true)
            }
        }
        dataBinding.imgRight.setOnClickListener {

            if (currentPosition < (dataBinding.viewPager.adapter as BigPhoneAdapter).count - 1) {
                currentPosition++
                dataBinding.viewPager.setCurrentItem(currentPosition, true)
            }
        }
        showPage()
    }

    private fun setSaveBtnVisibilityByUrl(currentUrl: String?) {
        val md5 = EncryptUtils.encryptMD5ToString(currentUrl)
        val fileName = "$FILE_PREFIX$md5$FILE_SUFFIX"
        val destFile = File("/camera/media", fileName)
        val exists = destFile.exists()
        LogUtils.d("setSaveBtnVisibilityByUrl exists:$exists")
        val visibility = if (!exists) {
            View.VISIBLE
        } else View.GONE
        dataBinding.btnSave.visibility = visibility
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        showViewPaper(intent)

    }


    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    private fun showPage() {
        dataBinding.imgLeft.isEnabled = currentPosition > 0
        dataBinding.imgRight.isEnabled =
            currentPosition < (dataBinding.viewPager.adapter as BigPhoneAdapter).count - 1
    }

    override fun onPageSelected(position: Int) {
        currentPosition = position
        showPage()
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    @Deprecated("Deprecated in Java")
    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        super.onMultiWindowModeChanged(isInMultiWindowMode)
        LogUtils.i(TAG, "onMultiWindowModeChanged")
        if (dataBinding.viewPager.adapter != null) {
            (dataBinding.viewPager.adapter as BigPhoneAdapter).setIsFullScreen(!isInMultiWindowMode)
            val params = dataBinding.btnSave.layoutParams as ViewGroup.MarginLayoutParams
            if (!isInMultiWindowMode) {
                params.bottomMargin = resources.getDimension(R.dimen.dp_108).toInt()
            } else {
                params.bottomMargin = resources.getDimension(R.dimen.dp_226).toInt()
            }
            dataBinding.btnSave.layoutParams = params
        }
    }


}