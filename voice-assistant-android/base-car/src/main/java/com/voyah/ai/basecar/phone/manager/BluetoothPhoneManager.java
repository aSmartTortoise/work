package com.voyah.ai.basecar.phone.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.voyah.cockpit.btphone.IBluetoothPhoneService;
import com.voyah.cockpit.btphone.IVoiceCallBack;
import com.voyah.cockpit.btphone.bean.CallLogInfo;
import com.voyah.cockpit.btphone.bean.ContactInfo;

import java.util.List;

/**
 * @author:lcy
 * @data:2024/4/13
 **/
public class BluetoothPhoneManager {
    private static final String TAG = BluetoothPhoneManager.class.getSimpleName();
    private static final String SERVICE_PACKAGE = "com.voyah.cockpit.btphone";
    private static final String SERVICE_ACTION = "aidlService";

    private static volatile BluetoothPhoneManager sInstance;

    private IBluetoothPhoneService mBinder;
    private InnerServiceConnection mInnerServiceConnection;
    private RemoteCallbackList<IVoiceCallBack> mListenerList = new RemoteCallbackList();


    public static BluetoothPhoneManager getInstance() {
        if (sInstance == null) {
            Class var0 = BluetoothPhoneManager.class;
            synchronized (BluetoothPhoneManager.class) {
                if (sInstance == null) {
                    sInstance = new BluetoothPhoneManager();
                }
            }
        }

        return sInstance;
    }

    public boolean bindService(Context context, InnerServiceConnection connection) {
        this.mInnerServiceConnection = connection;
        Intent intent = new Intent();
        intent.setPackage("com.voyah.cockpit.btphone");
        intent.setAction("aidlService");
        boolean flag = context.getApplicationContext().bindService(intent, this.mConnection, Context.BIND_AUTO_CREATE);
        Log.d("BluetoothPhoneManager", "bindService: " + flag);
        return flag;
    }

