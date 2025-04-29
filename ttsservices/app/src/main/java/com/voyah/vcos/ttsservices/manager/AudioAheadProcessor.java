package com.voyah.vcos.ttsservices.manager;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;

import com.voyah.vcos.ttsservices.mem.ByteArr;
import com.voyah.vcos.ttsservices.mem.ByteArrMgr;
import com.voyah.vcos.ttsservices.mem.TtsByteArrMgr;
import com.voyah.vcos.ttsservices.utils.LogUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author:lcy
 * @data:2025/3/11
 **/
public class AudioAheadProcessor {

    private String TAG = AudioAheadProcessor.class.getSimpleName();

    private final BlockingQueue<byte[]> audioBuffer = new LinkedBlockingQueue<>();
    private ByteArrMgr.ByteArrObj completedByteArray = new ByteArrMgr.ByteArrObj(0);
    private final int delayMillis = 200;
    private final Handler mHandler;
    private HandlerThread mHandlerThread;
    private ITtsResolverCallBack ttsResolverCallBack;
    private boolean isInitialDelay = true; // 是否处于初始延迟阶段
    private boolean isAudioComplete = false; // 音频合成是否完成

    private boolean isTaskStop = false; //任务是否终止

    private String currentTtsId; //当前执行任务id
    private long audioLen;

    public AudioAheadProcessor(String num) {
        TAG = TAG + "-" + num;
//        Looper.prepare();
//        mLooper = Looper.myLooper();
//        mHandler = new Handler(mLooper);
//        Looper.loop();
        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    //任务开始
    public void startTask(String ttsId) {
        LogUtils.d(TAG, "startTask:" + ttsId);
        currentTtsId = ttsId;
        isTaskStop = false;
        isInitialDelay = true;
    }

    // 处理在线合成音频数据
    public void processAudioDataDelay(byte[] audioData, String ttsId, String resultId, int usage) {
//        LogUtils.d(TAG, "processAudioDataDelay isInitialDelay:" + isInitialDelay);
        if (isInitialDelay) {
            // 将初始数据放入缓存
            audioBuffer.offer(audioData);

            // 如果还没有安排定时任务，则安排一个任务在300ms后处理缓存中的数据
            if (!mHandler.hasMessages(0)) {
                LogUtils.d(TAG, "processAudioData");
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        flushBufferToPlayer(ttsId, resultId, usage);
                    }
                }, delayMillis);
            }
        } else {
            mHandler.post(() -> disposeBytes(audioData, ttsId, resultId, usage));
        }
    }

    public void processAudioData(byte[] audioData, String ttsId, String resultId, int usage) {
        disposeBytes(audioData, ttsId, resultId, usage);
    }

    // 标记音频合成完成，并立即刷新缓存
    public void markAudioComplete(String ttsId, String resultId, int usage) {
        LogUtils.d(TAG, "markAudioComplete ttsId:" + ttsId + " ,resultId:" + resultId + " ,usage:" + usage);
        isAudioComplete = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                flushBufferToPlayer(ttsId, resultId, usage);
            }
        });
    }


    // 停止任务，清空缓存并重置状态
    public void stopTask() {
        LogUtils.d(TAG, "stopTask");
        //移除所有未处理的任务
        cleanTask();
        // 重置状态
        isInitialDelay = true;
        isAudioComplete = false;
        isTaskStop = true;
        // 清空缓存
        audioBuffer.clear();
    }

    //播报结束
    public void playEnd() {
        LogUtils.d(TAG, "playEnd");
        cleanTask();
    }

    public void setTtsResolverCallBack(ITtsResolverCallBack ttsResolverCallBack) {
        this.ttsResolverCallBack = ttsResolverCallBack;
    }


    // 刷新缓存中的数据到播放器
    private void flushBufferToPlayer(String ttsId, String resultId, int usage) {
        // 如果已经不在初始延迟阶段，直接返回
        if (!isInitialDelay) {
            return;
        } else {
            audioLen = 0;
            LogUtils.d(TAG, "audioBuffer.size:" + audioBuffer.size());
        }
        isInitialDelay = false; // 结束初始延迟阶段

        // 将缓存中的数据写入播放器
        while (!audioBuffer.isEmpty()) {
            byte[] data = audioBuffer.poll();
            if (data != null) {
                audioLen += data.length;
                disposeBytes(data, ttsId, resultId, usage);
            }
        }
        LogUtils.d(TAG, "audioLen:" + audioLen);
    }

    /**
     * 处理字节数据，将其拆分为指定大小的块，并通过 onData 方法下发。
     *
     * @param bytes    输入的字节数组
     * @param ttsId    当前任务的唯一标识
     * @param resultId 结果标识
     * @param usage    用于获取 ByteArrObj 的用途标识
     */
    private void disposeBytes(byte[] bytes, String ttsId, String resultId, int usage) {
        // 1. 输入校验
        if (bytes == null) {
            LogUtils.e(TAG, "Input bytes array is null.");
            return;
        }
        if (bytes.length == 0) {
            LogUtils.d(TAG, " last buffer");
            onData(ttsId, new ByteArrMgr.ByteArrObj(0), 0, resultId); // 下发空数据
            return;
        }
        // 2. 初始化变量
        final int totalLength = bytes.length;
        final int chunkSize = ByteArr.LEN_PER;
        final int chunkCount = (totalLength + chunkSize - 1) / chunkSize; // 向上取整
//        LogUtils.d(TAG, "Processing bytes. Total length: " + totalLength + ", Chunk size: " + chunkSize + ", Chunk count: " + chunkCount);
        // 3. 遍历所有块
        for (int i = 0; i < chunkCount; i++) {
            // 3.1 检查任务状态
            if (isTaskStop) {
                LogUtils.d(TAG, "Task stopped. Aborting processing.");
                return;
            }
            if (!TextUtils.equals(currentTtsId, ttsId)) {
                LogUtils.d(TAG, "ttsId mismatch. Current ttsId: " + currentTtsId + ", Expected ttsId: " + ttsId);
                return;
            }
            // 3.2 获取复用的 ByteArrObj
            ByteArrMgr.ByteArrObj bufferObj = TtsByteArrMgr.getInstance().getByteObj(usage);
            if (bufferObj == null || bufferObj.arr == null) {
                LogUtils.e(TAG, "Failed to get ByteArrObj or buffer is null.");
                return;
            }
            // 3.3 计算当前块的起始位置和长度
            int startPos = i * chunkSize;
            int remainingLength = totalLength - startPos;
            int currentChunkLength = Math.min(remainingLength, chunkSize);
            // 3.4 重置 buffer 并拷贝数据
            if (bufferObj.len > 0)
                bufferObj.reset(); // 重置 buffer
            System.arraycopy(bytes, startPos, bufferObj.arr, 0, currentChunkLength);
            bufferObj.len = currentChunkLength;
            // 3.5 下发数据
            onData(ttsId, bufferObj, 0, resultId);
//            LogUtils.d(TAG, "Chunk " + (i + 1) + " of " + chunkCount + " processed. Length: " + currentChunkLength);
        }
//        LogUtils.d(TAG, "All chunks processed successfully.");
    }

