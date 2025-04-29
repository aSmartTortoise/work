package com.voyah.voice.main.manager

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.voyah.common.constant.PATH_GALLERY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.Exception


/**
 *  author : jie wang
 *  date : 2024/8/21 15:57
 *  description :
 */
class FileDownloadManager {

    @SuppressLint("UseCompatLoadingForDrawables")
    fun downloadOriginalFile(
        activity: AppCompatActivity,
        url: String,
        fileName: String,
        onSuccess: (destFile: File) -> Unit,
        onFail: (exception: Exception) -> Unit
    ) {
        // 使用Glide下载原始图片
        if (url.toIntOrNull() == null) {
            Glide.with(activity)
                .asFile()
                .load(url)
                .into(object : CustomTarget<File?>() {

                    @SuppressLint("SetWorldWritable", "SetWorldReadable")
                    override fun onResourceReady(
                        resource: File,
                        transition: Transition<in File?>?
                    ) {
                        LogUtils.d("onResourceReady")
                        activity.lifecycleScope.launch(Dispatchers.IO) {
                            LogUtils.d("downloadOriginalFile url:$url")
                            val destFile = File(PATH_GALLERY, fileName)
                            val sourceChannel = FileInputStream(resource).channel
                            val destChannel = FileOutputStream(destFile).channel
                            try {
                                destChannel.transferFrom(sourceChannel, 0, sourceChannel.size())
                                LogUtils.d("downloadOriginalFile, download file success.")
                                onSuccess.invoke(destFile)
                            } catch (e: IOException) {
                                LogUtils.w("downloadOriginalFile, download file error:$e")
                                onFail.invoke(e)
                            } finally {
                                sourceChannel.close()
                                destChannel.close()
                            }
                        }
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        onFail.invoke(RuntimeException("onLoadFailed, url:$url"))
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        LogUtils.d("downloadOriginalFile, onLoadCleared")
                        onFail.invoke(RuntimeException("onLoadCleared, url:$url"))
                    }
                })
        } else {
            activity.lifecycleScope.launch(Dispatchers.IO) {
                val drawable = activity.resources.getDrawable(url.toIntOrNull()!!)
                val bitmap = drawableToBitmap(drawable)
                val destFile = File(PATH_GALLERY, fileName)
                val fileOutputStream = FileOutputStream(destFile)
                try {
                    bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                    onSuccess.invoke(destFile)
                } catch (e: Exception) {
                    e.printStackTrace()
                    onFail.invoke(e)
                } finally {
                    fileOutputStream.close()
                }
            }
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}