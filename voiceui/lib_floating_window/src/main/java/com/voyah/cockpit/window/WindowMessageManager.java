package com.voyah.cockpit.window;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import com.voyah.cockpit.window.model.APIResult;
import com.voyah.cockpit.window.model.PageInfo;
import com.voyah.cockpit.window.model.UIMessage;
import com.voyah.cockpit.window.model.WindowMessage;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * author : jie wang
 * date : 2024/3/7 15:29
 * description : 负责与voice ui process ipc的类。
 */
public class WindowMessageManager {

    private static final String TAG = "WindowMessageManager";

    private volatile IVoyahWindowManager mWindowBinder;
    private WindowMessageCallback messageCallback;
    private Context mContext;

    private Handler mHandler;

    private final IReceiveWindowMsgCallback mWindowMsgCallback = new IReceiveWindowMsgCallback.Stub() {

        @Override
        public void onReceiveWindowMessage(WindowMessage msg) throws RemoteException {
            Log.d(TAG, "onReceiveWindowMessage: msg:" + msg);
            if (messageCallback != null) {
                messageCallback.onReceiveWindowMessage(msg);
            }
        }

        @Override
        public void onReceiveVoyahWindowMessage(String msgJson) throws RemoteException {
            Log.d(TAG, "onReceiveVoyahWindowMessage: msgJson:" + msgJson);
            if (messageCallback != null) {
                messageCallback.onReceiveVoyahWindowMessage(msgJson);
            }
        }

        @Override
        public void onCardScroll(String cardType, int direction, boolean canScroll) throws RemoteException {
            Log.d(TAG, "onCardScroll: cardType:" + cardType + " direction:" + direction +
                    " canScroll:" + canScroll);
            if (messageCallback != null) {
                messageCallback.onCardScroll(cardType, direction, canScroll);
            }
        }

        @Override
        public void interruptStreamInput(String domainType) throws RemoteException {
            Log.d(TAG, "interruptStreamInput: domainType:" + domainType);
        }


    };

    private final IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {

        @Override
        public void binderDied() {
            Log.d(TAG, "binderDied");
            if (mWindowBinder != null) {
                mWindowBinder.asBinder().unlinkToDeath(mDeathRecipient, 0);
                mWindowBinder = null;
            }
            if (messageCallback != null) {
                messageCallback.onBinderDied();
            }
            bindService();
        }
    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: name:" + name.getClassName());
            mWindowBinder = IVoyahWindowManager.Stub.asInterface(service);
            try {
                mWindowBinder.asBinder().linkToDeath(mDeathRecipient, 0);
                mWindowBinder.registerReceiveCallback(mWindowMsgCallback);
                if (messageCallback != null) {
                    messageCallback.onServiceBind();
                }
            } catch (RemoteException e) {
                Log.d(TAG, "onServiceConnected: registerReceiveCallback error:" + e);
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: name:" + name.getClassName());
            mWindowBinder = null;
            if (messageCallback != null) {
                messageCallback.onServiceDisconnected();
            }
        }
    };

    private WindowMessageManager() {
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
    }

    public static WindowMessageManager getInstance() {
        return WindowMessageManagerHolder.INSTANCE;
    }

