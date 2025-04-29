package com.voyah.vcos

import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.lib.common.voyah.ServiceFactory
import com.voyah.vcos.manager.MegaDisplayHelper
import com.voyah.viewcmd.VoiceViewCmdUtils
import com.voyah.voice.framework.report.Report
import com.voyah.voice.framework.report.ReportHelp
import com.voyah.voice.framework.report.TrackOther

/**
 *  author : jie wang
 *  date : 2024/9/14 15:02
 *  description :
 */

const val TAG = "BurialPointPost"
const val EVENT_TYPE_REDRAW = "redraw"
const val EVENT_TYPE_DRAWING_HISTORY = "drawing_history"
const val EVENT_TYPE_TO_GOODS_DETAIL = "goods_detail"
const val EVENT_TYPE_REMAIN_TIME = "remain_time"
const val EVENT_TYPE_SELECT_STYLE = "select_style"
const val EVENT_TYPE_SAVE_PHOTO = "save_photo"
const val EVENT_TYPE_OPEN_APP = "open_app"
const val EVENT_TYPE_ACTIVE_TIME = "active_time"
const val EVENT_TYPE_EDIT_HISTORY = "edit_history"
const val EVENT_TYPE_DRAWING_RESULT = "drawing_result"

fun postReport(view: View, eventType: String) {
    val isViewCmd = VoiceViewCmdUtils.isClickByViewCmd(view)
    LogUtils.d(TAG, "postReport isViewCmd:$isViewCmd, eventType:$eventType")
    var type = Report.CLICK
    if (isViewCmd) {
        type = Report.VIEW_CMD
    }
    when(eventType) {
        EVENT_TYPE_REDRAW -> {
            ServiceFactory.getInstance().voiceService.postBurialPointEvent(
                eventType,
                type,
                0,
                ""
            )
        }

        EVENT_TYPE_DRAWING_HISTORY -> {
            ServiceFactory.getInstance().voiceService.postBurialPointEvent(
                EVENT_TYPE_DRAWING_HISTORY,
                type,
                0,
                ""
            )
        }

        EVENT_TYPE_TO_GOODS_DETAIL -> {
            ServiceFactory.getInstance().voiceService.postBurialPointEvent(
                eventType,
                type,
                0,
                ""
            )
        }
    }
}