    public void unbindService(Context context) {
        this.mInnerServiceConnection = null;
        this.unregisterInnerListener();

        try {
            context.getApplicationContext().unbindService(this.mConnection);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        this.mBinder = null;
    }

    public int openBtApk() {
        if (this.mBinder != null) {
            try {
                return this.mBinder.openBtApk();
            } catch (Exception var2) {
                var2.printStackTrace();
                return -20000;
            }
        } else {
            return -10000;
        }
    }

    public int closeBtApk() {
        if (this.mBinder != null) {
            try {
                return this.mBinder.closeBtApk();
            } catch (Exception var2) {
                var2.printStackTrace();
                return -20000;
            }
        } else {
            return -10000;
        }
    }

    public int answerCall() {
        if (this.mBinder != null) {
            try {
                return this.mBinder.answerCall();
            } catch (Exception var2) {
                var2.printStackTrace();
                return -20000;
            }
        } else {
            return -10000;
        }
    }

    public int disconnectCall() {
        if (this.mBinder != null) {
            try {
                return this.mBinder.disconnectCall();
            } catch (Exception var2) {
                var2.printStackTrace();
                return -20000;
            }
        } else {
            return -10000;
        }
    }

    public int placeCall(String number) {
        if (this.mBinder != null) {
            try {
                return this.mBinder.placeCall(number);
            } catch (Exception var3) {
                var3.printStackTrace();
                return -20000;
            }
        } else {
            return -10000;
        }
    }

    public int getBluetoothConnectState() {
        if (this.mBinder != null) {
            try {
                return this.mBinder.getBluetoothConnectState();
            } catch (Exception var2) {
                var2.printStackTrace();
                return -20000;
            }
        } else {
            return -10000;
        }
    }

    public int getBluetoothCallState() {
        if (this.mBinder != null) {
            try {
                return this.mBinder.getBluetoothCallState();
            } catch (Exception var2) {
                var2.printStackTrace();
                return -20000;
            }
        } else {
            return -10000;
        }
    }

    public int syncConcact() {
        if (this.mBinder != null) {
            try {
                return this.mBinder.syncConcact();
            } catch (Exception var2) {
                var2.printStackTrace();
                return -20000;
            }
        } else {
            return -10000;
        }
    }

    public int openBluetoothSettings() {
        if (this.mBinder != null) {
            try {
                return this.mBinder.openBluetoothSettings();
            } catch (Exception var2) {
                var2.printStackTrace();
                return -20000;
            }
        } else {
            return -10000;
        }
    }

    public int setBluetoothPhoneTab(int tab) {
        if (this.mBinder != null) {
            try {
                return this.mBinder.setBluetoothPhoneTab(tab);
            } catch (Exception var3) {
                var3.printStackTrace();
                return -20000;
            }
        } else {
            return -10000;
        }
    }

    public int switchBluetoothPhoneTab() {
        if (this.mBinder != null) {
            try {
                return this.mBinder.switchBluetoothPhoneTab();
            } catch (Exception var2) {
                var2.printStackTrace();
                return -20000;
            }
        } else {
            return -10000;
        }
    }

    public String getLastIncomingNumber() {
        if (this.mBinder != null) {
            try {
                return this.mBinder.getLastIncomingNumber();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }

        return null;
    }

    public String getLastOutgoingNumber() {
        if (this.mBinder != null) {
            try {
                return this.mBinder.getLastOutgoingNumber();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }

        return null;
    }

    public List<ContactInfo> getContactInfoList() {
        if (this.mBinder != null) {
            try {
                return this.mBinder.getContactInfoList();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }

        return null;
    }


    public List<ContactInfo> getContactInfoList(int start, int end) {
        if (this.mBinder != null) {
            try {
                return this.mBinder.getContactsList(start, end);
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }

        return null;
    }

    public int getContactsSize() {
        if (this.mBinder != null) {
            try {
                return this.mBinder.getContactsSize();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }
        return 0;
    }

    public List<CallLogInfo> getCallLogInfoList() {
        if (this.mBinder != null) {
            try {
                return this.mBinder.getCallLogInfoList();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }

        return null;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("BluetoothPhoneManager", "onServiceConnected:");
            BluetoothPhoneManager.this.mBinder = IBluetoothPhoneService.Stub.asInterface(service);
            BluetoothPhoneManager.this.registerInnerListener();
            if (BluetoothPhoneManager.this.mInnerServiceConnection != null) {
                BluetoothPhoneManager.this.mInnerServiceConnection.onServiceConnected(name, BluetoothPhoneManager.this.mBinder != null);
            }

        }

        public void onServiceDisconnected(ComponentName name) {
            Log.d("BluetoothPhoneManager", "onServiceDisconnected:");
            if (BluetoothPhoneManager.this.mInnerServiceConnection != null) {
                BluetoothPhoneManager.this.mInnerServiceConnection.onServiceDisconnected(name);
            }

        }

        public void onBindingDied(ComponentName name) {
            Log.d("BluetoothPhoneManager", "onBindingDied:");
            if (BluetoothPhoneManager.this.mInnerServiceConnection != null) {
                BluetoothPhoneManager.this.mInnerServiceConnection.onBindingDied(name);
            }

        }

        public void onNullBinding(ComponentName name) {
            Log.d("BluetoothPhoneManager", "onNullBinding:");
            if (BluetoothPhoneManager.this.mInnerServiceConnection != null) {
                BluetoothPhoneManager.this.mInnerServiceConnection.onNullBinding(name);
            }

        }
    };

    private void registerInnerListener() {
        if (this.mBinder == null) {
            Log.d("BluetoothPhoneManager", "registerInnerListener: mBinder == null,connection failed");
        } else {
            try {
                this.mBinder.registerListener(this.mInnerVoiceCallBack);
                Log.d("BluetoothPhoneManager", "registerInnerListener: registerCallback");
            } catch (Exception var2) {
                var2.printStackTrace();
            }

        }
    }

    private void unregisterInnerListener() {
        if (this.mBinder == null) {
            Log.d("BluetoothPhoneManager", "unregisterInnerListener: mBinder == null,connection failed");
        } else {
            try {
                this.mBinder.unregisterListener(this.mInnerVoiceCallBack);
            } catch (Exception var2) {
                var2.printStackTrace();
            }

        }
    }

    private IVoiceCallBack mInnerVoiceCallBack = new IVoiceCallBack.Stub() {
        public void onBluetoothState(int state) throws RemoteException {
            try {
                int num = BluetoothPhoneManager.this.mListenerList.beginBroadcast();

                for (int i = 0; i < num; ++i) {
                    IVoiceCallBack listener = (IVoiceCallBack) BluetoothPhoneManager.this.mListenerList.getBroadcastItem(i);

                    try {
                        listener.onBluetoothState(state);
                    } catch (Exception var6) {
                        var6.printStackTrace();
                    }
                }

                BluetoothPhoneManager.this.mListenerList.finishBroadcast();
            } catch (Exception var7) {
                var7.printStackTrace();
            }

        }

        public void onPhoneCallStateChanged(String number, String name, int state) throws RemoteException {
            try {
                int num = BluetoothPhoneManager.this.mListenerList.beginBroadcast();

                for (int i = 0; i < num; ++i) {
                    IVoiceCallBack listener = (IVoiceCallBack) BluetoothPhoneManager.this.mListenerList.getBroadcastItem(i);

                    try {
                        listener.onPhoneCallStateChanged(number, name, state);
                    } catch (Exception var8) {
                        var8.printStackTrace();
                    }
                }

                BluetoothPhoneManager.this.mListenerList.finishBroadcast();
            } catch (Exception var9) {
                var9.printStackTrace();
            }

        }

        public void syncContactFinish() throws RemoteException {
            try {
                int num = BluetoothPhoneManager.this.mListenerList.beginBroadcast();

                for (int i = 0; i < num; ++i) {
                    IVoiceCallBack listener = (IVoiceCallBack) BluetoothPhoneManager.this.mListenerList.getBroadcastItem(i);

                    try {
                        listener.syncContactFinish();
                    } catch (Exception var5) {
                        var5.printStackTrace();
                    }
                }

                BluetoothPhoneManager.this.mListenerList.finishBroadcast();
            } catch (Exception var6) {
                var6.printStackTrace();
            }

        }

        public void syncContactFail() throws RemoteException {
            try {
                int num = BluetoothPhoneManager.this.mListenerList.beginBroadcast();

                for (int i = 0; i < num; ++i) {
                    IVoiceCallBack listener = (IVoiceCallBack) BluetoothPhoneManager.this.mListenerList.getBroadcastItem(i);

                    try {
                        listener.syncContactFail();
                    } catch (Exception var5) {
                        var5.printStackTrace();
                    }
                }

                BluetoothPhoneManager.this.mListenerList.finishBroadcast();
            } catch (Exception var6) {
                var6.printStackTrace();
            }

        }

        @Override
        public void syncingContact() throws RemoteException {
            try {
                int num = BluetoothPhoneManager.this.mListenerList.beginBroadcast();

                for (int i = 0; i < num; ++i) {
                    IVoiceCallBack listener = (IVoiceCallBack) BluetoothPhoneManager.this.mListenerList.getBroadcastItem(i);

                    try {
                        listener.syncingContact();
                    } catch (Exception var5) {
                        var5.printStackTrace();
                    }
                }

                BluetoothPhoneManager.this.mListenerList.finishBroadcast();
            } catch (Exception var6) {
                var6.printStackTrace();
            }
        }
    };

    public void registerListener(IVoiceCallBack listener) {
        this.mListenerList.register(listener);
    }

    public void unregisterListener(IVoiceCallBack listener) {
        this.mListenerList.unregister(listener);
    }
}
