package com.voyah.ai.basecar.viewcmd.strategy;

import static com.voice.sdk.constant.ApplicationConstant.INTENT_VIEWCMD;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ArrayUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.device.system.ScreenInterface;
import com.voice.sdk.device.viewcmd.AccessibleAbilityInterface;
import com.voice.sdk.device.viewcmd.ViewCmdCache;
import com.voyah.ai.basecar.manager.DialogueManager;
import com.voyah.ai.basecar.manager.FeedbackManager;
import com.voyah.ai.basecar.tts.BeanTtsManager;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.CommonSystemUtils;
import com.voyah.ai.basecar.R;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.common.utils.Arab2ChineseUtil;
import com.voice.sdk.device.viewcmd.ViewCmdResult;
import com.voyah.ai.sdk.bean.LifeState;
import com.voyah.ai.sdk.bean.NluResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractBaseScreenStrategy implements IScreenStrategy {

    /**
     * 可见即可说文本长度分词触发值
     */
    private static final int LENGTH_TEXT_BLUR_THRESH = 12;
    /**
     * 不开启分词prompt列表
     */
    private static final List<String> NO_BLUR_PROMPT_LIST = ArrayUtils.asArrayList("Switch", "Tab");
    private static final Pattern PATTERN_BRACKET = Pattern.compile("[（\\(](.*?)[）\\)]");
    private static final Pattern PATTERN_SHORT_CHARS = Pattern.compile("^[a-zA-Z0-9]{1,2}$");
    private static final Pattern PATTERN_LEFT_BRACKET = Pattern.compile("[（\\(]");
    private static final Pattern PATTERN_TIME = Pattern.compile("^(\\d+)(s|min)$");
    private static final Pattern PATTERN_KM = Pattern.compile(".*(\\d+)(km|m)$");
    private static final Pattern PATTERN_DISTANCE = Pattern.compile("(\\d{4})(米|公里)");
    private static final Pattern PATTERN_YEAR = Pattern.compile("\\d+(年|后)");
    private static final Pattern PATTERN_PLAY_SPEED = Pattern.compile("\\d\\.\\d{1,2}[xX]");
    private static final Pattern PATTERN_PERCENT = Pattern.compile("^(\\d+(\\.\\d+)?)%$");
    private static final Pattern PATTERN_TEMP = Pattern.compile("^(\\d+)℃$");

    final ViewCmdCache viewCmdCache = new ViewCmdCache();
    static final ViewCmdCache globalViewCmdCache = new ViewCmdCache();
    final ViewCmdCache kwsViewCmdCache = new ViewCmdCache();
    final Map<Integer, Map<String, String>> textMap = new ConcurrentHashMap<>();
    AccessibleAbilityInterface accessibleAbility;
    final Map<Integer, Map<String, String>> specialTextMap = new ConcurrentHashMap<>();
    final int mainDisplayId;
    final int ceilingDisplayId;

    AbstractBaseScreenStrategy() {
        this.mainDisplayId = MegaDisplayHelper.getMainScreenDisplayId();
        this.ceilingDisplayId = MegaDisplayHelper.getCeilingScreenDisplayId();
        this.accessibleAbility = DeviceHolder.INS().getDevices().getViewCmd().getAccessibleAbility();
        DialogueManager.get().registerStateCallback(state -> {
            if (LifeState.READY.equals(state)) {
                ViewCmdCache.Cache kwsCache = kwsViewCmdCache.getCache(ViewCmdCache.KWS_ID);
                if (kwsCache != null) {
                    FeedbackManager.get().uploadViewCmd(new ArrayList<>(), null, kwsViewCmdCache);
                }
            }
        });
    }

    public List<String> normalize(String pkg, int id, List<String> list) {
        if (list.size() == 0) {
            return list;
        }
        Map<String, String> textMap = new HashMap<>();
        List<String> retList = new ArrayList<>();
        boolean isCompatibleMode = isCompatibleMode(list.get(0));
        String version = FeedbackManager.get().getViewCmdVersion(pkg);
        for (int i = 0; i < list.size(); i++) {
            JSONObject object;
            String text = list.get(i);
            String prompt = null;
            if (isCompatibleMode) {
                object = new JSONObject();
            } else {
                object = JSON.parseObject(text);
                text = object.getString("text");
                prompt = object.getString("prompt");
            }
            if (TextUtils.isEmpty(text)) {
                continue;
            }
            String newText;
            if (isEnglishSentence(text)) {
                newText = text.trim().replaceAll("\\s+", " ").replaceAll("\\?", "");
            } else if (text.trim().matches("\\d+([-~:|+.])\\d+")) {
                newText = text.trim();
            } else {
                newText = text.trim().replaceAll("[^\\u4E00-\\u9FA50-9a-zA-Z.\\s]", "")
                        .replaceAll("\\s+", " ");
                if (newText.contains(" ") && noNeedSpace(newText)) {
                    newText = newText.replaceAll("\\s", "");
                }
            }
            int length = com.voyah.ai.common.utils.Utils.getStringLength(newText);
            if (length >= 40 || newText.length() == 0) {
                continue;
            }
            if (id != ViewCmdCache.KWS_ID && (prompt == null || !NO_BLUR_PROMPT_LIST.contains(prompt))) {
                if (length >= LENGTH_TEXT_BLUR_THRESH) {
                    newText = "^" + newText + "^";
                }
            }
            if (isSupportNluViewCmd() && "Switch".equals(prompt) && !"1.5".equals(version)) {
                newText = "&" + newText + "&";
            }

            // newText = Arab2ChineseUtil.convert(newText);

            if (!isCompatibleMode && prompt == null && id != ViewCmdCache.KWS_ID) {
                object.put("prompt", "Common");
            }
            object.put("text", newText);
            newText = object.toString();
            if (newText.length() > 0 && !retList.contains(newText)) {
                retList.add(newText);
                textMap.put(newText, text);
            }
        }
        this.textMap.put(id, textMap);
        return retList;
    }

    public List<String> specialPreHandle(int id, List<String> list) {
        if (list.size() == 0) {
            return list;
        }
        boolean isCompatibleMode = isCompatibleMode(list.get(0));
        List<String> originTexts = getOriginTexts(list, isCompatibleMode);
        List<String> retList = new ArrayList<>(list);
        Map<String, String> specialMap = specialTextMap.get(id);
        if (specialMap == null) {
            specialMap = new HashMap<>();
        } else {
            specialMap.clear();
        }
        for (int i = 0; i < list.size(); i++) {
            String text = list.get(i);
            String prompt = null;
            if (!isCompatibleMode) {
                JSONObject object = JSON.parseObject(text);
                text = object.getString("text");
                prompt = object.getString("prompt");
            }
            if (TextUtils.isEmpty(text)) {
                continue;
            }
            List<String> specialList = new ArrayList<>();
            String newText1 = extractBracketContent(text);
            if (!TextUtils.isEmpty(newText1)) {
                specialList.add(newText1);
            }

            String newText2 = removeBracketContent(text);
            if (!TextUtils.isEmpty(newText2)) {
                specialList.add(newText2);
            }

            String newText3 = convertTimeFormat(text);
            if (!TextUtils.isEmpty(newText3)) {
                specialList.add(newText3);
            }

            String newText4 = genericFixedWord(text);
            if (!TextUtils.isEmpty(newText4)) {
                specialList.add(newText4);
            }

            String newText5 = extractFirstSplitContent(text);
            if (!TextUtils.isEmpty(newText5)) {
                specialList.add(newText5);
            }

            String newText6 = convertYearFormat(text);
            if (!TextUtils.isEmpty(newText6)) {
                specialList.add(newText6);
            }

            List<String> newText7 = convertPlaySpeed(text);
            if (newText7 != null && newText7.size() > 0) {
                specialList.addAll(newText7);
            }

            List<String> newText8 = convertKmFormat(text);
            if (newText8 != null && newText8.size() > 0) {
                specialList.addAll(newText8);
            }

            String newText9 = convertDistanceFormat(text);
            if (!TextUtils.isEmpty(newText9)) {
                specialList.add(newText9);
            }

            String newText10 = convertPercentFormat(text);
            if (!TextUtils.isEmpty(newText10)) {
                specialList.add(newText10);
            }

            List<String> newText11 = convertTempFormat(text);
            if (newText11 != null && newText11.size() > 0) {
                specialList.addAll(newText11);
            }
            for (String specialText : specialList) {
                boolean bAdd = false;
                if (isCompatibleMode) {
                    if (!retList.contains(specialText)) {
                        retList.add(specialText);
                        bAdd = true;
                    }
                } else {
                    JSONObject newObject = new JSONObject();
                    newObject.put("prompt", prompt);
                    newObject.put("text", specialText);
                    if (!retList.contains(newObject.toString()) && !originTexts.contains(specialText)) {
                        retList.add(newObject.toString());
                        bAdd = true;
                    }
                }
                if (bAdd && !specialMap.containsKey(specialText)) {
                    specialMap.put(specialText, text);
                }
            }
        }
        specialTextMap.put(id, specialMap);
        return retList;
    }

    private static boolean noNeedSpace(String text) {
        return text.matches("^[\\u4E00-\\u9FA50-9. ]*$")
                || text.matches("^(自动|超清|高清|标清|蓝光|流畅)\\s.*\\d+P$")
                || text.matches("^\\d+P\\s.*(自动|超清|高清|标清|蓝光|流畅)$");
    }

    private List<String> getOriginTexts(List<String> list, boolean isCompatibleMode) {
        List<String> retList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String text = list.get(i);
            if (!isCompatibleMode) {
                JSONObject object = JSON.parseObject(text);
                text = object.getString("text");
            }
            retList.add(text);
        }
        return retList;
    }

    private static String extractFirstSplitContent(String text) {
        if (text.contains("丨")) {
            String[] split = text.split("丨");
            if (split.length > 0) {
                return split[0].trim();
            }
        } else if (text.contains("|")) {
            String[] split = text.split("\\|");
            if (split.length > 0) {
                return split[0].trim();
            }
        }
        return null;
    }

    private static String genericFixedWord(String input) {
        if ("确认".equals(input)) {
            return "确定";
        } else if ("确定".equals(input)) {
            return "确认";
        } else if ("其他".equals(input)) {
            return "其它";
        } else if ("我知道了".equals(input)) {
            return "知道了";
        } else if (input.contains("Hi-Res")) {
            return input.replace("Hi-Res", "high resource");
        }
        return null;
    }

    private static String extractBracketContent(String input) {
        Matcher matcher = PATTERN_BRACKET.matcher(input);

        if (matcher.find()) {
            String result = matcher.group(1);
            if (result == null) {
                return null;
            } else {
                matcher = PATTERN_SHORT_CHARS.matcher(result.trim());
                if (matcher.matches()) {
                    return null;
                } else {
                    return result.trim();
                }
            }
        } else {
            return null;
        }
    }

    private static String removeBracketContent(String input) {
        Matcher matcher = PATTERN_LEFT_BRACKET.matcher(input);

        if (matcher.find()) {
            int bracketIndex = matcher.start();
            String result = input.substring(0, bracketIndex).trim();
            return result.isEmpty() ? null : result;
        } else {
            return null;
        }
    }

    private static String convertTimeFormat(String input) {
        Matcher matcher = PATTERN_TIME.matcher(input);
        if (matcher.matches()) {
            String number = matcher.group(1);
            String unit = matcher.group(2);
            if ("s".equals(unit)) {
                return number + "秒";
            } else if ("min".equals(unit)) {
                return number + "分钟";
            }
        }
        return null;
    }

    private static String convertYearFormat(String text) {
        Matcher matcher = PATTERN_YEAR.matcher(text);
        if (matcher.matches()) {
            StringBuffer sb = new StringBuffer();
            String group = matcher.group(0);
            if (group != null) {
                String last = group.substring(group.length() - 1);
                String numStr = group.substring(0, group.lastIndexOf(last));
                char[] chars = numStr.toCharArray();
                matcher.appendReplacement(sb, Arab2ChineseUtil.convertOnly(chars) + last);
                matcher.appendTail(sb);
                return sb.toString();
            }
        }
        return null;
    }

    private static String convertDistanceFormat(String text) {
        Matcher matcher = PATTERN_DISTANCE.matcher(text);
        if (matcher.matches()) {
            String value = matcher.group(1);
            String unit = matcher.group(2);
            if (value != null && unit != null) {
                try {
                    String convertValue = Arab2ChineseUtil.convert(Integer.parseInt(value));
                    return convertValue + unit;
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    private static List<String> convertPlaySpeed(String text) {
        Matcher matcher = PATTERN_PLAY_SPEED.matcher(text);
        if (matcher.matches()) {
            List<String> list = new ArrayList<>();
            String text1 = text.trim().replace("x", "倍").replace("X", "倍");
            String text2 = text.trim().replace("x", "倍速").replace("X", "倍速");
            list.add(text1);
            list.add(text2);
            return list;
        }
        return null;
    }

    private static List<String> convertKmFormat(String text) {
        Matcher matcher = PATTERN_KM.matcher(text);
        if (matcher.matches()) {
            List<String> list = new ArrayList<>();
            String text1 = text.trim().replace("km", "千米");
            String text2 = text.trim().replace("km", "公里");
            String text3 = text.trim().replace("m", "米");
            list.add(text1);
            list.add(text2);
            list.add(text3);
            return list;
        }
        return null;
    }

    public static String convertPercentFormat(String text) {
        Matcher matcher = PATTERN_PERCENT.matcher(text);
        if (matcher.find()) {
            String numberPart = matcher.group(1);
            if (numberPart == null) {
                return null;
            }
            StringBuilder result = new StringBuilder("百分之");
            String convert = null;
            try {
                if (numberPart.contains(".")) {
                    convert = Arab2ChineseUtil.convert(Float.parseFloat(numberPart));
                } else {
                    convert = Arab2ChineseUtil.convert(Integer.parseInt(numberPart));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (convert != null) {
                result.append(convert);
            }
            return result.toString();
        }
        return null;
    }

    private static List<String> convertTempFormat(String text) {
        Matcher matcher = PATTERN_TEMP.matcher(text);
        if (matcher.matches()) {
            List<String> list = new ArrayList<>();
            String text1 = text.trim().replace("℃", "度");
            String text2 = text.trim().replace("℃", "摄氏度");
            list.add(text1);
            list.add(text2);
            return list;
        }
        return null;
    }

    private static boolean isEnglishSentence(String text) {
        return text.matches("(?=.*[a-zA-Z])[a-zA-Z0-9 ,.?!']*");
    }

    public boolean handleViewCommandInner(int id, ViewCmdResult result, @ViewCmdType String type) {
        ViewCmdCache.Cache cache;
        if (ViewCmdType.TYPE_KWS.equals(type)) {
            cache = kwsViewCmdCache.getCache(id);
        } else if (ViewCmdType.TYPE_GLOBAL.equals(type)) {
            cache = globalViewCmdCache.getCache(id);
        } else {
            cache = viewCmdCache.getCache(id);
        }
        if (cache != null) {
            String newText = makeJson(result);
            LogUtils.v("search keyword:" + newText);
            LogUtils.v("cache.list:" + cache.list);
            if (cache.list.contains(newText)) {
                NluResult nluResult = new NluResult();
                nluResult.id = cache.uid;
                nluResult.domain = cache.pkgName;
                nluResult.rawText = result.query;
                JSONObject object = new JSONObject();
                String viewCmd = result.text;
                Map<String, String> textMap = this.textMap.get(id);
                if (textMap != null && textMap.get(newText) != null) {
                    viewCmd = textMap.get(newText);
                }
                if (viewCmd != null) {
                    viewCmd = viewCmd.replace("^", "").replace("&", "");
                    viewCmd = getTextFromSpecialMap(id, viewCmd);
                }
                String prompt = result.prompt;
                String action = null;
                if (prompt != null) {
                    prompt = result.prompt.split(":")[0];
                    if ("Common".equals(prompt)) {
                        prompt = null;
                    } else if ("Switch".equals(prompt) && result.prompt.contains(":")) {
                        action = result.prompt.split(":")[1];
                    }
                }
                int curDisplayId = MegaDisplayHelper.getVoiceDisplayId(result.direction);
                object.put("viewCmd", viewCmd);
                object.put("prompt", prompt);
                object.put("type", type);
                object.put("direction", result.direction);
                object.put("displayId", curDisplayId);
                if (action != null) {
                    object.put("action", action);
                }
                nluResult.data = object.toString();
                nluResult.intent = INTENT_VIEWCMD;
                nluResult.direction = result.direction;

                if (ViewCmdType.TYPE_NORMAL.equals(type) && curDisplayId == ceilingDisplayId && !isCeilOpen()) {
                    LogUtils.d("handleViewCommand ignore normal, celling screen has closed!");
                    return false;
                }
                DialogueManager.get().onViewCommandCallback(viewCmd, nluResult);
                return true;
            }
        }
        return false;
    }

    protected String getTextFromSpecialMap(int id, String text) {
        Map<String, String> specialMap = specialTextMap.get(id);
        if (specialMap != null) {
            String special = specialMap.get(text);
            if (special != null) {
                text = special;
            }
        }
        return text;
    }

    public boolean handleCommonViewCmd(int displayId, ViewCmdResult result) {
        if (Utils.getApp().getString(R.string.viewcmd_back).equals(result.text)) {
            if (DeviceHolder.INS().getDevices().getSystem().getKeyboard().isKeyboardShowing(displayId) && displayId == ceilingDisplayId && !isCeilOpen()) {
                LogUtils.d("handleViewCommand ignore back, celling screen has closed!");
                return false;
            }
            CommonSystemUtils.sendKeyCodeBack();
            BeanTtsManager.getInstance().speak(TtsReplyUtils.getViewCmdReply(), result.direction, null);
            ThreadUtils.runOnUiThreadDelayed(() -> {
                String topPackageName = CommonSystemUtils.getTopPackageName(displayId);
                if (DialogueManager.get().isInteractionState() && accessibleAbility.isAccessibilityApp(topPackageName)) {
                    accessibleAbility.handAccessibilityEvent(displayId);
                }
            }, 1000);

            return true;
        }
        return false;
    }

    private static boolean isCompatibleMode(String text) {
        try {
            JSONObject object = JSON.parseObject(text);
            return object == null;
        } catch (Exception e) {
            return true;
        }
    }

    public static String makeJson(ViewCmdResult result) {
        JSONObject object = new JSONObject();
        object.put("text", result.text);
        if (result.prompt != null) {
            object.put("prompt", result.prompt);
        }
        return object.toString();
    }

    @Override
    public void setKwsViewCmd(String pkg, List<String> list) {
        // kws应该是与屏幕绑定的，但历史原因只做了单屏情况，在56C/D中暂将kws作为全局的使用，后期再优化
        if (list.size() > 0) {
            kwsViewCmdCache.clear();
            List<String> newList = normalize(pkg, ViewCmdCache.KWS_ID, list);
            kwsViewCmdCache.addViewCommands(pkg, ViewCmdCache.KWS_ID, newList);
            if (!DialogueManager.get().isInteractionState()) {
                FeedbackManager.get().uploadViewCmd(new ArrayList<>(), null, kwsViewCmdCache);
            }
        } else if (!kwsViewCmdCache.isEmpty()) {
            ViewCmdCache.Cache cache = kwsViewCmdCache.getCache(ViewCmdCache.KWS_ID);
            if (cache != null && pkg.startsWith(cache.pkgName)) {
                kwsViewCmdCache.clear();
                if (!DialogueManager.get().isInteractionState()) {
                    FeedbackManager.get().uploadViewCmd(new ArrayList<>(), null, kwsViewCmdCache);
                }
            }
        }
    }

    protected static boolean isCeilOpen() {
        ScreenInterface screen = DeviceHolder.INS().getDevices().getSystem().getScreen();
        return !screen.isSupportScreen(DeviceScreenType.CEIL_SCREEN) || screen.isCeilScreenOpen();
    }

    protected static boolean isSupportNluViewCmd() {
        return DeviceHolder.INS().getDevices().getViewCmd().isSupportNluViewCmd();
    }
}