//    private void disposeBytes(byte[] bytes, String ttsId, String resultId, int usage) {
////        LogUtils.e(TAG, "disposeBytes bytes.length:" + bytes.length + " ,ttsId:" + ttsId + " ,mTtsId:" + mTtsId);
//        if (isTaskStop || !TextUtils.equals(currentTtsId, ttsId)) {
//            LogUtils.d(TAG, "disposeBytes isTaskStop:" + isTaskStop + " ,currentTtsId:" + currentTtsId + " ,ttsId:" + ttsId);
//            return;
//        }
//        if (bytes.length == 0) {
//            LogUtils.d(TAG, " last buffer");
//            onData(ttsId, completedByteArray, 0, resultId);
//        } else {
//            int count = bytes.length / ByteArr.LEN_PER + (bytes.length % ByteArr.LEN_PER == 0 ? 0 : 1);
//            if (count >= 60)
//                LogUtils.d(TAG, "disposeBytes count:" + count);
//            for (int i = 0; i < count; i++) {
//                ByteArrMgr.ByteArrObj obj = TtsByteArrMgr.getInstance().getByteObj(usage);
//                if (obj.len > 0) {
//                    obj.reset();
//                }
//                int len = bytes.length - i * ByteArr.LEN_PER;
//                if (len > ByteArr.LEN_PER) {
//                    len = ByteArr.LEN_PER;
//                }
//                System.arraycopy(bytes, i * ByteArr.LEN_PER, obj.arr, 0, len);
//                obj.len = len;
//                if (!isTaskStop && TextUtils.equals(currentTtsId, ttsId))
//                    onData(ttsId, obj, 0, resultId);
//                else
//                    LogUtils.d(TAG, "disposeBytes stopped:" + isTaskStop + " ,currentTtsId:" + currentTtsId + " ,ttsId:" + ttsId
//                            + ", count:" + count + " ,i:" + i);
//            }
//        }
//    }

    private void cleanTask() {
        // 当停止任务或播放完成后清除下队列
        mHandler.removeCallbacksAndMessages(null);
    }

    private void onData(String ttsId, ByteArrMgr.ByteArrObj arrObject, int ttsType, String resultId) {
        if (ttsResolverCallBack != null)
            ttsResolverCallBack.onData(ttsId, arrObject, ttsType);
    }

}
