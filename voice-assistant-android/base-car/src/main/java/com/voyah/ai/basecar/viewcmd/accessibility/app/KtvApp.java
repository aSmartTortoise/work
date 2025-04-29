package com.voyah.ai.basecar.viewcmd.accessibility.app;

import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KtvApp extends BaseApp {
    public static final String PACKAGE_NAME = "com.thunder.carplay";
    private static final String REGEX_SPECIAL_CHARS = "[^\\u4E00-\\u9FA50-9a-zA-Z.]";
    private static final String REGEX_SPLIT_TAB = "[\\u4E00-\\u9FA5]+｜[\\u4E00-\\u9FA5]+"; //一单打进｜好歌放送
    private static final String REGEX_FIX_TAILS = "(HD|MV|BHD)$";
    private static final List<String> SearchIconAddress = Arrays.asList("e1c008c681af9e2da693cdc12edd8347",
            "e24e8b0fd8b2ba5f24df9b86ba0bc9ef");
    private static final List<String> ReturnIconAddress = Arrays.asList("72ff0b114ee2cb3153ce901af19bc813",
            "7691c0a91bc7f951374f15c175f8d69a");

    @Override
    public Map<String, String> handle(String topApp, List<String> list, List<String> viewIds) {
        fullNameMap.clear();
        addGestureUpDown();
        removePureDigits(list);
        buildTextMap(list);
        return fullNameMap;
    }


    private void buildTextMap(List<String> list) {
        List<String> newList = new ArrayList<>(list);
        boolean isHomePage = isHomePage(list);
        for (int i = 0; i < newList.size(); i++) {
            String text = newList.get(i);
            if (isHomePage && SearchIconAddress.contains(text)) {
                fullNameMap.put("搜索", createText(text));
                fullNameMap.put("搜索歌曲", createText(text));
                fullNameMap.put("搜索歌曲歌手", createText(text));
            } else if (!isHomePage) {
                if (ReturnIconAddress.contains(text)) {
                    String str = String.join(",", list);
                    if (!str.contains("已唱")) {
                        fullNameMap.put("返回", createText(text));
                    }
                }
                if (SearchIconAddress.contains(text)) {
                    fullNameMap.put("搜索歌曲", createText(text));
                    fullNameMap.put("搜索歌曲歌星", createText(text));
                }
            }

            Pattern splitTabRegex = Pattern.compile(REGEX_SPLIT_TAB);
            if (splitTabRegex.matcher(text).matches()) {
                String[] split = text.split("｜");
                for (String subText : split) {
                    if (!list.contains(subText) && !TextUtils.isEmpty(subText)) {
                        fullNameMap.put(subText, createText(text));
                    }
                }
            } else {
                String newText = text.replaceAll(REGEX_SPECIAL_CHARS, "").trim();
                Matcher matcher = Pattern.compile(REGEX_FIX_TAILS).matcher(newText);
                if (matcher.find()) {
                    newText = newText.replaceAll(REGEX_FIX_TAILS, "");
                    if (!list.contains(newText) && !TextUtils.isEmpty(newText) && !TextUtils.equals(text, newText)) {
                        fullNameMap.put(newText, createText(text));
                    }
                }
            }
        }
    }

    private boolean isHomePage(List<String> list) {
        return (list.contains("ec8cbde5d850fc7d200c65402e4db24f") || list.contains("587b94b627ce3bb928c3b16a26225698"))
                && !list.contains("搜索");
    }

    @Override
    public List<AccessibilityNodeInfo> selectStrategy(String text, @NonNull List<AccessibilityNodeInfo> list) {
        if ("已点".equals(text) && list.size() > 0) {
            return Collections.singletonList(list.get(1));
        }
        return super.selectStrategy(text, list);
    }

    @Override
    protected String packageName() {
        return PACKAGE_NAME;
    }

}
