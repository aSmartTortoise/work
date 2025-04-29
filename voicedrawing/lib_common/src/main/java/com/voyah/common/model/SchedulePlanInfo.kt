package com.voyah.common.model

data class SchedulePlanInfo (
    val intent: Int,
    val needRecord: Int,
    val needTTS: Int,
    val resultText: ResultText?,
    val scheduleList: List<ScheduleInfo>?
)

//{
//    "deviceId": "2d1f3664a4d253641a103129556bc9a18",
//    "aid": "8",
//    "status": 200,
//    "needTTS": 1,
//    "needRecord": 0,
//    "query": "查询今天的日程",
//    "intent": 1,
//    "scheduleList": [],
//    "resultText":
//    {
//        "ttsText": "今天没有日程安排。",
//        "weatherInfo": "",
//        "restaurantInfo": "",
//        "hotelInfo": "",
//        "pathInfo": ""
//    }
//}