package com.voyah.ai.common.helper

import android.content.Context
import com.voyah.ai.common.utils.AssetsUtils
import com.voyah.ai.common.utils.LogUtils
import com.voyah.ai.common.utils.SPUtil

/**
 * @Date 2024/11/9 13:44
 * @Author 8327821
 * @Email *
 * @Description 管理语音工程需要的资源文件
 **/
object ResMgr {

    private const val TAG = "ResMgr"

    private val graphPath = "/data/data/com.voyah.ai.voice/files/Configuration/flow_chart/";
    private val resPath = "/data/data/com.voyah.ai.voice/files/Configuration/resource/";

    //FIXME 文件IO，考虑放在子线程并处理同步问题
    fun tryUpdateRes(context: Context) {
        LogUtils.d(TAG, "tryUpdateRes")
        //extractCloudFLow(context)
        //extractOtherRes(context)

        //如果复制过，直接返回
        //未了兼容本地版本可以一致更新图拷贝，新增逻辑，只有更新后才不进行拷贝。
//        if (SPUtil.getBoolean(context, "assets_copy", false)) {
//            LogUtils.d(TAG, "assets_copy = true, return")
//            return
//        }
        //APK首次打开，复制资源文件到私有目录下
        AssetsUtils.getInstance().init(context)
        AssetsUtils.getInstance().deleteAllFilesInFolder(graphPath)
        AssetsUtils.getInstance().copyAssetsToSD("flow_chart", graphPath)
        AssetsUtils.getInstance().copyAssetsToSD("resource", resPath)
        SPUtil.putBoolean(context, "assets_copy", true)
    }

    /**
     * 车机启动的时候，加载云端下发的流程图资源
     *
     * @param context
     */
    /*private fun extractCloudFLow(context: Context) {
        //如果存在压缩包则进行删除和解压操作。
        LogUtils.d(TAG, "提取download流程图")
        val zipPath = getDownloadResPath(PathUtil.GRAPH_ZIP_PATH)
        val file = File(zipPath)
        if (file.exists()) {
            //1.删除原来的图文件
            LogUtils.d(TAG, "删除原来的图文件")
            FileUtils.deleteDir(PathUtil.GRAPH_PATH)
            //创建文件夹
            LogUtils.d(TAG, "重新创建文件夹")
            FileUtils.createFile(PathUtil.GRAPH_PATH)
            //2.解压之前的文件
            LogUtils.d(TAG, "解压图文件")
            try {
                FileUtils.unzip(zipPath, PathUtil.GRAPH_PATH)
            } catch (e: IOException) {
                LogUtils.e(TAG, "解压异常" + e.message)
            }
            //3.删除压缩包
            LogUtils.d(TAG, "删除压缩包")
            FileUtils.deleteFile(zipPath)
        }
    }*/


    /**
     * 车机启动的时候，加载云端下发的资源
     * @param context
     */
    /*private fun extractOtherRes(context: Context) {
        LogUtils.d(TAG, "提取其他资源文件")
        PathUtil.getResNames().forEach { resName ->
            val file = File(getDownloadResPath(resName)) //download目录下的文件
            if (file.exists()) {
                //说明上次启动下载了这个文件，尝试将其移动到/resource下
                LogUtils.d(TAG, "移动到/resource/$resName")
                file.renameTo(File(getResourceResPath(resName)))
            }
        }
    }*/

}