package com.voyah.common.model


data class DrawingTimesInfo(
    val free: Boolean,
    val freeRemainTimes: Int, // 免费剩余次数
    val freeTimes: Int, // 免费总次数
    val remainTimes: Int, // 收费剩余次数
    val times: Int, //收费总次数
    val totalRemainTimes: Int, // 剩余总次数
    val totalTimes: Int,   // 总次数
    val vin: String
)