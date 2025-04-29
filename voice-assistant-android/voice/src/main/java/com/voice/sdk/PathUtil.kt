package com.voice.sdk

import java.io.File

object PathUtil {
    //内置files目录
    const val BASE_FILE: String = Constant.path.dirWrite

    //配置文件目录
    @JvmField
    val CONF_FILE: String = BASE_FILE + "Configuration/"
    //流程图路径
    @JvmField
    val GRAPH_PATH: String = CONF_FILE + File.separator + "flow_chart/"

    //tts mapping等文件路径
    @JvmField
    val SOURCE_PATH: String = CONF_FILE + File.separator + "resource/"
    @JvmField
    val DOWNLOAD_PATH: String = CONF_FILE + File.separator + "download/"

    const val GRAPH_ZIP_PATH: String = "flow_chart.zip"
    const val FUNC_DEVICE: String = "function_devices.json"
    const val FUNC_GUIDE_TTS: String = "function_guidance_tts.json"
    const val FUNC_MAPPING: String = "function_mapping.json"
    const val FUNC_TO_ID: String = "function_to_id.json"
    const val OFFLINE_SCOPE: String = "offline_scope.json"
    const val OFFLINE_SCOPE_NO: String = "offline_scope_no.json"
    const val PARAMS: String = "params_en_ch.json"
    const val SCENE_CLASH: String = "scene_clash_mapping.json"
    const val SOURCE_EDITION: String = "source_edition.json" //版本控制的文件，不能单独以服务器版本为标准
    const val TTS_ID: String = "tts_id.json"

    /**
     * 通过资源文件名，获取其在/download的文件路径
     * @param resName
     * @return
     */
    @JvmStatic
    fun getDownloadResPath(resName: String): String {
        return DOWNLOAD_PATH + resName
    }

    /**
     * 通过资源文件名，获取其在/resource的文件路径
     * @param resName
     * @return
     */
    @JvmStatic
    fun getResourceResPath(resName: String): String {
        return SOURCE_PATH + resName
    }

    @JvmStatic
    fun getResNames() : Set<String> {
        return setOf(FUNC_DEVICE, FUNC_GUIDE_TTS, FUNC_MAPPING, FUNC_TO_ID, OFFLINE_SCOPE, OFFLINE_SCOPE_NO, PARAMS, SCENE_CLASH, TTS_ID)
    }

    @JvmStatic
    fun getGraphPath(): String {
        ensureDir(GRAPH_PATH)
        return GRAPH_PATH
    }

    @JvmStatic
    fun getConfigurationPath(): String {
        ensureDir(CONF_FILE)
        return CONF_FILE
    }

    /**
     * 确保某个文件夹存在，不存在则创建
     * @param path
     */
    private fun ensureDir(path: String) {
        val f = File(path)
        if (!f.exists()) {
            f.mkdirs()
        }
    }
}
