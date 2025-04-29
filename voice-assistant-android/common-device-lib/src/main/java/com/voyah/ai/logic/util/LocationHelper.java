package com.voyah.ai.logic.util;

import android.text.TextUtils;

import com.voice.sdk.device.DeviceHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocationHelper {

    private static Set<Integer> fourSeat = new HashSet<>();
    private static Set<Integer> sixSeat = new HashSet<>();
    private boolean curISFourSeat = true;
    private int positionSize;

    public LocationHelper() {

        String str = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        if (str.equals("H56C") || str.equals("H56D")) {
            //6座位
            positionSize = 6;
        } else {
            //4座位
            positionSize = 4;
        }

    }

    static {
        fourSeat.add(0);//主驾
        fourSeat.add(1);//副驾
        fourSeat.add(2);//左后
        fourSeat.add(3);//右后

        sixSeat.add(0);//主驾
        sixSeat.add(1);//副驾
        sixSeat.add(2);//二排左
        sixSeat.add(3);//二排右
        sixSeat.add(4);//三排左
        sixSeat.add(5);//三排右
    }

    /**
     * 主驾/左前/一排左	position	first_row_left
     * 副驾/右前/一排右	position	first_row_right
     * 二排左/左后/后排左	position	second_row_left
     * 二排右/右后/后排右	position	second_row_right
     * 二排中/后排中	position	second_row_mid
     * 三排左	position	third_row_left
     * 三排右	position	third_row_right
     * 前排/一排	position	first_row_side
     * 后排/二排	position	second_row_side
     * 三排	position	third_row_side
     * 中间	position	middle_side
     * 左侧	position	left_side
     * 右侧	position	right_side
     * 全部/全车	position	total_car
     * <p>
     * 需要获取当前车是多少排的车，这个逻辑后续再加。
     * <p>
     * 把主驾，副驾，前排，后排，全都转换成0，1，2，3的信息
     *
     * @param set
     * @param position
     */
    public void convertLocationInformation(Set<Integer> set, String position) {
        if (TextUtils.isEmpty(position)) {
            return;
        }
        String[] array = position.split(",");
        for (int i = 0; i < array.length; i++) {
            //需要根据车型去判断当前位置信息是怎样的。
            switch (array[i]) {
                case "first_row_left"://主驾/左前/一排左
                    set.add(0);
                    break;
                case "first_row_right"://副驾/右前/一排右
                    set.add(1);
                    break;
                case "second_row_left"://二排左/左后/后排左
                    set.add(2);
                    break;
                case "second_row_right"://二排右/右后/后排右
                    set.add(3);
                    break;
                case "second_row_mid"://二排中/后排中
                case "third_row_left"://三排左
                case "third_row_right"://三排右
                    break;
                case "first_row_side"://前排/一排
                    set.add(0);
                    set.add(1);
                    break;
                case "second_row_side"://后排/二排
                    set.add(2);
                    set.add(3);
                    break;
                case "third_row_side"://三排
                case "middle_side"://中间
                    break;
                case "left_side"://左侧
                    set.add(0);
                    set.add(2);
                    break;
                case "right_side"://右侧
                    set.add(1);
                    set.add(3);
                    break;
                case "total_car"://全部/全车
                    set.addAll(fourSeat);
                    break;
                case "front_left"://全部/全车
                    set.add(0);
                    break;
            }
        }
    }

    public List<Integer> convertLocationInformation(String position) {
        List<Integer> list = new ArrayList<>();
        if (TextUtils.isEmpty(position)) {
            return list;
        }
        String str = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        String[] array = position.split(",");
        for (int i = 0; i < array.length; i++) {
            //需要根据车型去判断当前位置信息是怎样的。
            switch (array[i]) {
                case "first_row_left"://主驾/左前/一排左
                    list.add(0);
                    break;
                case "first_row_right"://副驾/右前/一排右
                    list.add(1);
                    break;
                case "second_row_left"://二排左/左后/后排左
                case "rear_side_left"://二排左/左后/后排左
                    list.add(2);
                    break;
                case "second_row_right"://二排右/右后/后排右
                case "rear_side_right"://二排右/右后/后排右
                    list.add(3);
                    break;
                case "second_row_mid"://二排中/后排中
                    break;
                case "third_row_left"://三排左
                    list.add(4);
                    break;
                case "third_row_right"://三排右
                    list.add(5);
                    break;
                case "first_row_side"://前排/一排
                    list.add(0);
                    list.add(1);
                    break;
                case "middle_side"://中间
                case "second_row_side"://后排/二排
                    list.add(2);
                    list.add(3);
                    break;
                case "third_row_side"://三排
                    list.add(4);
                    list.add(5);
                    break;
                case "left_side"://左侧
                    if (positionSize == 4) {
                        list.add(0);
                        list.add(2);
                    } else {
                        list.add(0);
                        list.add(2);
                        list.add(4);
                    }
                    break;
                case "right_side"://右侧
                    if (positionSize == 4) {
                        list.add(1);
                        list.add(3);
                    } else {
                        list.add(1);
                        list.add(3);
                        list.add(5);
                    }
                    break;
                case "total_car"://全部/全车
                    if (str.equals("H56C") || str.equals("H56D")) {
                        list.addAll(sixSeat);
                    } else {
                        list.addAll(fourSeat);
                    }
                    break;
                case "front_left"://全部/全车
                    list.add(0);
                    break;
            }
        }
        return list;
    }
}
