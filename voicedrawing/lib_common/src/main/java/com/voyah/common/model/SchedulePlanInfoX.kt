package com.voyah.common.model

data class SchedulePlanInfoX(
    val intent: Int,
    val markdownContent: String?,
    val scheduleList: List<Any>?,
    val ttsText: String?,
    var streamMode: Int = -1
)