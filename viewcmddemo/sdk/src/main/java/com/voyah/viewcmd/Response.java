package com.voyah.viewcmd;

import android.content.res.Resources;
import android.view.View;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Random;

/**
 * 执行结果
 */
public class Response {

    @IntDef({ErrCode.EC_NORMAL, ErrCode.EC_MULTI_SELECT, ErrCode.EC_UNSAFE_NONE, ErrCode.EC_UNSAFE_ONLY_DRIVER, ErrCode.EC_UNSAFE_ONLY_FRONT,
            ErrCode.EC_OPENED, ErrCode.EC_CLOSED, ErrCode.EC_UNKNOWN, ErrCode.EC_GESTURE_DOWN_MAX, ErrCode.EC_GESTURE_UP_MAX,
            ErrCode.EC_GESTURE_LEFT_MAX, ErrCode.EC_GESTURE_RIGHT_MAX, ErrCode.EC_PAGE_UP_MAX, ErrCode.EC_PAGE_DOWN_MAX,
            ErrCode.EC_VIEW_DISABLED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ErrCode {
        int EC_NORMAL = 0; //正常响应
        int EC_MULTI_SELECT = 1; //找到多个结果，无法选择
        int EC_UNSAFE_NONE = 2; //非安全类, 禁止操作
        int EC_UNSAFE_ONLY_DRIVER = 3; //非安全类，仅主驾
        int EC_UNSAFE_ONLY_FRONT = 4; //非安全类，仅前排
        int EC_OPENED = 5; //当前已经是打开状态
        int EC_CLOSED = 6; //当前已经是关闭状态
        int EC_GESTURE_DOWN_MAX = 7; //当前已经下滑到底
        int EC_GESTURE_UP_MAX = 8; //当前已经上滑到顶
        int EC_GESTURE_LEFT_MAX = 9; //当前已经左滑到顶
        int EC_GESTURE_RIGHT_MAX = 10; //当前已经右滑到顶
        int EC_PAGE_UP_MAX = 11; //当前已经是第一页
        int EC_PAGE_DOWN_MAX = 12; //当前已经是最后一页
        int EC_VIEW_DISABLED = 13; //不可点击状态
        int EC_UNKNOWN = -1; //未知异常
    }

    public View view; //执行view
    public int errCode; //错误码
    public String text; //回复语

    /**
     * 创建反馈类
     *
     * @param view    执行view
     * @param errCode 执行结果
     * @param text    播报语
     */
    private Response(View view, int errCode, String text) {
        this.view = view;
        this.errCode = errCode;
        this.text = text;
    }


    /**
     * 创建反馈类
     *
     * @param view    执行view
     * @param errCode 执行结果
     */
    public static Response response(View view, int errCode) {
        String text = getReply(errCode);
        return new Response(view, errCode, text);
    }

    /**
     * 创建反馈类
     *
     * @param view 执行view
     * @param text 播报语
     */
    public static Response response(View view, String text) {
        return new Response(view, ErrCode.EC_NORMAL, text);
    }

    /**
     * 创建反馈类
     *
     * @param text 播报语
     */
    public static Response response(String text) {
        return new Response(null, ErrCode.EC_NORMAL, text);
    }

    /**
     * 获取回复语
     *
     * @return
     */
    public static String getReply(@ErrCode int errCode) {
        Resources resources = VoiceViewCmdUtils.mCtx.getResources();
        switch (errCode) {
            case ErrCode.EC_NORMAL:
                return getRandomReply(resources.getStringArray(R.array.execute_ok));
            case ErrCode.EC_MULTI_SELECT:
                return getRandomReply(resources.getStringArray(R.array.execute_multi_select));
            case ErrCode.EC_UNSAFE_NONE:
                return getRandomReply(resources.getStringArray(R.array.execute_unsafe_none));
            case ErrCode.EC_UNSAFE_ONLY_DRIVER:
                return getRandomReply(resources.getStringArray(R.array.execute_unsafe_only_driver));
            case ErrCode.EC_UNSAFE_ONLY_FRONT:
                return getRandomReply(resources.getStringArray(R.array.execute_unsafe_only_front));
            case ErrCode.EC_OPENED:
                return getRandomReply(resources.getStringArray(R.array.execute_opened_state));
            case ErrCode.EC_CLOSED:
                return getRandomReply(resources.getStringArray(R.array.execute_closed_state));
            case ErrCode.EC_GESTURE_UP_MAX:
                return getRandomReply(resources.getStringArray(R.array.execute_gesture_up_max));
            case ErrCode.EC_GESTURE_DOWN_MAX:
                return getRandomReply(resources.getStringArray(R.array.execute_gesture_down_max));
            case ErrCode.EC_GESTURE_LEFT_MAX:
                return getRandomReply(resources.getStringArray(R.array.execute_gesture_left_max));
            case ErrCode.EC_GESTURE_RIGHT_MAX:
                return getRandomReply(resources.getStringArray(R.array.execute_gesture_right_max));
            case ErrCode.EC_PAGE_UP_MAX:
                return getRandomReply(resources.getStringArray(R.array.execute_page_up_max));
            case ErrCode.EC_PAGE_DOWN_MAX:
                return getRandomReply(resources.getStringArray(R.array.execute_page_down_max));
            case ErrCode.EC_VIEW_DISABLED:
                return getRandomReply(resources.getStringArray(R.array.execute_view_disabled));
            case ErrCode.EC_UNKNOWN:
                return "";
        }
        return null;
    }

    /**
     * 获取随机回复语
     *
     * @param replies
     * @return
     */
    private static String getRandomReply(String... replies) {
        if (replies.length == 0) {
            return "";
        } else {
            int i = new Random().nextInt(replies.length);
            return replies[i];
        }
    }

    @Override
    public String toString() {
        return "Response{" +
                "errCode=" + errCode +
                ", text='" + text + '\'' +
                '}';
    }
}
