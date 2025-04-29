package com.voyah.ai.basecar.viewcmd.accessibility;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractTF<T> {

    public final T mCheckData;

    private AbstractTF(@NonNull T checkData) {
        mCheckData = checkData;
    }

    public abstract boolean checkOk(AccessibilityNodeInfo thisInfo);

    /**
     * 找id，就是findAccessibilityNodeInfosByViewId方法
     * 和找text一样效率最高，如果能找到，尽量使用这个
     */
    private static class IdTF extends AbstractTF<String> implements IdTextTF {
        private IdTF(@NonNull String idFullName) {
            super(idFullName);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo thisInfo) {
            return true;
        }

        @Nullable
        @Override
        public AccessibilityNodeInfo findFirst(AccessibilityNodeInfo root) {
            List<AccessibilityNodeInfo> list = root.findAccessibilityNodeInfosByViewId(mCheckData);
            if (isEmptyArray(list)) {
                return null;
            }
            for (int i = 1; i < list.size(); i++) {
                list.get(i).recycle();
            }
            return list.get(0);
        }

        @Nullable
        @Override
        public List<AccessibilityNodeInfo> findAll(AccessibilityNodeInfo root) {
            return root.findAccessibilityNodeInfosByViewId(mCheckData);
        }
    }

    /**
     * 普通text，就是findAccessibilityNodeInfosByText方法
     * 和找id一样效率最高，如果能找到，尽量使用这个
     */
    private static class TextTF extends AbstractTF<String> implements IdTextTF {
        private TextTF(@NonNull String text) {
            super(text);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo thisInfo) {
            return true;
        }

        @Nullable
        @Override
        public AccessibilityNodeInfo findFirst(AccessibilityNodeInfo root) {
            List<AccessibilityNodeInfo> list = root.findAccessibilityNodeInfosByText(mCheckData);
            if (isEmptyArray(list)) {
                return null;
            }
            AccessibilityNodeInfo returnInfo = null;
            for (AccessibilityNodeInfo info : list) {
                if (info.getText() != null && mCheckData.equals(info.getText().toString()) && info.isVisibleToUser()) {
                    returnInfo = info;
                } else {
                    info.recycle();
                }
            }
            return returnInfo;
        }

        @Nullable
        @Override
        public List<AccessibilityNodeInfo> findAll(AccessibilityNodeInfo root) {
            List<AccessibilityNodeInfo> list = root.findAccessibilityNodeInfosByText(mCheckData);
            if (isEmptyArray(list)) {
                return null;
            }
            ArrayList<AccessibilityNodeInfo> listNew = new ArrayList<>();
            for (AccessibilityNodeInfo info : list) {
                if (info.getText() != null && mCheckData.equals(info.getText().toString()) && info.isVisibleToUser()) {
                    listNew.add(info);
                } else {
                    info.recycle();
                }
            }
            return listNew;
        }
    }

    /**
     * 类似uc浏览器，有text值但无法直接根据text来找到
     */
    private static class WebTextTF extends AbstractTF<String> {
        private WebTextTF(@NonNull String checkString) {
            super(checkString);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo info) {
            CharSequence text = info.getText();
            return text != null && text.toString().equals(mCheckData) && info.isVisibleToUser();
        }
    }

    /**
     * 找ContentDescription字段
     */
    private static class ContentDescriptionTF extends AbstractTF<String> {
        private ContentDescriptionTF(@NonNull String checkString) {
            super(checkString);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo thisInfo) {
            CharSequence text = thisInfo.getContentDescription();
            return text != null && text.toString().equals(mCheckData);
        }
    }

    /**
     * 找ClassName匹配
     */
    private static class ClassNameTF extends AbstractTF<String> {
        public ClassNameTF(@NonNull String checkString) {
            super(checkString);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo thisInfo) {
            return thisInfo.getClassName().toString().equals(mCheckData);
        }
    }

    /**
     * 找PackageName匹配
     */
    private static class PackageNameTF extends AbstractTF<String> {
        public PackageNameTF(@NonNull String checkString) {
            super(checkString);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo thisInfo) {
            return thisInfo.getPackageName().toString().equals(mCheckData);
        }
    }

    /**
     * 桌面Widget控件
     */
    private static class WidgetTextTF extends AbstractTF<String> {
        private WidgetTextTF(@NonNull String checkString) {
            super(checkString);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo info) {
            CharSequence text = info.getText();
            return text != null && text.toString().equals(mCheckData) && info.isVisibleToUser();
        }
    }

    /**
     * 在某个区域内的控件
     */
    private static class RectTF extends AbstractTF<Rect> {
        public RectTF(@NonNull Rect rect) {
            super(rect);
        }

        @Override
        public boolean checkOk(AccessibilityNodeInfo thisInfo) {
            thisInfo.getBoundsInScreen(mRecycleRect);
            return mCheckData.contains(mRecycleRect);
        }
    }

    public interface IdTextTF {
        @Nullable
        AccessibilityNodeInfo findFirst(AccessibilityNodeInfo root);

        @Nullable
        List<AccessibilityNodeInfo> findAll(AccessibilityNodeInfo root);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 创建方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Rect mRecycleRect = new Rect();


    /**
     * @param idfullName id全称:com.android.xxx:id/tv_main
     */
    public static AbstractTF<?> newId(@NonNull String idfullName) {
        return new IdTF(idfullName);
    }

    /**
     * 普通text，就是findAccessibilityNodeInfosByText方法
     * 和找id一样效率最高，如果能找到，尽量使用这个
     */
    public static AbstractTF<?> newText(@NonNull String text) {
        return new TextTF(text);
    }

    /**
     * 类似uc浏览器，有text值但无法直接根据text来找到
     */
    public static AbstractTF<?> newWebText(@NonNull String webText) {
        return new WebTextTF(webText);
    }

    /**
     * 找ContentDescription字段
     */
    public static AbstractTF<?> newContentDescription(@NonNull String cd) {
        return new ContentDescriptionTF(cd);
    }

    /**
     * 找ClassName匹配
     */
    public static AbstractTF<?> newClassName(@NonNull String className) {
        return new ClassNameTF(className);
    }

    /**
     * 找PackageName匹配
     */
    public static AbstractTF<?> newPackageName(@NonNull String packageName) {
        return new PackageNameTF(packageName);
    }

    public static AbstractTF<?> newWidgetText(@NonNull String webText) {
        return new WidgetTextTF(webText);
    }
    /**
     * 在某个区域内的控件
     */
    public static AbstractTF<?> newRect(@NonNull Rect rect) {
        return new RectTF(rect);
    }

    private static boolean isEmptyArray(Collection<?> list) {
        return list == null || list.size() == 0;
    }
}