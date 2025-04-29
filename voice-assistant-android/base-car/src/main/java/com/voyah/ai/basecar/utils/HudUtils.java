package com.voyah.ai.basecar.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.autoai.ar.setting.IArAidlInterface;
import com.blankj.utilcode.util.Utils;
import com.voyah.ai.common.utils.LogUtils;

public class HudUtils {

    private static final String TAG = HudUtils.class.getSimpleName();

    private static HudUtils mInstance;
    private static Context mContext;
    private boolean isConnected = false;
    private IArAidlInterface mIArAidlInterface;

    public static HudUtils getInstance() {
        if (null == mInstance) {
            synchronized (HudUtils.class) {
                if (null == mInstance) {
                    mInstance = new HudUtils(Utils.getApp());
                }
            }
        }
        return mInstance;
    }

    private HudUtils(Context mContext) {
        this.mContext = mContext;
        initArHud();
    }

    private void initArHud() {
        Log.d(TAG, "initArHud");
        if (!isConnected) {
            Log.d(TAG, "connected aidl");
            Intent intent = new Intent();
            intent.setPackage("com.crystal.h37.arservice");
            intent.setAction("com.autoai.ar.service.ArService");
            mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "ServiceConnection onServiceConnected");
            isConnected = true;
            mIArAidlInterface = IArAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "ServiceConnection onServiceDisconnected");
            isConnected = false;
            mIArAidlInterface = null;
        }
    };

    /**
     * 判断是否HUD硬件温度超过阈值
     *
     * @return true: 温度过高 false: 温度正常
     */
    public int getTemperatureState() {
        if (mIArAidlInterface != null) {
            try {
                int temp =  mIArAidlInterface.get(18);
                Log.d(TAG, "isTemperatureWarning: " + temp);
                return temp;
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "isTemperatureWarning mIArAidlInterface == null");
        }
        return 1;
    }

    /**
     * 打开/关闭HUD
     *
     * @param hudSwitch 打开/关闭
     */
    public void setArHudSwitch(int hudSwitch) {
        if (mIArAidlInterface != null) {
            Log.d(TAG, "setArHudSwitch open:" + hudSwitch);
            try {
                mIArAidlInterface.set(1, hudSwitch);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "setArHudSwitch mIArAidlInterface == null");
        }
    }

    /**
     * 获取当前HUD的开关状态
     *
     * @return HUD的开关状态
     */
    public int getArHudSwitch() {
        int stat = -1;
        if (mIArAidlInterface != null) {

            try {
                stat = mIArAidlInterface.get(1);
                LogUtils.d(TAG, "getArHudSwitch: " + stat);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
            Log.d(TAG, "getArHudSwitch ret=" + stat);
        } else {
            Log.e(TAG, "getArHudSwitch mIArAidlInterface == null");
        }
        return stat;
    }

    /**
     * 设置显示模式
     *
     * @param hudMode 显示模式
     */
    public void setHudMode(int hudMode) {
        if (mIArAidlInterface != null) {
            try {
                LogUtils.d(TAG, "setHudMode: " + hudMode);
                mIArAidlInterface.set(21, hudMode);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "setHudMode mIArAidlInterface == null");
        }
    }

    /**
     * 获取当前模式
     *
     * @return 当前的显示模式
     */
    public int getHudCurMode() {
        int stat = -1;
        if (mIArAidlInterface != null) {
            Log.d(TAG, "getHudCurMode");
            try {
                stat = mIArAidlInterface.get(21);
                LogUtils.d(TAG, "getHudCurMode: " + stat);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "getHudCurMode mIArAidlInterface == null");
        }
        return stat;
    }

    /**
     * 雪地模式，高对比度模式
     *
     * @param snowMode 打开/关闭
     */
    public void setSnowMode(int snowMode) {
        if (mIArAidlInterface != null) {
            Log.d(TAG, "setSnowMode open:" + snowMode);
            try {
                mIArAidlInterface.set(7, snowMode);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "setSnowMode mIArAidlInterface == null");
        }
    }

    /**
     * 雪地模式，高对比度模式
     *
     * @return 打开/关闭高对比模式
     */
    public int getSnowMode() {
        int stat = -1;
        if (mIArAidlInterface != null) {
            Log.d(TAG, "getSnowMode");
            try {
                stat = mIArAidlInterface.get(7);
                LogUtils.d(TAG, "getSnowMode: " + stat);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "getSnowMode mIArAidlInterface == null");
        }
        return stat;
    }

    /**
     * 高度自适应
     *
     * @param heightAuto 打开/关闭
     */
    public void setHeightAuto(int heightAuto) {
        if (mIArAidlInterface != null) {
            Log.d(TAG, "setHeightAuto open:" + heightAuto);
            try {
                mIArAidlInterface.set(10, heightAuto);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "setHeightAuto mIArAidlInterface == null");
        }
    }

    /**
     * 高度自适应
     *
     * @return 打开/关闭高度自适应
     */
    public int getHeightAuto() {
        int stat = -1;
        if (mIArAidlInterface != null) {
            Log.d(TAG, "getHeightAuto");
            try {
                stat = mIArAidlInterface.get(10);
                LogUtils.d(TAG, "getHeightAuto: " + stat);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "getHeightAuto mIArAidlInterface == null");
        }
        return stat;
    }


    /**
     * 刷新高度
     *
     */
    public void uploadHeightAuto(int heightAuto) {
        if (mIArAidlInterface != null) {
            Log.d(TAG, "uploadHeightAuto");
            try {
                // todo 调用一次刷新高度
                mIArAidlInterface.set(-1, heightAuto);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "uploadHeightAuto mIArAidlInterface == null");
        }
    }

    /**
     * 手动高度调节
     *
     * @param num 调节的数值
     */
    public void setHeight(int num) {
        if (num < 0) {
            num = 0;
        } else if (num > 100) {
            num = 100;
        }
        if (mIArAidlInterface != null) {
            Log.d(TAG, "setHeight:" + num);
            try {
                mIArAidlInterface.set(12, num);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "setHeight mIArAidlInterface == null");
        }
    }

    /**
     * 高度
     *
     * @return 当前高度
     */
    public int getHeight() {
        int stat = -1;
        if (mIArAidlInterface != null) {
            Log.d(TAG, "getHeight");
            try {
                stat = mIArAidlInterface.get(12);
                LogUtils.d(TAG, "getHeight: " + stat);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "getHeight mIArAidlInterface == null");
        }
        return stat;
    }

    /**
     * 手动亮度调节
     *
     * @param num 调节的亮度
     */
    public void setLightNum(int num) {
        if (num < 0) {
            num = 0;
        } else if (num > 100) {
            num = 100;
        }
        if (mIArAidlInterface != null) {
            Log.d(TAG, "setLightNum:" + num);
            try {
                mIArAidlInterface.set(17, num);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "setLightNum mIArAidlInterface == null");
        }
    }

    /**
     * 获取亮度大小
     *
     * @return 当前亮度
     */
    public int getLightNum() {
        int stat = -1;
        if (mIArAidlInterface != null) {
            Log.d(TAG, "getLightNum");
            try {
                stat = mIArAidlInterface.get(17);
                LogUtils.d(TAG, "getLightNum: " + stat);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "getLightNum mIArAidlInterface == null");
        }
        return stat;
    }

    /**
     * 自适应亮度模式
     *
     * @return 打开/关闭亮度自适应模式
     */
    public int getLightMode() {
        int stat = -1;
        if (mIArAidlInterface != null) {
            Log.d(TAG, "getLightNum");
            try {
                stat = mIArAidlInterface.get(8);
                LogUtils.d(TAG, "getLightMode: " + stat);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "getLightNum mIArAidlInterface == null");
        }
        return stat;
    }

    /**
     * 自适应亮度开关
     *
     * @param lightAutoMode 打开/关闭亮度自适应模式
     */
    public void setLightAutoMode(int lightAutoMode) {
        if (mIArAidlInterface != null) {
            try {
                LogUtils.d(TAG, "setLightAutoMode: " + lightAutoMode);
                mIArAidlInterface.set(8, lightAutoMode);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "getLightNum mIArAidlInterface == null");
        }
    }

    /**
     * 路口放大开关
     *
     * @return 打开/关闭
     */
    public int getJADStatus() {
        int stat = -1;
        if (mIArAidlInterface != null) {
            Log.d(TAG, "getJADStatus");
            try {
                stat = mIArAidlInterface.get(24);
                LogUtils.d(TAG, "getJADStatus: " + stat);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "getJADStatus mIArAidlInterface == null");
        }
        return stat;
    }

    /**
     * 路口放大开关
     *
     * @param open 打开/关闭
     */
    public void setJADStatus(boolean open) {
        if (mIArAidlInterface != null) {
            try {
                LogUtils.d(TAG, "setJADStatus: " + open);
                mIArAidlInterface.set(24, open ? 1 : 0);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "setJADStatus mIArAidlInterface == null");
        }
    }

    /**
     * 红绿灯倒计时开关
     *
     * @return 打开/关闭
     */
    public int getTLCStatus() {
        int stat = -1;
        if (mIArAidlInterface != null) {
            Log.d(TAG, "getTLCStatus");
            try {
                stat = mIArAidlInterface.get(25);
                LogUtils.d(TAG, "getTLCStatus: " + stat);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "getTLCStatus mIArAidlInterface == null");
        }
        return stat;
    }

    /**
     * 红绿灯倒计时开关
     *
     * @param open 打开/关闭
     */
    public void setTLCStatus(boolean open) {
        if (mIArAidlInterface != null) {
            try {
                LogUtils.d(TAG, "setTLCStatus: " + open);
                mIArAidlInterface.set(25, open ? 1 : 0);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "setTLCStatus mIArAidlInterface == null");
        }
    }

    /**
     * 当前时间开关
     *
     * @return 打开/关闭
     */
    public int getCTDStatus() {
        int stat = -1;
        if (mIArAidlInterface != null) {
            Log.d(TAG, "getCTDStatus");
            try {
                stat = mIArAidlInterface.get(27);
                LogUtils.d(TAG, "getJADStatus: " + stat);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "getCTDStatus mIArAidlInterface == null");
        }
        return stat;
    }

    /**
     * 当前时间开关
     *
     * @param open 打开/关闭
     */
    public void setCTDStatus(boolean open) {
        if (mIArAidlInterface != null) {
            try {
                LogUtils.d(TAG, "setCTDStatus: " + open);
                mIArAidlInterface.set(27, open ? 1 : 0);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "setCTDStatus mIArAidlInterface == null");
        }
    }

    /**
     * 多媒体信息开关
     *
     * @return 打开/关闭
     */
    public int getMSDStatus() {
        int stat = -1;
        if (mIArAidlInterface != null) {
            Log.d(TAG, "getCTDStatus");
            try {
                stat = mIArAidlInterface.get(26);
                LogUtils.d(TAG, "getMSDStatus: " + stat);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "getMSDStatus mIArAidlInterface == null");
        }
        return stat;
    }

    /**
     * 多媒体信息开关
     *
     * @param open 打开/关闭
     */
    public void setMSDStatus(boolean open) {
        if (mIArAidlInterface != null) {
            try {
                LogUtils.d(TAG, "setMSDStatus: " + open);
                mIArAidlInterface.set(26, open ? 1 : 0);
            } catch (RemoteException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.e(TAG, "setMSDStatus mIArAidlInterface == null");
        }
    }
}