    public void setMessageCallback(WindowMessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    public void init(Context context) {
        mContext = context;
        bindService();
    }

    private void bindService() {
        AtomicInteger count = new AtomicInteger();
        Runnable binderServiceTask = new Runnable() {

            @Override
            public void run() {
                boolean bindFlag = checkWindowMessageServiceBind();
                Log.d(TAG, "bindService: bindFlag:" + bindFlag);
                if (!bindFlag) {
                    Intent intent = new Intent();
                    intent.setAction("com.voyah.window.FLOATING_WIINDOW");
                    intent.setPackage("com.voyah.voice.ui");
                    try {
                        boolean result = mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
                        Log.d(TAG, "bindService: result:" + result);
                    } catch (Exception e) {
                        Log.w(TAG, "init: bind remote service error, e:" + e);
                    }

                    count.getAndIncrement();
                    Log.d(TAG, "bindService: bind service count:" + count);
                    if (count.get() < 5) {
                        mHandler.postDelayed(this, 2000);
                    } else {
                        mHandler.removeCallbacks(this);
                    }
                } else {
                    mHandler.removeCallbacks(this);
                }
            }
        };

        mHandler.post(binderServiceTask);
    }

    private boolean checkWindowMessageServiceBind() {
        return mWindowBinder != null && mWindowBinder.asBinder().isBinderAlive();
    }

    /**
     * 发送window message 到 voice ui 进程
     * @param message json格式。
     */
    public void sendWindowMessage(String message) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.sendWindowMessage(message);
            } catch (RemoteException e) {
                Log.d(TAG, "sendWindowMessage: error:" + e);
            }
        }
    }

    public void onVoiceAwake(int voiceServiceMode, int languageType, int location) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.onVoiceAwake(voiceServiceMode, languageType, location);
            } catch (RemoteException e) {
                Log.d(TAG, "onVoiceAwake: error:" + e);
            }
        }
    }

    public void showVoiceVpa(UIMessage uiMessage) {
        if (mWindowBinder != null) {
            try {
                Log.i(TAG, "showVoiceVpa: uiMessage" + uiMessage.toString());
                mWindowBinder.showVoiceVpa(uiMessage);
            } catch (RemoteException e) {
                Log.w(TAG, "showVoiceVpa: error:" + e);
            }
        } else {
            Log.w(TAG, "showVoiceVpa: mWindowBinder is null...");
        }
    }

    public void showVoiceView(int voiceServiceMode, int languageType, int location) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.showVoiceView(voiceServiceMode, languageType, location);
            } catch (RemoteException e) {
                Log.d(TAG, "showVoiceView: error:" + e);
            }
        }
    }

    public void onVoiceListening() {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.onVoiceListening();
            } catch (RemoteException e) {
                Log.d(TAG, "onVoiceListening: error:" + e);
            }
        }
    }

    public void onVoiceSpeaking() {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.onVoiceSpeaking();
            } catch (RemoteException e) {
                Log.d(TAG, "onVoiceSpeaking: error:" + e);
            }
        }
    }

    public void onVoiceExit() {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.onVoiceExit();
            } catch (RemoteException e) {
                Log.d(TAG, "onVoiceExit: error:" + e);
            }
        }
    }

    public void inputTypewriterText(String text, int textStyle) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.inputTypewriterText(text, textStyle);
            } catch (RemoteException e) {
                Log.d(TAG, "inputTypewriterText: error:" + e);
            }
        }
    }

    public void showCard(String cardJson) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.showCard(cardJson);
            } catch (RemoteException e) {
                Log.d(TAG, "showCard: error:" + e);
            }
        }
    }

    public void updateCardData(String cardJson) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.updateCardData(cardJson);
            } catch (RemoteException e) {
                Log.d(TAG, "updateCardData: error:" + e);
            }
        }
    }

    public void dismissCard(int screenType) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.dismissCard(screenType);
            } catch (RemoteException e) {
                Log.d(TAG, "dismissCard: error:" + e);
            }
        }
    }

    public void dismissVoiceView(int screenType) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.dismissVoiceView(screenType);
            } catch (RemoteException e) {
                Log.d(TAG, "dismissVoiceView: error:" + e);
            }
        }
    }

    public void showExecuteFeedbackWindow(String feedbackJson) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.showExecuteFeedbackWindow(feedbackJson);
            } catch (RemoteException e) {
                Log.d(TAG, "showExecuteFeedbackWindow: error:" + e);
            }
        }
    }

    public void dismissFeedbackWindow(int voiceDirection) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.dismissExecuteFeedbackWindow(voiceDirection);
            } catch (RemoteException e) {
                Log.d(TAG, "dismissFeedbackWindow: error:" + e);
            }
        }
    }

    public void scrollCard(int direction, int screenType) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.scrollCard(direction, screenType);
            } catch (RemoteException e) {
                Log.d(TAG, "scrollCard: error:" + e);
            }
        }
    }

    public boolean scrollCardView(int direction, int screenType) {
        boolean result = false;
        if (mWindowBinder != null) {
            try {
                result = mWindowBinder.scrollCardView(direction, screenType);
            } catch (RemoteException e) {
                Log.d(TAG, "scrollCardView: error:" + e);
            }
        }
        return result;
    }

    public boolean nextPage(int pageOffset, int screenType) {
        boolean result = false;
        if (mWindowBinder != null) {
            try {
                result = mWindowBinder.nextPage(pageOffset, screenType);
            } catch (RemoteException e) {
                Log.d(TAG, "nextPage: error:" + e);
            }
        }
        return result;
    }

    public PageInfo getCurrentPage(int screenType) {
        PageInfo pageInfo = null;
        if (mWindowBinder != null) {
            try {
                pageInfo = mWindowBinder.getCurrentPage(screenType);
            } catch (RemoteException e) {
                Log.d(TAG, "nextPage: error:" + e);
            }
        }
        return pageInfo;
    }

    public boolean setCurrentItem(int itemIndex, int screenType) {
        boolean isSelected = false;
        if (mWindowBinder != null) {
            try {
                isSelected = mWindowBinder.setCurrentItem(itemIndex, screenType);
            } catch (RemoteException e) {
                Log.d(TAG, "setCurrentItem: error:" + e);
            }
        }
        return isSelected;
    }

    public boolean setCurrentPage(int pageIndex, int screenType) {
        boolean isSelected = false;
        if (mWindowBinder != null) {
            try {
                isSelected = mWindowBinder.setCurrentPage(pageIndex, screenType);
            } catch (RemoteException e) {
                Log.d(TAG, "setCurrentPage: error:" + e);
            }
        }
        return isSelected;
    }

    public String getCurrentCardType(int screenType) {
        String cardType = null;
        if (mWindowBinder != null) {
            try {
                cardType = mWindowBinder.getCurrentCardType(screenType);
            } catch (RemoteException e) {
                Log.d(TAG, "getCurrentCardType: error:" + e);
            }
        }
        return cardType;
    }

    public void setVoiceMode(int voiceMode) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.setVoiceMode(voiceMode);
            } catch (RemoteException e) {
                Log.w(TAG, "setVoiceMode: error:" + e);
            }
        }
    }

    public void setLanguageType(int languageType) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.setLanguageType(languageType);
            } catch (RemoteException e) {
                Log.w(TAG, "setLanguageType: error:" + e);
            }
        }
    }

    public void setScreenEnable(int screenType, boolean enable) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.setScreenEnable(screenType, enable);
            } catch (RemoteException e) {
                Log.w(TAG, "setScreen: error:" + e);
            }
        }
    }

    public void setCeilingScreenEnable(boolean enable) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.setScreenEnable(2, enable);
            } catch (RemoteException e) {
                Log.w(TAG, "setCeilingScreenEnable: error:" + e);
            }
        }
    }

    public int getCardViewRegisterViewCmdState(int displayId, int screenType) {
        int result = APIResult.INIT;
        if (mWindowBinder != null) {
            try {
                result = mWindowBinder.getCardViewRegisterViewCmdState(displayId, screenType);
            } catch (RemoteException e) {
                result = APIResult.ERROR;
                Log.w(TAG, "getCardViewRegisterViewCmdState: error:" + e);
            }
        }
        return result;
    }

    public int getCardState(int screenType) {
        int result = APIResult.INIT;
        if (mWindowBinder != null) {
            try {
                result = mWindowBinder.getCardState(screenType);
            } catch (RemoteException e) {
                Log.d(TAG, "getCardState: error:" + e);
            }
        }
        return result;
    }

    public void showWave(int location) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.showWave(location);
            } catch (RemoteException e) {
                Log.d(TAG, "showWave: error:" + e);
            }
        }
    }
    public void dismissWave() {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.dismissWave();
            } catch (RemoteException e) {
                Log.d(TAG, "dismissWave: error:" + e);
            }
        }
    }
    //VPA显示需要的属性
    //text      文本
    //textStyle     文本样式
    //privacyMode       是否隐私模式
    //langType      方言识别
    //voiceMode     聆听态识别态动作态

    public void showVpa(String text, int textStyle, String voiceState) {
        if (mWindowBinder != null) {
            try {
                Log.d(TAG, "show Vpa:[text:" + text + "][textStyle:" + textStyle + "][voiceState:" + voiceState + "]");
                mWindowBinder.showVpa(text, textStyle, voiceState);
            } catch (RemoteException e) {
                Log.d(TAG, "showVpa: error:" + e);
            }
        }
    }

    public Bundle call(String method, Bundle bundle) {
        Bundle result = null;
        if (mWindowBinder != null) {
            try {
                result = mWindowBinder.call(method, bundle);
            } catch (RemoteException e) {
                Log.d(TAG, "call: error:" + e);
            }
        }
        return result;
    }

    public void callAsync(String method, Bundle bundle, ICallback callback) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.callAsync(method, bundle, callback);
            } catch (RemoteException e) {
                Log.d(TAG, "callAsync: error:" + e);
            }
        }
    }

    public void register(String name, ICallback callback) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.register(name, callback);
            } catch (RemoteException e) {
                Log.d(TAG, "register: error:" + e);
            }
        }
    }

    public void unRegister(String name) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.unRegister(name);
            } catch (RemoteException e) {
                Log.d(TAG, "unregister: error:" + e);
            }
        }
    }


    public interface WindowMessageCallback {
        void onServiceBind();

        void onBinderDied();

        void onServiceDisconnected();

        void onReceiveWindowMessage(WindowMessage msg);

        void onReceiveVoyahWindowMessage(@NonNull String msgJson);

        void onCardScroll(@NonNull String cardType, int direction, boolean canScroll);
    }

    private static class WindowMessageManagerHolder {
        private static final WindowMessageManager INSTANCE = new WindowMessageManager();
    }
}
