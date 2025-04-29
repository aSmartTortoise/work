package com.voyah.viewcmd;

import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class VcosViewUtil {

    /**
     * 岚图公共组件
     */
    public static final String VCOS_TAB = "com.vcos.common.widgets.button.VcosTab";
    public static final String VCOS_TAB_HORIZONTAL = "com.vcos.common.widgets.button.VcosTabHorizontal";
    public static final String VCOS_TAB_VERTICAL = "com.vcos.common.widgets.button.VcosTabVertical";
    public static final String VCOS_SWITCH = "com.vcos.common.widgets.vcosswitch.VcosSwitch";
    public static final String VCOS_SELECTOR_VIEW = "com.vcos.common.widgets.vcosselectview.VcosSelectorView";

    public static final List<String> VCOS_ITEM_VIEW_LIST = Arrays.asList(
            "com.vcos.common.widgets.vcositemview.VcosItemView",
            "com.vcos.common.widgets.vcositemview.VcosSwitchItemView"
    );
    public static final List<String> VCOS_BUTTON_LIST = Arrays.asList(
            "com.vcos.common.widgets.button.VcosButton",
            "com.vcos.common.widgets.button.VcosTextButton",
            "com.vcos.common.widgets.button.VcosIconButton",
            "com.vcos.common.widgets.button.VcosCardButton",
            "com.vcos.common.widgets.button.VcosGhostButton"
    );


    /**
     * 判断VCOS_Switch是否checked
     */
    public static boolean isVCOSSwitchChecked(View vcosSwitch) throws Exception {
        Class<?> switchClass = vcosSwitch.getClass();
        Method isCheckedMethod = switchClass.getDeclaredMethod("isChecked");
        return (boolean) isCheckedMethod.invoke(vcosSwitch);
    }

    /**
     * toggle接口
     */
    public static void toggleVCOSSwitchForVoice(View vcosSwitch) {
        try {
            Class<?> vcosSwitchClass = vcosSwitch.getClass();
            Method setCheckedMethod = vcosSwitchClass.getDeclaredMethod("setCheckedForVoice", boolean.class);
            setCheckedMethod.setAccessible(true);
            boolean isChecked = isVCOSSwitchChecked(vcosSwitch);
            setCheckedMethod.invoke(vcosSwitch, !isChecked);
            // 显示动画
            Method startAnimMethod = vcosSwitchClass.getDeclaredMethod("startAnim");
            startAnimMethod.setAccessible(true);
            startAnimMethod.invoke(vcosSwitch);
        } catch (Exception e) {
            e.printStackTrace();
            toggleVCOSSwitch(vcosSwitch);
        }
    }

    public static void toggleVCOSSwitch(View vcosSwitch) {
        try {
            Class<?> vcosSwitchClass = vcosSwitch.getClass();
            Method setCheckedMethod = vcosSwitchClass.getDeclaredMethod("setChecked", boolean.class);
            setCheckedMethod.setAccessible(true);
            boolean isChecked = isVCOSSwitchChecked(vcosSwitch);
            setCheckedMethod.invoke(vcosSwitch, !isChecked);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过反射获取VcosSelectorView的mText成员变量值
     */
    public static String getVCOSSelectorViewText(View vcosSelectorView) {
        try {
            Class<?> clazz = vcosSelectorView.getClass();
            Field mTextField = clazz.getDeclaredField("mText");
            mTextField.setAccessible(true);
            return (String) mTextField.get(vcosSelectorView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过反射获取VcosButton的mText成员变量值
     */
    public static String getVCOSButtonText(View vcosButton) {
        try {
            Class<?> clazz = vcosButton.getClass();
            Field mTextField = clazz.getDeclaredField("mContent");
            mTextField.setAccessible(true);
            return (String) mTextField.get(vcosButton);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
