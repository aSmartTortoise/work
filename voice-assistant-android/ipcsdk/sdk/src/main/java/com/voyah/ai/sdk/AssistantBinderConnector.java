package com.voyah.ai.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.voyah.ai.sdk.bean.DhDirection;
import com.voyah.ai.sdk.bean.DhPickupMode;
import com.voyah.ai.sdk.bean.DhScreenType;
import com.voyah.ai.sdk.bean.DhSwitch;
import com.voyah.ai.sdk.bean.DhDialect;
import com.voyah.ai.sdk.bean.LifeState;
import com.voyah.ai.sdk.bean.NluResult;
import com.voyah.ai.sdk.bean.PvcResult;
import com.voyah.ai.sdk.bean.ShortCommand;
import com.voyah.ai.sdk.bean.UserVprInfo;
import com.voyah.ai.sdk.bean.VprResult;
import com.voyah.ai.sdk.listener.IHintResultListener;
import com.voyah.ai.sdk.listener.IImageRecognizeListener;
import com.voyah.ai.sdk.listener.IRmsDbListener;
import com.voyah.ai.sdk.listener.IVAResultListener;
import com.voyah.ai.sdk.listener.IVAStateListener;
import com.voyah.ai.sdk.listener.ILongASRListener;
import com.voyah.ai.sdk.listener.IPvcResultListener;
import com.voyah.ai.sdk.listener.IVprResultListener;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;


/**
 * binder远程服务辅助类
 */
public class AssistantBinderConnector extends AbsBinderConnector {
    public static final String TAG = "AssistantConnector";
    /**
     * 启动语音服务的action和包名
     */
    private static final String HOST_ASSISTANT_SERVICE_ACTION = "com.voyah.ai.RemoteBindService";
    private static final String HOST_ASSISTANT_SERVICE_PACKAGE = "com.voyah.ai.voice";
    private static final DateTimeFormatter NEWS_PUSH_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private ILongASRListener mLongASRListener; // 长识别结果回调
    private RemoteVprCallbackImpl remoteVprResultCallback;  //声纹识别回调
    private RemoteStateCallbackImpl remoteStateCallbackImpl;
    private RemoteResultCallbackImpl remoteResultCallbackImpl;
    private RemoteImageRecognizeCallbackImpl remoteImageRecognizeCallbackImpl;
    private ISpeechService mAssistantServiceInterface;

    public AssistantBinderConnector(Context context) {
        super(context);
    }

    @Override
    public void bindService() {
        Log.d(TAG, "bindService() called, mPackageName:" + mPackageName + ", id:" + id + ", SDK FLAVOR: 1.5");
        remoteReadyImpl = new RemoteSpeechReadyImpl();

        Intent intent = new Intent(HOST_ASSISTANT_SERVICE_ACTION);
        intent.setPackage(HOST_ASSISTANT_SERVICE_PACKAGE);
        boolean ret = context.bindService(intent, mAssistantBinderConnection, Context.BIND_AUTO_CREATE);
        Log.i(TAG, "bindService ret: " + ret);
        super.bindService();
    }

    @Override
    public void unBindService() {
        Log.d(TAG, "unBindService() called, isAlive:" + isAlive());
        if (isAlive()) {
            context.unbindService(mAssistantBinderConnection);
            mAssistantServiceInterface = null;
        }
        super.unBindService();
    }

    @Override
    public boolean isAlive() {
        return mAssistantServiceInterface != null && mAssistantServiceInterface.asBinder().isBinderAlive();
    }

    @Override
    public boolean isAppReInstalled(String pkgName) {
        return HOST_ASSISTANT_SERVICE_PACKAGE.equals(pkgName);
    }

