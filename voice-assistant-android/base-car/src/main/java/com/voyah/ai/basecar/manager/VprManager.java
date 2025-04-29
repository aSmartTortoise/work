package com.voyah.ai.basecar.manager;

import android.os.RemoteException;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.voice.sdk.constant.ConfigsConstant;
import com.voice.sdk.device.base.VprInterface;
import com.voyah.ai.sdk.IVprCallback;
import com.voyah.ai.sdk.bean.UserVprInfo;
import com.voyah.ai.sdk.bean.VprError;
import com.voyah.ai.sdk.bean.VprResult;
import com.voyah.ai.sdk.listener.IVprResultListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class VprManager implements VprInterface {

    private IVprResultListener listener;
    private UserVprInfo curVprInfo;

    public final VprError EC_VPR_REGISTER_SUCCESS = new VprError(1000, "声纹重复，请勿重复注册");
    public final VprError EC_VPR_REACH_MAX = new VprError(1001, "最多添加10个用户，请先删除部分声纹后再进行注册");
    public final VprError EC_NON_P_GEAR = new VprError(1002, "请在驻车环境下进行声纹录制");
    public final VprError EC_NO_NETWORK = new VprError(1003, "网络异常，请稍后再试");

    public final VprError EC_RECORD_SINGLE_TEXT_SUCCESS = new VprError(2000, "单条文本录入成功");
    public final VprError EC_RECORD_ALL_TEXT_SUCCESS = new VprError(2001, "所有文本录入成功");
    public final VprError EC_RECORD_VPR_EXISTED = new VprError(2002, "声纹重复，请勿重复注册");
    public final VprError EC_RECORD_NO_SPEAK = new VprError(2003, "10s内未检测到人声");
    public final VprError EC_RECORD_CONTENT_NO_MATCH = new VprError(2004, "内容不一致，识别文本不匹配");
    public final VprError EC_RECORD_FAIL_REACH_MAX = new VprError(2005, "单节点录入失败累计超过3次");
    public final VprError EC_RECORD_NOISE = new VprError(2006, "请关闭车窗空调等，保持环境安静");
    public final VprError EC_RECORD_NO_MANDARIN = new VprError(2007, "请用普通话录入");
    public final VprError EC_RECORD_NO_CLEAR = new VprError(2008, "由于各种原因，听不清楚");

    public final VprError EC_SAVE_SENSITIVE_WORD = new VprError(3001, "有敏感词，请换一个");
    public final VprError EC_SAVE_NAME_EXISTED = new VprError(3002, "名字重复了，请换一个");

    /**
     * 开始注册声纹识别
     */
    public String startRegisterVpr(String vprId, int direction, IVprCallback callback) {
        listener = vprResult -> {
            if (callback != null) {
                try {
                    callback.onVprResult(JSON.toJSONString(vprResult));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
        if (vprId == null) {
            String id = UUID.randomUUID().toString();
            curVprInfo = new UserVprInfo(id);
        } else {
            curVprInfo = getUserVprInfo(vprId);
            if (curVprInfo == null) {
                LogUtils.w("exception, please check the code!!!");
                String id = UUID.randomUUID().toString();
                curVprInfo = new UserVprInfo(id);
            }
        }
        curVprInfo.direction = direction;
        return curVprInfo.id;
    }

    /**
     * 结束声纹注册
     */
    public void stopRegisterVpr(String id, int errCode) {
        curVprInfo = getUserVprInfo(id);
        if (errCode == 0 && checkVprInfoIsCompleted(curVprInfo)) {
            LogUtils.d("stopRegisterVpr normal");
        } else {
            LogUtils.d("stopRegisterVpr abnormal");
            deleteUserVpr(id);
        }
        curVprInfo = null;
        listener = null;
    }

    /**
     * 删除用户声纹
     */
    public int deleteUserVpr(String id) {
        SPUtils.getInstance(ConfigsConstant.SP_VPR_NAME).remove(ConfigsConstant.SP_KEY_VPR_USER + "_" + id, true);
        Utils.getApp().getContentResolver().notifyChange(ConfigsConstant.URI_EXPORT_VPR_USER_INFO, null);
        return 0;
    }

    /**
     * 保存声纹信息
     */
    public int saveUserVprInfo(UserVprInfo vp) {
        if (isSensitiveWord(vp.name)) {
            if (listener != null) {
                listener.onVprResult(new VprResult(vp.id, EC_SAVE_SENSITIVE_WORD));
                return -1;
            }
        } else if (isNameExist(vp.name)) {
            if (listener != null) {
                listener.onVprResult(new VprResult(vp.id, EC_SAVE_NAME_EXISTED));
                return -1;
            }
        }
        vp.timestamp = System.currentTimeMillis();
        SPUtils.getInstance(ConfigsConstant.SP_VPR_NAME).put(ConfigsConstant.SP_KEY_VPR_USER + "_" + vp.id, JSON.toJSONString(vp), true);
        Utils.getApp().getContentResolver().notifyChange(ConfigsConstant.URI_EXPORT_VPR_USER_INFO, null);
        return 0;
    }

    private boolean isNameExist(String name) {
        List<UserVprInfo> list = getRegisteredVprList();
        if (list != null && list.size() > 0) {
            for (UserVprInfo info : list) {
                if (TextUtils.equals(info.name, name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSensitiveWord(String name) {
        List<String> list = ResourceUtils.readAssets2List("dict/sensitive_word_dict.txt");
        if (list != null && list.size() > 0) {
            for (String word : list) {
                if (name.contains(word)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkVprInfoIsCompleted(UserVprInfo info) {
        return info != null && info.id != null && info.texts != null
                && info.texts.size() == 3 && info.name != null;
    }

    /**
     * 获取已注册声纹
     */
    public List<UserVprInfo> getRegisteredVprList() {
        Map<String, ?> allUsers = SPUtils.getInstance(ConfigsConstant.SP_VPR_NAME).getAll();
        if (allUsers != null && allUsers.size() > 0) {
            List<UserVprInfo> list = new ArrayList<>();
            for (Map.Entry<String, ?> entry : allUsers.entrySet()) {
                String value = (String) entry.getValue();
                UserVprInfo userVprInfo = JSON.parseObject(value, UserVprInfo.class);
                list.add(userVprInfo);
            }
            list.sort(Comparator.comparingLong(a -> a.timestamp));
            return list;
        }
        return null;
    }

    /**
     * 开始声纹录制
     */
    public void startRecordingVpr(String id, String text) {
        curVprInfo = getUserVprInfo(id);
        if (curVprInfo != null) {
            curVprInfo.texts.remove(text);
        } else {
            LogUtils.w("curVprInfo is null or id is incorrect!!");
        }
    }

    /**
     * 结束声纹录制
     */
    public void stopRecognizeVpr(String id, String text, int errCode) {
        curVprInfo = getUserVprInfo(id);
        if (curVprInfo != null) {
            if (errCode == 0) {
                curVprInfo.texts.add(text);
            }
        } else {
            LogUtils.w("curVprInfo is null or id is incorrect!!");
        }
    }

    @Override
    public int getMaxSupportedVprNum() {
        return 10;
    }

    public UserVprInfo getUserVprInfo(String id) {
        if (curVprInfo != null && TextUtils.equals(curVprInfo.id, id)) {
            return curVprInfo;
        } else {
            UserVprInfo userVprInfo = null;
            String json = SPUtils.getInstance(ConfigsConstant.SP_VPR_NAME).getString(ConfigsConstant.SP_KEY_VPR_USER + "_" + id);
            if (!TextUtils.isEmpty(json)) {
                userVprInfo = JSON.parseObject(json, UserVprInfo.class);
            }
            return userVprInfo;
        }
    }

    private static class Holder {
        private static final VprManager _INSTANCE = new VprManager();
    }

    private VprManager() {
        super();
    }

    public static VprManager get() {
        return Holder._INSTANCE;
    }
}
