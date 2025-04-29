package com.voyah.ai.logic.dc;

import android.text.TextUtils;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.GearInfo;
import com.voice.sdk.device.carservice.dc.DoorControlInterface;
import com.voice.sdk.device.carservice.signal.CarSettingSignal;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.DoorSignal;
import com.voice.sdk.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class DoorControlImpl  extends AbsDevices implements DoorControlInterface  {

    private static final String TAG = DoorControlImpl.class.getSimpleName();

    @Override
    public String getDomain() {
        return "doorControl";
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        int index = 0;

        int start = str.indexOf("@");
        if (start == -1) {
            return str;
        }
        int end = str.indexOf("}");
        String key = str.substring(start + 2, end);
        switch (key) {
            case "left_right":
                String pos = map.get("left_right").toString();
                str = str.replace("@{left_right}", pos);
                break;
            case "sliding_door_mode":
                String poss = map.get("sliding_door_mode").toString();
                str = str.replace("@{sliding_door_mode}", poss);
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;
        }
        LogUtils.e(TAG, "tts :" + start + " " + end + " " + key + " " + index);

        LogUtils.e(TAG, "tts :" + str);
        return str;
    }


    /**
     * 当前滑移门是否是已打开的状态
     */
    public boolean scratchDoorIsOpen(HashMap<String, Object> map){
        boolean isOpen =false;

        int stateSwRl = operator.getIntProp(DoorSignal.DOOR_ELECTRIC_SWITCH_IS_OPEN_LEFT);
//        int stateSwRl = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_PSDENADBLESTA);
        int stateSwRr = operator.getIntProp(DoorSignal.DOOR_ELECTRIC_SWITCH_IS_OPEN_RIGHT);
//        int stateSwRr = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_PSDENADBLESTA);
        if(stateSwRr ==1){
            isOpen = true;
        }
        return isOpen;
    }



    /**
     * 打开滑移门
     */
    public void setOpenScratch(HashMap<String, Object> map) {
        LogUtils.e(TAG,"-------setOpenScratch-------start=======");

        String positions = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(positions)) {
            switch (positions){
                case "first_row_left"://主驾
                case "second_row_left"://二排左
                case "left_side"://左侧
                case "rear_side_left"://左后
                case "third_row_left"://三排左
//                    carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RLDOORCTL_CMD, 2);
                    operator.setIntProp(DoorSignal.DOOR_SWITCH_LEFT,  2);
                    map.put("left_right","左侧");
                    break;
                case "first_row_right"://副驾
                case "second_row_right"://二排右
                case "right_side":
                case "rear_side_right"://右后
                case "third_row_right":
//                    carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RRDOORCTL_CMD, 2);
                    operator.setIntProp(DoorSignal.DOOR_SWITCH_RIGHT,  2);
                    map.put("left_right","右侧");
                    break;
                case "second_side":
                    int soundSource = getSoundSourcePos(map); //声源位置
                    switch (soundSource) {
                        case 0:
                        case 2:
                        case 4:
//                            carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RLDOORCTL_CMD, 2);
                            operator.setIntProp(DoorSignal.DOOR_SWITCH_LEFT,  2);
                            map.put("left_right","左侧");
                            break;
                        case 1:
                        case 3:
                        case 5:
//                            carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RRDOORCTL_CMD, 2);
                            operator.setIntProp(DoorSignal.DOOR_SWITCH_RIGHT,  2);
                            map.put("left_right","右侧");
                            break;
                        default:
                            LogUtils.e(TAG,"------声源位置-------"+soundSource+"==================");
                            break;
                    }
                    break;
                default://主驾副驾，或者后排中间等，需要提示用户说打开左边还是右边的，这里不做处理
                    LogUtils.e(TAG,"------指定位置-------"+positions+"==================");
                    break;
            }
        }else {
            int soundSource = getSoundSourcePos(map); //声源位置
            switch (soundSource) {
                case 0:
                case 2:
                case 4:
//                    carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RLDOORCTL_CMD, 2);
                    operator.setIntProp(DoorSignal.DOOR_SWITCH_LEFT,  2);
                    map.put("left_right","左侧");
                    break;
                case 1:
                case 3:
                case 5:
//                    carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RRDOORCTL_CMD, 2);
                    operator.setIntProp(DoorSignal.DOOR_SWITCH_RIGHT,  2);
                    map.put("left_right","右侧");
                    break;
                default:
                    LogUtils.e(TAG,"------声源位置-------"+soundSource+"==================");
                    break;
            }
        }
    }


    /**
     * 关闭滑移门
     */
    public void setCloseScratch(HashMap<String, Object> map){

        String positions = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(positions)) {
            switch (positions){
                case "first_row_left"://主驾
                case "second_row_left"://二排左
                case "left_side":
                case "rear_side_left"://左后
                case "third_row_left"://三排左
//                    carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RLDOORCTL_CMD, 1);//1 是关，2是开
                    operator.setIntProp(DoorSignal.DOOR_SWITCH_LEFT,  1);//1 是关，2是开
                    map.put("left_right","左侧");
                    break;
                case "first_row_right"://副驾
                case "second_row_right"://二排右
                case "right_side":
                case "rear_side_right"://右后
                case "third_row_right":
//                    carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RRDOORCTL_CMD, 1);//1 是关，2是开
                    operator.setIntProp(DoorSignal.DOOR_SWITCH_RIGHT,  1);//1 是关，2是开
                    map.put("left_right","右侧");
                    break;
                case "second_side":
//                    carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RLDOORCTL_CMD, 1);//1 是关，2是开
//                    carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RRDOORCTL_CMD, 1);//1 是关，2是开
                    operator.setIntProp(DoorSignal.DOOR_SWITCH_LEFT,  1);//1 是关，2是开
                    operator.setIntProp(DoorSignal.DOOR_SWITCH_RIGHT,  1);//1 是关，2是开
                    map.put("left_right","二排");
                    break;
                default://其他位置
                    break;
            }
        }else{//没有指定关闭哪个滑移门的时候，根据声源位置判断
            ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
            int curPosition = curSoundPositions.get(0);
            if(curPosition ==0 ||curPosition ==1){//代表是前排  前排关闭左右两侧的滑移门
//                carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RLDOORCTL_CMD, 1);//1 是关，2是开
//                carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RRDOORCTL_CMD, 1);//1 是关，2是开
                operator.setIntProp(DoorSignal.DOOR_SWITCH_LEFT,  1);//1 是关，2是开
                operator.setIntProp(DoorSignal.DOOR_SWITCH_RIGHT,  1);//1 是关，2是开
                map.put("left_right","全车");
            }else{//代表是后排
                if(curPosition ==2 ||curPosition ==4){//关闭左侧滑移门
//                    carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RLDOORCTL_CMD, 1);//1 是关，2是开
                    operator.setIntProp(DoorSignal.DOOR_SWITCH_LEFT,  1);//1 是关，2是开
                    map.put("left_right","左侧");
                }else {
//                    carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RRDOORCTL_CMD, 1);//1 是关，2是开
                    operator.setIntProp(DoorSignal.DOOR_SWITCH_RIGHT,  1);//1 是关，2是开
                    map.put("left_right","右侧");
                }
            }
        }