    private final ServiceConnection mAssistantBinderConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAG, "onServiceDisconnected() called with: name = [" + name + "]");
            mAssistantServiceInterface = null;
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(() -> bindService(), 500);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected() called with: name = [" + name + "], service = [" + service + "]");
            try {
                // 注册 onSpeechReadyListener 到 service
                mAssistantServiceInterface = ISpeechService.Stub.asInterface(service);
                mAssistantServiceInterface.registerReadyCallback(mPackageName, remoteReadyImpl);
                mAssistantServiceInterface.asBinder().linkToDeath(mBinderPoolDeathRecipient, 0);

                if (isRemoteReady()) {
                    executeRemoteReady();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 判断远程服务是否准备就绪
     *
     * @return 是否准备就绪
     */
    public boolean isRemoteReady() {
        boolean isRemoteReady = false;
        if (isAlive()) {
            try {
                isRemoteReady = mAssistantServiceInterface.isRemoteReady();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return isRemoteReady;
    }

    /**
     * 获取语音状态
     */
    public String getLifeState() {
        String lifeState = LifeState.UNKNOWN;
        if (isAlive()) {
            try {
                lifeState = mAssistantServiceInterface.getLifeState();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return lifeState;
    }

    /**
     * 设置语音对话结果监听器
     *
     * @param listener 监听器
     */
    public void setVAResultListener(IVAResultListener listener) {
        try {
            if (isAlive()) {
                // 检测json parser库主动触发集成SDK的crash
                JsonUtil.getJSONParser();
                if (listener != null) {
                    if (remoteResultCallbackImpl != null) {
                        mAssistantServiceInterface.unregisterResultCallback(mPackageName, remoteResultCallbackImpl);
                    }
                    remoteResultCallbackImpl = new RemoteResultCallbackImpl(listener);
                    mAssistantServiceInterface.registerResultCallback(mPackageName, remoteResultCallbackImpl);
                } else if (remoteResultCallbackImpl != null) {
                    mAssistantServiceInterface.unregisterResultCallback(mPackageName, remoteResultCallbackImpl);
                    remoteResultCallbackImpl.listener = null;
                    remoteResultCallbackImpl = null;
                }
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置语音对话状态监听器
     *
     * @param listener 监听器
     */
    public void setVAStateListener(IVAStateListener listener) {
        try {
            if (isAlive()) {
                if (listener != null) {
                    if (remoteStateCallbackImpl != null) {
                        mAssistantServiceInterface.unregisterStateCallback(mPackageName, remoteStateCallbackImpl);
                    }
                    remoteStateCallbackImpl = new RemoteStateCallbackImpl(listener);
                    mAssistantServiceInterface.registerStateCallback(mPackageName, remoteStateCallbackImpl);
                } else if (remoteStateCallbackImpl != null) {
                    mAssistantServiceInterface.unregisterStateCallback(mPackageName, remoteStateCallbackImpl);
                    remoteStateCallbackImpl.listener = null;
                    remoteStateCallbackImpl = null;
                }
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void subscribeDomains(List<String> domainList) {
        if (isAlive()) {
            try {
                mAssistantServiceInterface.subscribeDomains(mPackageName, domainList);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "serviceInterface is died");
        }
    }

    /**
     * 注册/取消注册自定义快捷命令
     */
    public void setShortCommands(List<ShortCommand> lstOfCommand) {
        if (isAlive()) {
            try {
                mAssistantServiceInterface.setShortCommands(mPackageName, JsonUtil.toJSONString(lstOfCommand));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "serviceInterface is died");
        }
    }

    public void startDialogue(int direction) {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.startDialogue(mPackageName, direction);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void stopDialogue() {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.stopDialogue(mPackageName);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int getWakeupDirection() {
        int direction = -1;
        try {
            if (isAlive()) {
                direction = mAssistantServiceInterface.getWakeupDirection();
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return direction;
    }

    public void getHintResult(int num, IHintResultListener listener) {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.getHintResult(mPackageName, num, new RemoteHintCallbackImpl(listener));
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 识别事件埋点
     */
    public void trackAsrEvent(String skill, String scene, String intent, String tts) {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.trackAsrEvent(mPackageName, skill, scene, intent, tts);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String startRegisterVpr(int direction, IVprResultListener listener) {
        try {
            if (isAlive()) {
                // 检测json parser库主动触发集成SDK的crash
                JsonUtil.getJSONParser();
                if (listener != null) {
                    remoteVprResultCallback = new RemoteVprCallbackImpl(listener);
                } else {
                    remoteVprResultCallback.listener = null;
                    remoteVprResultCallback = null;
                }
                return mAssistantServiceInterface.startRegisterVpr(mPackageName, direction, remoteVprResultCallback);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String startRegisterVprWithId(String vprId, int direction, IVprResultListener listener) {
        try {
            if (isAlive()) {
                // 检测json parser库主动触发集成SDK的crash
                JsonUtil.getJSONParser();
                if (listener != null) {
                    remoteVprResultCallback = new RemoteVprCallbackImpl(listener);
                } else {
                    remoteVprResultCallback.listener = null;
                    remoteVprResultCallback = null;
                }
                return mAssistantServiceInterface.startRegisterVprWithId(mPackageName, vprId, direction, remoteVprResultCallback);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void stopRegisterVpr(String vprId, int errCode) {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.stopRegisterVpr(mPackageName, vprId, errCode);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void startRecordingVpr(String vprId, String text) {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.startRecordingVpr(mPackageName, vprId, text);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void stopRecordingVpr(String vprId, String text, int errCode) {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.stopRecordingVpr(mPackageName, vprId, text, errCode);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除声纹
     */
    public int deleteUserVpr(String vprId) {
        try {
            if (isAlive()) {
                return mAssistantServiceInterface.deleteUserVpr(mPackageName, vprId);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取当前声纹注册的数量
     */
    public int getMaxSupportedVprNum() {
        int num = -1;
        try {
            if (isAlive()) {
                num = mAssistantServiceInterface.getMaxSupportedVprNum();
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return num;
    }

    public UserVprInfo getUserVprInfo(String id) {
        try {
            if (isAlive()) {
                String json = mAssistantServiceInterface.getUserVprInfo(id);
                return JsonUtil.fromJson(json, UserVprInfo.class);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int saveUserVprInfo(UserVprInfo info) {
        try {
            if (isAlive()) {
                // 检测json parser库主动触发集成SDK的crash
                JsonUtil.getJSONParser();
                return mAssistantServiceInterface.saveUserVprInfo(mPackageName, JsonUtil.toJSONString(info));
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<UserVprInfo> getRegisteredVprList() {
        try {
            if (isAlive()) {
                String json = mAssistantServiceInterface.getRegisteredVprList();
                return JsonUtil.fromJsonToList(json, UserVprInfo.class);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void enableSwitch(@DhSwitch String switchName, boolean enable) {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.enableSwitch(mPackageName, switchName, enable);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean isEnableSwitch(String switchName) {
        boolean isEnable = false;
        try {
            if (isAlive()) {
                isEnable = mAssistantServiceInterface.isEnableSwitch(switchName);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return isEnable;
    }


    public void setPickupMode(int mode) {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.setPickupMode(mPackageName, mode);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int getPickupMode() {
        int mask = DhPickupMode.CAR_PICKUP_MODE_POSITION;
        try {
            if (isAlive()) {
                mask = mAssistantServiceInterface.getPickupMode();
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return mask;
    }

    public void setContinuousWaitTime(int time) {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.setContinuousWaitTime(mPackageName, time);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int getContinuousWaitTime() {
        int mask = -1;
        try {
            if (isAlive()) {
                mask = mAssistantServiceInterface.getContinuousWaitTime();
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return mask;
    }

    public void setMicZoneEnable(@DhDirection int direction, boolean enable) {
        try {
            if (isAlive()) {
                int location = getLocation(direction);
                int micMask = mAssistantServiceInterface.getUserVoiceMicMask();
                if (enable) {
                    mAssistantServiceInterface.setUserVoiceMicMask(mPackageName, micMask | location);
                } else {
                    mAssistantServiceInterface.setUserVoiceMicMask(mPackageName, micMask & ~location);
                }
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean isMicZoneEnable(@DhDirection int direction) {
        try {
            if (isAlive()) {
                int location = getLocation(direction);
                int micMask = mAssistantServiceInterface.getUserVoiceMicMask();
                return (micMask & location) != 0;
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return true;
    }

    private int getLocation(int direction) {
        switch (direction) {
            case DhDirection.FRONT_LEFT:
                return 1;
            case DhDirection.FRONT_RIGHT:
                return 2;
            case DhDirection.REAR_LEFT:
                return 4;
            case DhDirection.REAR_RIGHT:
                return 8;
            default:
                return direction;
        }
    }

    public void setDialect(DhDialect dialect) {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.changeDialect(mPackageName, JsonUtil.toJSONString(dialect));
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public DhDialect getCurrentDialect() {
        DhDialect dialect = null;
        try {
            if (isAlive()) {
                String json = mAssistantServiceInterface.getCurrentDialect();
                dialect = JsonUtil.fromJson(json, DhDialect.class);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return dialect;
    }

    public void getPvcList(@NonNull IPvcResultListener listener) {
        try {
            if (isAlive()) {
                // 检测json parser库主动触发集成SDK的crash
                JsonUtil.getJSONParser();
                mAssistantServiceInterface.getPvcList(mPackageName, new RemotePvcCallbackImpl(listener));
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setNickname(@NonNull String nickname) {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.setNickname(mPackageName, nickname);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void restoreNickname() {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.restoreNickname(mPackageName);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        String nickName = null;
        try {
            if (isAlive()) {
                nickName = mAssistantServiceInterface.getNickname();
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return nickName;
    }

    public void setDiyGreeting(int direction, String greet) {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.setDiyGreeting(mPackageName, direction, greet);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String getDiyGreeting(int direction) {
        String greet = null;
        try {
            if (isAlive()) {
                greet = mAssistantServiceInterface.getDiyGreeting(direction);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return greet;
    }

    public void startLongASR(int screenType, @NonNull ILongASRListener listener) {
        this.mLongASRListener = listener;
        try {
            if (isAlive()) {
                mAssistantServiceInterface.startLongAsr(mPackageName, screenType, new RemoteLongAsrCallbackImpl());
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void stopLongASR(int screenType) {
        this.mLongASRListener = null;
        try {
            if (isAlive()) {
                mAssistantServiceInterface.stopLongAsr(mPackageName, screenType);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean isLongAsrGoing(int screenType) {
        try {
            if (isAlive()) {
                return mAssistantServiceInterface.isLongAsrGoing(screenType);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void onViewContentChange(String json) {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.onViewContentChange(getUniqueCode(), json);
            } else {
                Log.e(TAG, "serviceInterface is died");
                bindService();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int getMusicPreference() {
        int preference = 0;
        try {
            if (isAlive()) {
                preference = mAssistantServiceInterface.getMusicPreference();
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return preference;
    }

    public void setMusicPreference(int preference) {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.setMusicPreference(mPackageName, preference);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int getVideoPreference() {
        int preference = 0;
        try {
            if (isAlive()) {
                preference = mAssistantServiceInterface.getVideoPreference();
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return preference;
    }

    public void setVideoPreference(int preference) {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.setVideoPreference(mPackageName, preference);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void uploadPersonalEntity(String json) {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.uploadPersonalEntity(mPackageName, json);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setRmsDbListener(IRmsDbListener listener) {
        try {
            if (isAlive()) {
                if (listener != null) {
                    mAssistantServiceInterface.setRmsDbListener(mPackageName, new RemoteRmsCallbackImpl(listener));
                } else {
                    mAssistantServiceInterface.setRmsDbListener(mPackageName, null);
                }
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean isShowingTopCoverView(int displayId) {
        try {
            if (isAlive()) {
                return mAssistantServiceInterface.isShowingTopCoverViewWithDisplayId(mPackageName, displayId);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setTopCoverViewShowing(int displayId, boolean isShow) {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.setTopCoverViewShowing(mPackageName, displayId, isShow);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCurVpaDisplayId() {
        try {
            if (isAlive()) {
                return mAssistantServiceInterface.getCurVpaDisplayId();
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int triggerSceneIntent(SceneIntent intent) {
        try {
            if (isAlive()) {
                return mAssistantServiceInterface.triggerSceneIntent(mPackageName, intent);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getDisplayIdForScreenType(@DhScreenType int screenType) {
        int displayId = -1;
        try {
            if (isAlive()) {
                displayId = mAssistantServiceInterface.getDisplayIdForScreenType(screenType);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return displayId;
    }

    public int getLeftSplitScreenWidth() {
        int with = 850;
        try {
            if (isAlive()) {
                with = mAssistantServiceInterface.getLeftSplitScreenWidth();
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return with;
    }

    public void setNewsPushConfigTime(LocalTime time) {
        try {
            if (isAlive()) {
                String customTime = null;
                if (time != null) {
                    customTime = time.format(NEWS_PUSH_TIME_FORMATTER);
                }
                mAssistantServiceInterface.setNewsPushConfigTime(mPackageName, customTime);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public LocalTime getNewsPushConfigTime() {
        LocalTime time = null;
        try {
            if (isAlive()) {
                String customTime = mAssistantServiceInterface.getNewsPushConfigTime();
                if (customTime != null) {
                    time = LocalTime.parse(customTime, NEWS_PUSH_TIME_FORMATTER);
                }
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException | DateTimeParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public int getAiModelPreference() {
        int preference = 0;
        try {
            if (isAlive()) {
                preference = mAssistantServiceInterface.getAiModelPreference();
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return preference;
    }

    public void setAiModelPreference(int preference) {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.setAiModelPreference(mPackageName, preference);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册agent
     */
    public int registerAgentX(IAgentCallback callback) {
        try {
            if (isAlive()) {
                return mAssistantServiceInterface.registerAgentX(mPackageName, callback);
            } else {
                Log.e(TAG, "registerAgentX serviceInterface is died");
                return -2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 注册asr监听
     */
    public void registerAsrListener(IAsrListener asrListener) {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.registerAsrListener(asrListener);
            } else {
                Log.e(TAG, "registerAsrListener serviceInterface is died");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放asr监听
     */
    public void unregisterAsrListener(IAsrListener asrListener) {
        try {
            if (isAlive()) {
                mAssistantServiceInterface.unregisterAsrListener(asrListener);
            } else {
                Log.e(TAG, "unregisterAsrListener serviceInterface is died");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String startImageRecognize(int screenType) {
        try {
            if (isAlive()) {
                return mAssistantServiceInterface.startImageRecognize(mPackageName, screenType);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setImageRecognizeListener(IImageRecognizeListener listener) {
        try {
            if (isAlive()) {
                if (listener != null) {
                    if (remoteImageRecognizeCallbackImpl != null) {
                        mAssistantServiceInterface.unregisterImageRecognizeCallback(mPackageName, remoteImageRecognizeCallbackImpl);
                    }
                    remoteImageRecognizeCallbackImpl = new RemoteImageRecognizeCallbackImpl(listener);
                    mAssistantServiceInterface.registerImageRecognizeCallback(mPackageName, remoteImageRecognizeCallbackImpl);
                } else if (remoteImageRecognizeCallbackImpl != null) {
                    mAssistantServiceInterface.unregisterImageRecognizeCallback(mPackageName, remoteImageRecognizeCallbackImpl);
                    remoteImageRecognizeCallbackImpl = null;
                }
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int getImageRecognizeState() {
        try {
            if (isAlive()) {
                return mAssistantServiceInterface.getImageRecognizeState();
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 释放
     */
    public void release() {
        try {
            if (isAlive()) {
                if (remoteReadyImpl != null) {
                    mAssistantServiceInterface.unregisterReadyCallback(mPackageName, remoteReadyImpl);
                    remoteReadyImpl = null;
                }
                if (remoteStateCallbackImpl != null) {
                    mAssistantServiceInterface.unregisterStateCallback(mPackageName, remoteStateCallbackImpl);
                    remoteStateCallbackImpl = null;
                }
                if (remoteResultCallbackImpl != null) {
                    mAssistantServiceInterface.unregisterResultCallback(mPackageName, remoteResultCallbackImpl);
                    remoteResultCallbackImpl = null;
                }
                if (remoteImageRecognizeCallbackImpl != null) {
                    mAssistantServiceInterface.unregisterImageRecognizeCallback(mPackageName, remoteImageRecognizeCallbackImpl);
                    remoteImageRecognizeCallbackImpl = null;
                }
                unBindService();
            }
            mReadyListeners.clear();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 远程服务死亡监听
     */
    private final IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {

        @Override
        public void binderDied() {
            Log.e(TAG, "binder died, reBind service...");
            if (mAssistantServiceInterface != null) {
                mAssistantServiceInterface.asBinder().unlinkToDeath(mBinderPoolDeathRecipient, 0);
                mAssistantServiceInterface = null;
            }
            if (remoteStateCallbackImpl != null) {
                remoteStateCallbackImpl.listener.onState(LifeState.ASLEEP);
            }
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(() -> bindService(), 500);
        }
    };

    /**
     * 远程状态回调
     */
    private static class RemoteStateCallbackImpl extends IVAStateCallback.Stub {

        IVAStateListener listener;

        public RemoteStateCallbackImpl(IVAStateListener listener) {
            this.listener = listener;
        }

        @Override
        public void onState(String state) throws RemoteException {
            if (listener != null) {
                listener.onState(state);
            }
        }
    }

    /**
     * 远程结果回调
     */
    private class RemoteResultCallbackImpl extends IVAResultCallback.Stub {

        IVAResultListener listener;

        public RemoteResultCallbackImpl(IVAResultListener listener) {
            this.listener = listener;
        }

        @Override
        public void onAsr(String text, boolean isEnd) throws RemoteException {
            if (listener != null) {
                listener.onAsr(text, isEnd);
            }
        }

        @Override
        public void onNluResult(String result) throws RemoteException {
            NluResult nluResult = JsonUtil.fromJson(result, NluResult.class);
            if (listener != null) {
                listener.onNluResult(nluResult);
            } else {
                Log.w(TAG, mPackageName + " has not set OnVAResultListener");
            }
        }

        @Override
        public void onShortCommand(String command, String result) throws RemoteException {
            NluResult nluResult = JsonUtil.fromJson(result, NluResult.class);
            if (listener != null) {
                listener.onShortCommand(command, nluResult);
            } else {
                Log.w(TAG, mPackageName + " has not set OnVAResultListener");
            }
        }

        @Override
        public void onViewCommand(String command, String result) throws RemoteException {
            if (listener != null) {
                NluResult nluResult = JsonUtil.fromJson(result, NluResult.class);
                if (TextUtils.isEmpty(nluResult.id) || TextUtils.equals(id, nluResult.id)) {
                    listener.onViewCommand(command, nluResult);
                }
            }
        }
    }

    /**
     * 声纹识别远程回调
     */
    private static class RemoteVprCallbackImpl extends IVprCallback.Stub {

        IVprResultListener listener;

        public RemoteVprCallbackImpl(IVprResultListener listener) {
            this.listener = listener;
        }

        @Override
        public void onVprResult(String result) throws RemoteException {
            if (listener != null) {
                VprResult vprResult = JsonUtil.fromJson(result, VprResult.class);
                listener.onVprResult(vprResult);
            }
        }
    }

    /**
     * 声音复刻远程回调
     */
    private static class RemotePvcCallbackImpl extends IPvcCallback.Stub {

        private IPvcResultListener listener;

        public RemotePvcCallbackImpl(IPvcResultListener listener) {
            this.listener = listener;
        }

        @Override
        public void pvcResult(String result) throws RemoteException {
            if (listener != null) {
                List<PvcResult> pvcList = JsonUtil.fromJsonToList(result, PvcResult.class);
                listener.pvcResult(pvcList);
                listener = null;
            }
        }
    }

    /**
     * Hint远程回调
     */
    private static class RemoteHintCallbackImpl extends IHintResultCallback.Stub {
        private IHintResultListener listener;

        public RemoteHintCallbackImpl(IHintResultListener listener) {
            this.listener = listener;
        }

        @Override
        public void hintResult(List<String> result) throws RemoteException {
            if (listener != null) {
                listener.onHintResult(result);
                listener = null;
            }
        }
    }

    /**
     * Hint远程回调
     */
    private static class RemoteRmsCallbackImpl extends IRmsDbCallback.Stub {
        private final IRmsDbListener listener;

        public RemoteRmsCallbackImpl(IRmsDbListener listener) {
            this.listener = listener;
        }

        @Override
        public void onRmsChanged(float rmsDb) throws RemoteException {
            if (listener != null) {
                listener.onRmsChanged(rmsDb);
            }
        }
    }

    /**
     * 声音复刻远程回调
     */
    private class RemoteLongAsrCallbackImpl extends ILongAsrCallback.Stub {

        @Override
        public void onRecognize(String text) throws RemoteException {
            if (mLongASRListener != null) {
                mLongASRListener.onRecognize(text);
            }
        }

        @Override
        public void onEnd(int errCode, String reason) throws RemoteException {
            if (mLongASRListener != null) {
                mLongASRListener.onEnd(errCode, reason);
            }
        }
    }

    private class RemoteImageRecognizeCallbackImpl extends IImageRecognizeCallback.Stub {

        private final IImageRecognizeListener listener;


        public RemoteImageRecognizeCallbackImpl(IImageRecognizeListener listener) {
            this.listener = listener;
        }

        @Override
        public void onIRResult(String sessionId, int screenType, int state, String msg) throws RemoteException {
            if (listener != null) {
                listener.onIRResult(sessionId, screenType, state, msg);
            }
        }
    }

    private String getUniqueCode() {
        return mPackageName + "&" + id;
    }
}
