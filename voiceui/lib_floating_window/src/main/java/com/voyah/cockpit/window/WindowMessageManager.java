package com.voyah.cockpit.window;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.voyah.cockpit.window.model.WindowMessage;

/**
 * author : jie wang
 * date : 2024/3/7 15:29
 * description : 负责与voice ui process ipc的类。
 */
public class WindowMessageManager {

    private static final String TAG = "WindowMessageManager";

    private IVoyahWindowManager mWindowBinder;
    private WindowMessageCallback messageCallback;
    private Context mContext;
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
    };

    private final IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {

        @Override
        public void binderDied() {
            Log.d(TAG, "binderDied");
            mWindowBinder.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mWindowBinder = null;
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
            mWindowBinder = null;
        }
    };

    private WindowMessageManager() {
    }

    public static WindowMessageManager getInstance() {
        return WindowMessageManagerHolder.INSTANCE;
    }

    public void setMessageCallback(WindowMessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    public void init(Context context) {
        mContext = context;
        if (!checkWindowMessageServiceBind()) {
            bindService();
        }
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setAction("com.voyah.window.FLOATING_WIINDOW");
        intent.setPackage("com.voyah.voice.ui");
        try {
            mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            Log.d(TAG, "init: bind remote service error, e:" + e);
        }
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

    public void onVoiceAwake(int voiceServiceMode, int location) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.onVoiceAwake(voiceServiceMode, location);
            } catch (RemoteException e) {
                Log.d(TAG, "onVoiceAwake: error:" + e);
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

    public void dismissCard() {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.dismissCard();
            } catch (RemoteException e) {
                Log.d(TAG, "dismissCard: error:" + e);
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

    public void scrollCard(int direction) {
        if (mWindowBinder != null) {
            try {
                mWindowBinder.scrollCard(direction);
            } catch (RemoteException e) {
                Log.d(TAG, "scrollCard: error:" + e);
            }
        }
    }

    public boolean scrollCardView(int direction) {
        boolean result = false;
        if (mWindowBinder != null) {
            try {
                result = mWindowBinder.scrollCardView(direction);
            } catch (RemoteException e) {
                Log.d(TAG, "scrollCardView: error:" + e);
            }
        }
        return result;
    }

    public interface WindowMessageCallback {
        void onServiceBind();
        void onReceiveWindowMessage(WindowMessage msg);

        void onReceiveVoyahWindowMessage(@NonNull String msgJson);

        void onCardScroll(@NonNull String cardType, int direction, boolean canScroll);
    }

    private static class WindowMessageManagerHolder {
        private static final WindowMessageManager INSTANCE = new WindowMessageManager();
    }
}