//        carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RLDOORCTL_CMD, 1);//1 是关，2是开
//        carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RRDOORCTL_CMD, 1);
    }







    /**
     *  滑移门是否暂停，默认没有暂停
     * @return
     */
    public boolean isPauseScratch(HashMap<String, Object> map){
        boolean isPause = false;
        String positions = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(positions)) {
            switch (positions){
                case "first_row_left"://主驾
                case "second_row_left"://二排左
                case "rear_side_left"://后排左
                case "left_side"://左侧
                case "third_row_left"://三排左
//                    int stateRl = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_DOORWORKST_RSP);
                    int stateRl = operator.getIntProp(DoorSignal.DOOR_OPENING_LEFT);
                    if(stateRl == 0){//代表左侧滑移门是暂停的
                        isPause =true;
                        map.put("left_right","左侧");
                    }
                    break;
                case "first_row_right"://副驾
                case "second_row_right"://二排右
                case "rear_side_right":
                case "right_side":
                case "third_row_right":
//                    int stateRr = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_DOORWORKST_RSP);
                    int stateRr = operator.getIntProp(DoorSignal.DOOR_OPENING_RIGHT);
                    if(stateRr == 0){//代表右侧滑移门是暂停的
                        isPause =true;
                        map.put("left_right","右侧");
                    }
                    break;
                default:
                    break;
            }

        }else{//声源位置
            ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
            int curPosition = curSoundPositions.get(0);
            if(curPosition ==0 ||curPosition ==2 || curPosition ==4){//代表是左侧
//                int stateRl = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_DOORWORKST_RSP);
                int stateRl = operator.getIntProp(DoorSignal.DOOR_OPENING_LEFT);
                if(stateRl == 0){//代表左侧滑移门是暂停的
                    isPause =true;
                    map.put("left_right","左侧");
                }
            }else{//代表是右侧
//                int stateRr = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_DOORWORKST_RSP);
                int stateRr = operator.getIntProp(DoorSignal.DOOR_OPENING_RIGHT);
                if(stateRr == 0){//代表右侧滑移门是暂停的
                    isPause =true;
                    map.put("left_right","右侧");
                }
            }
        }
        return  isPause;
    }


    /**
     *  执行暂停滑移门
     * @return
     */
    public void doingPauseScratch(HashMap<String, Object> map){
        String positions = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(positions)) {
            switch (positions){
                case "first_row_left"://主驾
                case "second_row_left"://二排左
                case "rear_side_left"://后排左
                case "left_side"://左侧
                case "third_row_left"://三排左
//                    carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RLDOORCTL_CMD, 3);
                    operator.setIntProp(DoorSignal.DOOR_SWITCH_LEFT,3);
                    map.put("left_right","左侧");
                    break;
                case "first_row_right"://副驾
                case "second_row_right"://二排右
                case "rear_side_right"://右后
                case "right_side":
                case "third_row_right":
//                    carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RRDOORCTL_CMD, 3);
                    operator.setIntProp(DoorSignal.DOOR_SWITCH_RIGHT,3);
                    map.put("left_right","右侧");
                    break;
                default:
                    break;
            }

        }else {
            ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
            int curPosition = curSoundPositions.get(0);
            if(curPosition ==0 ||curPosition ==2 ||curPosition ==4){//代表是左侧
//                carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RLDOORCTL_CMD, 3);
                operator.setIntProp(DoorSignal.DOOR_SWITCH_LEFT,3);
                map.put("left_right","左侧");
            }else{//代表是右侧
//                carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RRDOORCTL_CMD, 3);
                operator.setIntProp(DoorSignal.DOOR_SWITCH_RIGHT,3);
                map.put("left_right","右侧");
            }
        }
    }

    //-------------------以下为打开滑移门-----------------
    /**
     * 判断是否是p挡  以及判断当前中控锁是否解锁
     * 只有是P档且解锁的时候，才可以触发 滑移门相关的功能
     * @param map
     * @return
     */
    public boolean getFrontStatus(HashMap<String, Object> map) {
        LogUtils.e(TAG,"----getFrontStatus------中控是否解锁且为P档----"+ CommonSignal.COMMON_GEAR_INFO+"===start===============");
        boolean frontStatus = false;//默认为false ,必须P档并且是中控解锁的情况下，才可以执行相关的操作
        int drvStatus = operator.getIntProp(CommonSignal.COMMON_GEAR_INFO);//获取当前的挡位
        LogUtils.d(TAG, "getDrvStatus :" + drvStatus);
        int state = operator.getIntProp(CarSettingSignal.CARSET_DOOR_LOCK);//获取当前的中控锁的状态  0是上锁  1是解锁
        LogUtils.i(TAG, "getCentralLockStatus : state-" + state);
        if((drvStatus == GearInfo.CARSET_GEAR_PARKING)&&(state ==1)){//如果是P档，且是解锁
            frontStatus =true;
        }
        LogUtils.e(TAG,"----getFrontStatus-----中控是否解锁且为P档-----"+frontStatus+"===end===============");

        return frontStatus;
    }

    /**
     *
     * @return  滑移门电动开关是否开启  默认是开启
     */
    public boolean isSwitchOpen(HashMap<String, Object> map){
        LogUtils.e(TAG,"----isSwitchOpen------滑移门开关是否开启----===end===============");
        boolean isOpen = true;
        String positions = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(positions)) {
            switch (positions){
                case "first_row_left"://主驾
                case "second_row_left"://二排左
                case "rear_side_left"://后排左
                case "left_side"://左侧
                case "third_row_left"://三排左
//                    int stateSwRl = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_PSDENADBLESTA);
                    int stateSwRl = operator.getIntProp(DoorSignal.DOOR_ELECTRIC_SWITCH_IS_OPEN_LEFT);
                    if(stateSwRl != 1){//代表左侧滑移门开关是关闭的
                        isOpen =false;
                    }
                    break;
                case "first_row_right"://副驾
                case "second_row_right"://二排右
                case "rear_side_right":
                case "right_side":
                case "third_row_right":
//                    int stateSwRr = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_PSDENADBLESTA);
                    int stateSwRr = operator.getIntProp(DoorSignal.DOOR_ELECTRIC_SWITCH_IS_OPEN_RIGHT);
                    if(stateSwRr != 1){//代表右侧滑移门开关是关闭的
                        isOpen =false;
                    }
                    break;
                case "second_side":
//                    int stateSwRLeft = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_PSDENADBLESTA);
//                    int stateSwRRight = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_PSDENADBLESTA);
                    int stateSwRLeft = operator.getIntProp(DoorSignal.DOOR_ELECTRIC_SWITCH_IS_OPEN_LEFT);
                    int stateSwRRight = operator.getIntProp(DoorSignal.DOOR_ELECTRIC_SWITCH_IS_OPEN_RIGHT);
                    if(stateSwRLeft != 1  || stateSwRRight != 1){//代表左侧或者右侧滑移门电动开关是关闭
                        isOpen =false;
                    }
                    break;
                default:
                    break;
            }

        }else {//根据音源位置判断
            ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
            int curPosition = curSoundPositions.get(0);
            if(curPosition ==0 ||curPosition ==2 ||curPosition ==4 ){//代表是左侧
//                int stateSwRl = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_PSDENADBLESTA);
                int stateSwRl = operator.getIntProp(DoorSignal.DOOR_ELECTRIC_SWITCH_IS_OPEN_LEFT);
                if(stateSwRl != 1){//代表左侧滑移门开关是关闭的
                    isOpen =false;
                }
            }else{//右侧
//                int stateSwRr = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_PSDENADBLESTA);
                int stateSwRr = operator.getIntProp(DoorSignal.DOOR_ELECTRIC_SWITCH_IS_OPEN_RIGHT);
                if(stateSwRr != 1){//代表右侧滑移门开关是关闭的
                    isOpen =false;
                }
            }
        }
        LogUtils.e(TAG,"----isSwitchOpen---滑移门开关是否开启--------"+isOpen+"===end===============");
        return isOpen;
    }

    /**
     * 判断用户是否说的是打开左侧滑移门 或者是关闭左侧滑移门
     */
    public boolean isOpenLeftScratch(HashMap<String, Object> map) {
        LogUtils.e(TAG,"----isOpenLeftScratch-----判断用户说的是否是左侧-----"+"===start===============");
        boolean openLeftScratch = false;
        String switch_mode = (String) getValueInContext(map, "positions");
        if (switch_mode == null) {
            return openLeftScratch;
        }
        if(switch_mode.contains("left_side") || switch_mode.contains("rear_side_left")){
            openLeftScratch = true;
        }
        return openLeftScratch;
    }

    /**
     * 判断用户是否说的是打开右侧滑移门或者是关闭右侧滑移门
     */
    public boolean isOpenRightScratch(HashMap<String, Object> map) {
        LogUtils.e(TAG,"----isOpenRightScratch------判断用户说的是否是右侧------===start===============");
        boolean openRightScratch = false;
        String switch_mode = (String) getValueInContext(map, "positions");
        if (switch_mode == null) {
            return openRightScratch;
        }
        if(switch_mode.contains("right_side") || switch_mode.contains("rear_side_right")){
            openRightScratch = true;
        }
        return openRightScratch;
    }

    /**
     * 判断声源位置是否是前排
     */
    public boolean isFrontPosition(HashMap<String, Object> map){
        LogUtils.e(TAG,"----isFrontPosition------------===start===============");
        boolean isFront = false;//默认不是前排声源
        ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
        int curPosition = curSoundPositions.get(0);
        if(curPosition ==0 ||curPosition ==1 ){
            isFront = true;//代表是前排声源
        }
        LogUtils.e(TAG,"----isFrontPosition----------isFront====="+isFront+"===end===============");
        return  isFront;
    }


    /**
     * 滑移门是否已经开启   默认没开启
     * @param map
     * @return
     */
    public boolean isCloseScratch(HashMap<String, Object> map){
        LogUtils.e(TAG,"----isCloseScratch-----是否已开启-----===start===============");
        boolean isClosed = false;
        String positions = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(positions)) {
            switch (positions){
                case "first_row_left"://主驾
                case "second_row_left"://二排左
                case "left_side":
                case "rear_side_left"://左后
                case "third_row_left"://三排左
//                    int stateRl = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_RLDOOR_ST);
                    int stateRl = operator.getIntProp(DoorSignal.DOOR_IS_OPEN_LEFT);
                    if(stateRl == 3){
                        isClosed =true;
                        map.put("left_right","左侧");
                    }
                    break;
                case "first_row_right"://副驾
                case "second_row_right"://二排右
                case "right_side":
                case "rear_side_right"://右后
                case "third_row_right":
//                    int stateRr = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_RRDOOR_ST);
                    int stateRr = operator.getIntProp(DoorSignal.DOOR_IS_OPEN_RIGHT);
                    if(stateRr == 3){
                        isClosed =true;
                        map.put("left_right","右侧");
                    }
                    break;
                case "second_side"://二排
//                    int stateLeft = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_RLDOOR_ST);
//                    int stateRight = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_RRDOOR_ST);
                    int stateLeft =  operator.getIntProp(DoorSignal.DOOR_IS_OPEN_LEFT);
                    int stateRight =  operator.getIntProp(DoorSignal.DOOR_IS_OPEN_RIGHT);

                    if(stateLeft == 3 && stateRight == 3){//判断两个门是否都打开
                        isClosed =true;
                        map.put("left_right","二排");
                    }else{//证明此时用户在后排说打开二排（因为前排说打开二排做了兜底拦截），此时需要根据音区判断是左侧还是右侧开着
                        ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
                        int curPosition = curSoundPositions.get(0);
                        if((curPosition ==2 ||curPosition ==4)&&(stateLeft ==3)) {//证明是左边音区  并且左边的滑移门开启
                            isClosed =true;
                            map.put("left_right","左侧");
                        }
                        if((curPosition ==3 ||curPosition ==5)&&(stateRight ==3)){//证明是右边音区   并且右边的滑移门开启
                            isClosed =true;
                            map.put("left_right","右侧");
                        }
                    }
                    break;
                default://其他位置
                    break;
            }

        }else{//根据音区判断
            ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
            int curPosition = curSoundPositions.get(0);
            if(curPosition ==0 ||curPosition ==2 || curPosition ==4 ){//如果是左边
//                int stateRl = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_RLDOOR_ST);
                int stateRl = operator.getIntProp(DoorSignal.DOOR_IS_OPEN_LEFT);
                if(stateRl == 3){
                    isClosed =true;
                    map.put("left_right","左侧");
                }
            }else{//如果是右边
//                int stateRr = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_RRDOOR_ST);
                int stateRr = operator.getIntProp(DoorSignal.DOOR_IS_OPEN_RIGHT);
                if(stateRr == 3){
                    isClosed =true;
                    map.put("left_right","右侧");
                }
            }
        }
        LogUtils.e(TAG,"----isCloseScratch----是否已开启------===end===============");
        return isClosed;
    }

    /**
     * 滑移门是否是开启中   默认不是开启中
     * @return
     */
    public boolean isOpeningScratch(HashMap<String, Object> map){
        LogUtils.e(TAG,"----isOpeningScratch------开启中----===start===============");
        boolean isOpen = false;
        String positions = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(positions)) {
            switch (positions){
                case "first_row_left"://主驾
                case "second_row_left"://二排左
                case "left_side":
                case "rear_side_left"://左后
                case "third_row_left"://三排左
//                    int runRl = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_DOORWORKST_RSP);
                    int runRl = operator.getIntProp(DoorSignal.DOOR_OPENING_LEFT);
                    if(runRl == 2){//代表左侧滑移门在开启中
                        isOpen =true;
                        map.put("left_right","左侧");
                    }
                    break;
                case "first_row_right"://副驾
                case "second_row_right"://二排右
                case "right_side":
                case "rear_side_right"://右后
                case "third_row_right":
//                    int runRr = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_DOORWORKST_RSP);
                    int runRr = operator.getIntProp(DoorSignal.DOOR_OPENING_RIGHT);
                    if(runRr == 2){//代表左侧滑移门在开启中
                        isOpen =true;
                        map.put("left_right","右侧");
                    }
                    break;
                case "second_side":
                    String switchType = getOneMapValue("switch_type", map);
                    LogUtils.e(TAG,"----isOpeningScratch------second_side----"+switchType+"===switchType===============");
                    ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
                    int curPosition = curSoundPositions.get(0);
                    if(curPosition ==0 ||curPosition ==2 || curPosition ==4 ){//如果是左边
//                        int runLeft = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_DOORWORKST_RSP);
                        int runLeft = operator.getIntProp(DoorSignal.DOOR_OPENING_LEFT);
                        if(runLeft == 2){//代表左侧滑移门在开启中
                            if(switchType!=null && switchType.equals("open")){//由于打开二排的逻辑不同，所以这里特地对打开的进行处理
                                isOpen =true;
                                map.put("left_right","左侧");
                            }
                        }
                    }else{//如果是右边
//                        int runRight = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_DOORWORKST_RSP);
                        int runRight = operator.getIntProp(DoorSignal.DOOR_OPENING_RIGHT);
                        if(runRight == 2){//代表左侧滑移门在开启中
                            if(switchType!=null && switchType.equals("open")){//由于打开二排的逻辑不同，所以这里特地对打开的进行处理
                                isOpen =true;
                                map.put("left_right","右侧");
                            }
                        }
                    }

                    break;
                default://其他位置
                    break;
            }

        }else{//根据音区判断
            ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
            int curPosition = curSoundPositions.get(0);
            if(curPosition ==0 ||curPosition ==2 || curPosition ==4 ){//如果是左边
//                int runRl = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_DOORWORKST_RSP);
                int runRl = operator.getIntProp(DoorSignal.DOOR_OPENING_LEFT);
                if(runRl == 2){//代表左侧滑移门在开启中
                    isOpen =true;
                    map.put("left_right","左侧");
                }
            }else{//如果是右边
//                int runRr = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_DOORWORKST_RSP);
                int runRr = operator.getIntProp(DoorSignal.DOOR_OPENING_RIGHT);
                if(runRr == 2){//代表左侧滑移门在开启中
                    isOpen =true;
                    map.put("left_right","右侧");
                }
            }
        }
        LogUtils.e(TAG,"----isOpeningScratch------开启中----"+isOpen+"===end===============");
        return isOpen;
    }

    /**
     * 滑移门是否是关闭中   默认不是关闭中
     * @return
     */
    public boolean isCloseingScratch(HashMap<String, Object> map){
        LogUtils.e(TAG,"----isCloseingScratch------关闭中----===start===============");
        boolean isClose = false;
        String positions = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(positions)) {
            switch (positions){
                case "first_row_left"://主驾
                case "second_row_left"://二排左
                case "left_side":
                case "rear_side_left"://左后
                case "third_row_left"://三排左
//                    int runRl = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_DOORWORKST_RSP);
                    int runRl = operator.getIntProp(DoorSignal.DOOR_OPENING_LEFT);
                    if(runRl == 1){//代表左侧滑移门在关闭中
                        isClose =true;
                        map.put("left_right","左侧");
                    }
                    break;
                case "first_row_right"://副驾
                case "second_row_right"://二排右
                case "right_side":
                case "rear_side_right"://右后
                case "third_row_right":
//                    int runRr = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_DOORWORKST_RSP);
                    int runRr =operator.getIntProp(DoorSignal.DOOR_OPENING_RIGHT);
                    if(runRr == 1){//代表左侧滑移门在关闭中
                        isClose =true;
                        map.put("left_right","右侧");
                    }
                    break;
                case "second_side":
                    String switchType = getOneMapValue("switch_type", map);
                    LogUtils.e(TAG,"----isCloseingScratch------second_side----"+switchType+"===switchType===============");
                    ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
                    int curPosition = curSoundPositions.get(0);
                    if(curPosition ==0 ||curPosition ==2 || curPosition ==4 ){//如果是左边
//                        int runLeft = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_DOORWORKST_RSP);
                        int runLeft = operator.getIntProp(DoorSignal.DOOR_OPENING_LEFT);
                        if(runLeft == 1){//代表左侧滑移门在关闭中
                            if(switchType!=null && switchType.equals("open")){//由于打开二排的逻辑不同，所以这里特地对打开的进行处理
                                isClose =true;
                                map.put("left_right","左侧");
                            }
                        }
                    }else{//如果是右边
//                        int runRight = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_DOORWORKST_RSP);
                        int runRight = operator.getIntProp(DoorSignal.DOOR_OPENING_RIGHT);
                        if(runRight == 1){//代表左侧滑移门在关闭中
                            if(switchType!=null && switchType.equals("open")){//由于打开二排的逻辑不同，所以这里特地对打开的进行处理
                                isClose =true;
                                map.put("left_right","右侧");
                            }
                        }
                    }
                    break;
                default://其他位置
                    break;
            }

        }else{//根据音区判断
            ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
            int curPosition = curSoundPositions.get(0);
            if(curPosition ==0 ||curPosition ==2 || curPosition ==4 ){//如果是左边
//                int runRl = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_DOORWORKST_RSP);
                int runRl = operator.getIntProp(DoorSignal.DOOR_OPENING_LEFT);
                if(runRl == 1){//代表左侧滑移门在关闭中
                    isClose =true;
                    map.put("left_right","左侧");
                }
            }else{//如果是右边
//                int runRr = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_DOORWORKST_RSP);
                int runRr = operator.getIntProp(DoorSignal.DOOR_OPENING_RIGHT);
                if(runRr == 1){//代表左侧滑移门在关闭中
                    isClose =true;
                    map.put("left_right","右侧");
                }
            }

        }
        LogUtils.e(TAG,"----isCloseingScratch------关闭中----"+isClose+"===end===============");
        return isClose;
    }


    //------------------以下为关闭滑移门--------------------

    /**
     * 判断哪个滑移门没有关闭就执行关闭
     */
    public void setScratchToClose(HashMap<String, Object> map){
        LogUtils.e(TAG,"----setScratchToClose------查询哪个滑移门没有关闭----===start===============");
//        int stateRl = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_RLDOOR_ST);
//        int stateRr = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_RRDOOR_ST);
//        int runRl = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_DOORWORKST_RSP);
//        int runRr = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_DOORWORKST_RSP);

        int stateRl = operator.getIntProp(DoorSignal.DOOR_IS_OPEN_LEFT);
        int stateRr = operator.getIntProp(DoorSignal.DOOR_IS_OPEN_RIGHT);
        int runRl = operator.getIntProp(DoorSignal.DOOR_OPENING_LEFT);
        int runRr = operator.getIntProp(DoorSignal.DOOR_OPENING_RIGHT);
        if (stateRl == 3 || stateRl ==2  ) {
//            carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RLDOORCTL_CMD, 1);//1 是关，2是开
            operator.setIntProp(DoorSignal.DOOR_SWITCH_LEFT, 1);//1 是关，2是开
            map.put("left_right","左侧");
        }
        if(stateRr == 3 || stateRr ==2 ){
//            carPropHelper.setIntProp(H56C.IVI_BodySet2_IVI_RRDOORCTL_CMD, 1);//1 是关，2是开
            operator.setIntProp(DoorSignal.DOOR_SWITCH_RIGHT, 1);//1 是关，2是开
            map.put("left_right","右侧");
        }
        LogUtils.e(TAG,"----setScratchToClose------查询哪个滑移门没有关闭----===end===============");
    }

    /**
     * 判断滑移门是否已经关闭
     */
    public boolean isClosed(HashMap<String, Object> map){
        boolean isClose = false;
        String positions = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(positions)) {
            switch (positions){
                case "first_row_left"://主驾
                case "second_row_left"://二排左
                case "left_side":
                case "rear_side_left"://左后
                case "third_row_left"://三排左
//                    int stateRl = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_RLDOOR_ST);
                    int stateRl = operator.getIntProp(DoorSignal.DOOR_IS_OPEN_LEFT);
                    if(stateRl == 0){//0代表已关闭
                        isClose =true;
                        map.put("left_right","左侧");
                    }
                    break;
                case "first_row_right"://副驾
                case "second_row_right"://二排右
                case "right_side":
                case "rear_side_right"://右后
                case "third_row_right":
//                    int stateRr = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_RRDOOR_ST);
                    int stateRr = operator.getIntProp(DoorSignal.DOOR_IS_OPEN_RIGHT);
                    if(stateRr == 0){//0代表已关闭
                        isClose =true;
                        map.put("left_right","右侧");
                    }
                    break;
                case "second_side"://二排
//                    int stateLeft = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_RLDOOR_ST);
//                    int stateRight = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_RRDOOR_ST);
                    int stateLeft = operator.getIntProp(DoorSignal.DOOR_IS_OPEN_LEFT);
                    int stateRight = operator.getIntProp(DoorSignal.DOOR_IS_OPEN_RIGHT);
                    if(stateLeft == 0 && stateRight ==0){//0代表已关闭
                        isClose =true;
                        map.put("left_right","二排");
                    }
                    break;
                default://其他位置
                    break;
            }
        }else{//根据音区判断
            ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
            int curPosition = curSoundPositions.get(0);
            if(curPosition ==0 ||curPosition ==2 || curPosition ==4 ){//如果是左边
//                int stateRl = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_RLDOOR_ST);
                int stateRl = operator.getIntProp(DoorSignal.DOOR_IS_OPEN_LEFT);
                if(stateRl == 0){//0代表已关闭
                    isClose =true;
                    map.put("left_right","左侧");
                }
            }else{//如果是右边
//                int stateRr = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_RRDOOR_ST);
                int stateRr =operator.getIntProp(DoorSignal.DOOR_IS_OPEN_RIGHT);
                if(stateRr == 0){//0代表已关闭
                    isClose =true;
                    map.put("left_right","右侧");
                }
            }
        }
        return isClose;
    }

    /**
     * 判断两个滑移门中是否有一个没有关闭
     */
    public  boolean isClosedTwoDoor(HashMap<String, Object> map){
        boolean isCloseDoor = false;
//        int stateLeft = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_RLDOOR_ST);
//        int stateRight = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_RRDOOR_ST);
        int stateLeft = operator.getIntProp(DoorSignal.DOOR_IS_OPEN_LEFT);
        int stateRight = operator.getIntProp(DoorSignal.DOOR_IS_OPEN_RIGHT);
        if(stateLeft == 0 && stateRight ==0){//0代表已关闭   代表两个门都已关闭
            isCloseDoor =true;
            map.put("left_right","二排");
        }
        return isCloseDoor;
    }

    /**---------------滑移门控制方式（手动，电动）--------start------------------------*/
    //判断用户说的是 电动还是手动的控制方式  false 手动  true电动
    public boolean isMaintenanceAdjust(HashMap<String, Object> map) {
        LogUtils.e(TAG,"----isMaintenanceAdjust------滑移门控制方式----===start===============");
        boolean isMaintenanceCar = false;
        String switch_mode = (String) getValueInContext(map, "switch_mode");
        if (switch_mode == null) {
            return isMaintenanceCar;
        }
        if(switch_mode.contains("electric")){
            isMaintenanceCar =true;
        }
        LogUtils.e(TAG,"----isMaintenanceAdjust------滑移门控制方式----===end===============");

        return isMaintenanceCar;
    }

    //判断当前是否已经是电动调节滑移门  true 已经开启电动调节  false 还未开启电动调节
    public boolean isElectric(HashMap<String, Object> map){
        LogUtils.e(TAG,"----isElectric----------===start===============");
        boolean isElectric = true;
//        int stateSwRl = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_PSDENADBLESTA);
//        int stateSwRr = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_PSDENADBLESTA);
        int stateSwRl = operator.getIntProp(DoorSignal.DOOR_ELECTRIC_SWITCH_IS_OPEN_LEFT);
        int stateSwRr = operator.getIntProp(DoorSignal.DOOR_ELECTRIC_SWITCH_IS_OPEN_RIGHT);
        if (stateSwRl == 2 && stateSwRr == 2) {
            isElectric = false;
        }
        map.put("sliding_door_mode","电动");
        LogUtils.e(TAG,"----isElectric---------===start===============");
        return isElectric;
    }


    //设置成电动调节滑移门
    public void doingElectric(HashMap<String, Object> map){
        map.put("sliding_door_mode","电动");
//        carPropHelper.setIntProp(H56C.IVI_BodySet3_IVI_PSDENADBLESET, 1);//1是打开，2是关闭
        operator.setIntProp(DoorSignal.DOOR_ELECTRIC_SWITCH, 1);//1是打开，2是关闭
        LogUtils.e(TAG,"----doingElectric---------===end===============");
    }

    //判断当前是否已经是手动滑移门  true 已经是手动滑移门  false 是电动滑移门
    public boolean isNotElectric(HashMap<String, Object> map){
        LogUtils.e(TAG,"----isNotElectric----------===start===============");
        boolean isElectric = true;
//        int stateSwRl = carPropHelper.getIntProp(H56C.PSD_L_PSD_L_PSDENADBLESTA);
//        int stateSwRr = carPropHelper.getIntProp(H56C.PSD_R_PSD_R_PSDENADBLESTA);
        int stateSwRl = operator.getIntProp(DoorSignal.DOOR_ELECTRIC_SWITCH_IS_OPEN_LEFT);
        int stateSwRr = operator.getIntProp(DoorSignal.DOOR_ELECTRIC_SWITCH_IS_OPEN_RIGHT);
        if (stateSwRl == 1 && stateSwRr == 1) {//等于1 代表滑移门电动开关已打开
            isElectric = false;
        }
        map.put("sliding_door_mode","手动");
        LogUtils.e(TAG,"----isNotElectric----------===start===============");
        return isElectric;
    }

    //设置成手动调节滑移门
    public void doingNotElectric(HashMap<String, Object> map){
        map.put("sliding_door_mode","手动");
//        carPropHelper.setIntProp(H56C.IVI_BodySet3_IVI_PSDENADBLESET, 2);//1是打开，2是关闭 ,即调成手动
        operator.setIntProp(DoorSignal.DOOR_ELECTRIC_SWITCH, 2);//1是打开，2是关闭 ,即调成手动
        LogUtils.e(TAG,"----doingNotElectric----------===end===============");
    }

    /**---------------滑移门控制方式（手动，电动）--------end---------------------------*/

    /**---------------电吸门功能--------start---------------------------*/
    //电吸门功能  H37B特有
    //获取电吸门开关状态
    public boolean isOpenElectricSuction(HashMap<String, Object> map){
        boolean isOpen = false;//默认开关是关闭的
        int stataValue = 0 ;
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        if ("H37B".equals(carType)) {
            stataValue = operator.getIntProp(DoorSignal.DOOR_ELECTRIC_SUCTION);
        }else{//H56D
            stataValue = operator.getIntProp(DoorSignal.DOOR_ELECTRIC_SUCTION_GET);

        }
        if(stataValue == 2){//2代表开关是打开的
            isOpen = true;
        }
        LogUtils.d(TAG,"-----isOpenElectricSuction---------stataValue======"+stataValue+"-----");

        return isOpen;
    }
    //打开电吸门开关
    public void setElectricOpen(HashMap<String, Object> map){
        LogUtils.d(TAG,"---setElectricOpen--------======open------------------");

        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        if ("H37B".equals(carType)) {
            operator.setIntProp(DoorSignal.DOOR_ELECTRIC_SUCTION, 2);
        }else{//H56D
            operator.setIntProp(DoorSignal.DOOR_ELECTRIC_SUCTION_SET, 2);
        }
    }
    //关闭电吸门开关
    public void setElectricClose(HashMap<String, Object> map){
        LogUtils.d(TAG,"---setElectricOpen--------======open------------------");
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        if ("H37B".equals(carType)) {
            operator.setIntProp(DoorSignal.DOOR_ELECTRIC_SUCTION, 1);
        }else{//H56D
            operator.setIntProp(DoorSignal.DOOR_ELECTRIC_SUCTION_SET, 1);
        }
    }

    //只有H37B和H56D有电吸功能
    public boolean isHaveElectricDoor(HashMap<String, Object> map){
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        if ("H56D".equals(carType) || "H37B".equals(carType)) {
            return true;
        }else{//H56C H37A
            return false;
        }
    }

    /**---------------电吸门功能--------end------------------------------*/

}
