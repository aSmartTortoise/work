package com.voyah.vcos.ttsservices.manager.near;


import android.text.TextUtils;

import com.voyah.vcos.ttsservices.AppContext;
import com.voyah.vcos.ttsservices.BinderList;
import com.voyah.vcos.ttsservices.Constant;
import com.voyah.vcos.ttsservices.utils.CarServiceUtils;
import com.voyah.vcos.ttsservices.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mega.car.MegaCarPropHelper;
import mega.car.config.H56C;
import mega.car.config.Infotainment;
import mega.car.hardware.CarPropertyValue;

/**
 * @author:lcy
 * @data:2024/9/26 就近交互管理类
 **/
public class ProximityInteractionManager {
    private static final String TAG = "ProximityInteractionManager";
    //就近功能是否支持无配置字，需要本地保存
    private static final List<String> supper_near_tts_car_type = Arrays.asList("H37B", "H56C");

    //Infotainment.ID_GENERAL_STS_ANDROID
    private static final int H37B_PROPERTY_ID = 117440561;

    //H56C.IVI_infoSet1_IVI_REGISTERPOSITIONSTATUS
    private static final int H56C_PROPERTY_ID = 1698820135;

    private final int[] statusArray = new int[6]; // 固定长度为6的状态数组
    private final int[] defaultArray = new int[6]; // 超出索引后返回默认使用
    private int fullOneTaskCount = 0; // 全1任务计数器
    private final Set<Integer> activeSingleTasks = new HashSet<>(); // 活跃的单点任务集合


    private int propertyId;
    private boolean isSupperCarType;

    private boolean mIsNearBtTts = false;
    private boolean mIsForbidden = false;

    private String carType;

    private int currentNearLocation = 0;

    private MegaCarPropHelper carPropHelper;


    private static class InnerHolder {
        private static final ProximityInteractionManager instance = new ProximityInteractionManager();
    }

    public static ProximityInteractionManager getInstance() {
        return ProximityInteractionManager.InnerHolder.instance;
    }

    public void init() {
        carPropHelper = MegaCarPropHelper.getInstance(AppContext.instant, null);
        carType = CarServiceUtils.getCarType();
        LogUtils.d(TAG, "init carType:" + carType);
        if (TextUtils.equals(carType, "H37B"))
            propertyId = H37B_PROPERTY_ID;
        else if (TextUtils.equals(carType, "H56C"))
            propertyId = H56C_PROPERTY_ID;
        else
            propertyId = H56C_PROPERTY_ID;
        isSupperCarType = supper_near_tts_car_type.contains(carType);
    }


    public void saveNearByTtsStatus(boolean isNearByTts) {
        LogUtils.d(TAG, "saveNearByTtsStatus isNearByTts:" + isNearByTts);
        mIsNearBtTts = isNearByTts;
    }

    public void saveNearByTtsForbiddenStatus(boolean isForbidden) {
        LogUtils.d(TAG, "saveNearByTtsForbiddenStatus isForbidden:" + isForbidden);
        mIsForbidden = isForbidden;
    }

    public boolean isNearByTts() {
        return mIsNearBtTts;
    }

    /**
     * todo:放在语音服务可能会有一种情况，多任务时切换就近交互，后续还未播报可能不会及时切换
     *
     * @param position    指定播放位置
     * @param packageName 发起播报业务方包名
     * @return true:设置成功 false:设置失败
     * 56C就近交互方位信号：IVI_infoSet1_IVI_REGISTERPOSITIONSTATUS
     * 0x0:ALL
     * 0x1:FirstRowLeft
     * 0x2:FirstRowRight
     * 0x3:SecondRowLeft
     * 0x4:SecondRowRight
     * 0x5:ThirdRowLeft
     * 0x6:ThirdRowRight
     * 0x7:FirstRow
     * 0x8:SecondRow
     * 0x9:ThirdRow
     * 0xA:Left
     * 0xB:Right
     */
    public boolean setNearbyTtsPosition(int position, String packageName, int usage) {
        LogUtils.d(TAG, "setNearbyTtsPosition packageName:" + packageName + " ,usage:" + usage + " ,position:" + position);
        if (usage == Constant.Usage.VOICE_USAGE || usage == Constant.Usage.ADAS_USAGE) {
            LogUtils.d(TAG, "setNearbyTtsPosition currentNearLocation:" + currentNearLocation + " ,mIsNearBtTts:" + mIsNearBtTts + " ,mIsForbidden:" + mIsForbidden + " ,position:" + position + " ,isSupperCarType:" + isSupperCarType);
            if (!mIsNearBtTts || mIsForbidden || !isSupperCarType) {
                setNearbyTtsPosition(0);
            } else {
                if (usage == Constant.Usage.VOICE_USAGE)
                    setNearbyTtsPosition(position);
                else
                    setNearbyTtsPosition(position != 0 ? position : 1);
            }
            return true;
        } else {
            return false;
        }
    }

    private void setNearbyTtsPosition(int position) {
        LogUtils.d(TAG, "setNearbyTtsPosition position:" + position + " ,propertyId:" + propertyId);
        if (propertyId == H56C_PROPERTY_ID) {
            carPropHelper.setRawProp(new CarPropertyValue(propertyId, position));
            currentNearLocation = position;
        } else if (propertyId == H37B_PROPERTY_ID) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("RegisterPositionStatus", position);
                carPropHelper.setRawProp(new CarPropertyValue(propertyId, jsonObject.toString()));
                currentNearLocation = position;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public synchronized int[] generalSts(boolean nearPlay, int position, int code) {
        if (position < 0 || position >= 6) {
            LogUtils.d(TAG, "index is not between 0 and 5");
            return defaultArray;
        }

        if (code == 0) { // 开始任务
            if (!nearPlay) {
                // 非指定位置播报，全设为1
                setAllOne();
                fullOneTaskCount++;
            } else {
                if (position == 0) {
                    // 全1任务
                    setAllOne();
                    fullOneTaskCount++;
                } else {
                    // 单点任务
                    statusArray[position] = 1;
                    activeSingleTasks.add(position);
                }
            }
        } else if (code == 1) { // 结束任务
            if (position == 0) {
                // 结束全1任务
                if (fullOneTaskCount > 0) {
                    fullOneTaskCount--;
                }
                updateStatusAfterTaskEnd();
            } else {
                // 结束单点任务
                if (activeSingleTasks.remove(position)) {
                    updateStatusAfterTaskEnd();
                }
            }
        }
        LogUtils.d(TAG, "generalSts " + Arrays.toString(statusArray));
        return statusArray;
    }

    /**
     * 任务结束后更新状态
     */
    private void updateStatusAfterTaskEnd() {
        if (fullOneTaskCount > 0) {
            // 还有全1任务，保持全1状态
            setAllOne();
        } else if (!activeSingleTasks.isEmpty()) {
            // 没有全1任务但有单点任务，保留这些位置为1
            for (int i = 0; i < statusArray.length; i++) {
                statusArray[i] = activeSingleTasks.contains(i) ? 1 : 0;
            }
        } else {
            // 没有任务了，全设为0
            Arrays.fill(statusArray, 0);
        }
    }

    /**
     * 将数组全部设为1
     */
    private void setAllOne() {
        Arrays.fill(statusArray, 1);
    }
}
