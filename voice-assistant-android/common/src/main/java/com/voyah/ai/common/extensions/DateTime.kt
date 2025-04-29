package com.voyah.ai.common.extensions

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit


/**
 * @Date 2024/9/2 11:20
 * @Author 8327821
 * @Email *
 * @Description .
 **/

fun Long.toDateTime(): String {
    val df: DateFormat = SimpleDateFormat.getTimeInstance()
    // 将毫秒转换为秒，因为SimpleDateFormat要求的是Date类型
    val date = Date(TimeUnit.MILLISECONDS.toSeconds(this))
    // 格式化时间
    return df.format(date)
}