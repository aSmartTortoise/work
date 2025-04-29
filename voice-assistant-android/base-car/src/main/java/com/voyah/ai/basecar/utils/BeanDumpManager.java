package com.voyah.ai.basecar.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;

import androidx.annotation.NonNull;

import com.voice.sdk.device.ui.IThreadDelay;
import com.voyah.ai.common.dump.IBeanDump;
import com.voyah.ai.common.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public final class BeanDumpManager implements IThreadDelay {

    private static final String TAG = BeanDumpManager.class.getSimpleName();

    private BeanDumpManager() {

    }

    private static class InnerHolder {
        private static final BeanDumpManager instance = new BeanDumpManager();
    }

    public static BeanDumpManager getInstance() {
        return InnerHolder.instance;
    }

    private static final int MSG_ADD = 1; //
    private static final int MSG_REMOVE = 2;
    private static final int MSG_PRINT = 3;
    private static final long DELAY_PRINT = 1000 * 30;



    private List<IBeanDump> dumpList = new ArrayList<>();
    private Handler handler;
    private HandlerThread handlerThread;

    private void initHandler() {
        if (handler == null) {
            handlerThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_MORE_FAVORABLE);
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    switch (msg.what) {
                        case MSG_ADD:
                        case MSG_REMOVE:
                            if (msg.obj != null && msg.obj instanceof IBeanDump) {
                                IBeanDump dump = (IBeanDump) msg.obj;
                                if (msg.what == MSG_ADD) {
                                    boolean isEmpty = dumpList.isEmpty();
                                    if (!dumpList.contains(dump)) {
                                        dumpList.add(dump);
                                        if (isEmpty) {
                                            LogUtils.i(TAG, "start dump --");
                                            sendEmptyMessageDelayed(MSG_PRINT, DELAY_PRINT);
                                        }
                                    }
                                } else {
                                    dumpList.remove(dump);
                                    if (dumpList.isEmpty()) {
                                        LogUtils.i(TAG, "stop dump -- list is empty");
                                        removeMessages(MSG_PRINT);
                                    }
                                }
                            }
                            break;
                        case MSG_PRINT:
//                            StringBuilder stringBuilder = new StringBuilder();
                            int index;
                            for (IBeanDump dump : dumpList) {
                                String info = dump.getDumpInfo();
//                                index = info.indexOf("{");
//                                if (index > 0) {
//                                    info = info.substring(index);
//                                }
//                                stringBuilder.append(info);
                                LogUtils.d(TAG, info);
                            }
//                            LogUtils.d(TAG, stringBuilder.toString());
                            sendEmptyMessageDelayed(MSG_PRINT, DELAY_PRINT);
                            break;
                    }
                }
            };
        }
    }

    public void init() {
        initHandler();
    }

    public void addDump(IBeanDump dump) {
        if (dump != null && handler != null) {
            Message msg = handler.obtainMessage();
            msg.what = MSG_ADD;
            msg.obj = dump;
            handler.sendMessage(msg);
        }
    }

    public void removeDump(IBeanDump dump) {
        if (dump != null && handler != null) {
            Message msg = handler.obtainMessage();
            msg.what = MSG_REMOVE;
            msg.obj = dump;
            handler.sendMessage(msg);
        }
    }


    /**=====================================延时任务管理====================================**/

    /**
     *
     * @param run 超时回调
     * @param delay 超时时间
     */
    public void addDelayRunnable(Runnable run, long delay) {
        if (run != null && handler != null) {
            handler.removeCallbacksAndMessages(run);
            handler.postDelayed(run, run, delay);
        }
    }


    public void removeDelayRunnable(Runnable run) {
        if (run!= null && handler!= null) {
            handler.removeCallbacksAndMessages(run);
        }
    }
}
