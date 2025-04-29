package com.voyah.ai.basecar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.voice.sdk.device.UserCenterInterface;
import com.voyah.accountmanager.VoyahAccountManager;
import com.voyah.accountmanager.bean.UserInfo;
import com.voyah.ai.basecar.system.MegaForegroundUtils;
import com.voice.sdk.constant.ApplicationConstant;
import com.voyah.ai.common.utils.LogUtils;

import mega.config.MegaDataStorageConfig;

/**
 * author : jie wang
 * date : 2024/4/22 19:20
 * description : 用户中心
 * 个人中心接口文档：https://hav4xarv6k.feishu.cn/docx/DjbQdppHAofwQUxItbncEW2QnMg
 */
public class UserCenterInterfaceImpl extends BaseAppPresenter implements UserCenterInterface {

    private static final String TAG = "UserCenterImpl";
//    public static final String PKG_MSG_CENTER = "com.voyah.msgcenter"; //消息中心

    //消息中心、个人中心、账号在同一界面
    public static final String PKG_USER_CENTER = "com.voyah.cockpit.usercenter"; //个人中心

    private VoyahAccountManager mAccountManager;

    private UserCenterInterfaceImpl() {
    }

    public static UserCenterInterfaceImpl getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void init() {
        LogUtils.i(TAG, "init");
        super.initSdk();
        initUserCenterSdk(mContext);
    }

    @Override
    public boolean isAppForeground() {
        return false;
    }

    private void initUserCenterSdk(Context context) {
        mAccountManager = VoyahAccountManager.getInstance();
        mAccountManager.init(context);

        if (!mAccountManager.checkUserService()) {
            mAccountManager.startUserService(() -> {
                LogUtils.d(TAG, "onServiceConnected");
                UserInfo userInfo = mAccountManager.getCurrentAccount();
                LogUtils.d(TAG, "onServiceConnected userInfo:" + userInfo);
                LogUtils.d(TAG, "onServiceConnected isLogin:" + mAccountManager.isLogin());
                // todo 先每次都去存一下userId，待调试个人中心应用后在适时地存
                if (userInfo != null && !TextUtils.isEmpty(userInfo.getUserId())) {
                    SPUtils.getInstance(ApplicationConstant.SP_NAME_COMMON)
                            .put(ApplicationConstant.SP_KEY_USER_ID, userInfo.getUserId());
                }

            });
        }


        IntentFilter filter = new IntentFilter();
        filter.addAction(ApplicationConstant.ACTION_USER_CENTER_LOGIN);
        filter.addAction(ApplicationConstant.ACTION_USER_CENTER_LOGOUT);
        filter.addAction(ApplicationConstant.ACTION_USER_CENTER_USERINFO_UPDATE);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null) {
                    return;
                }
                switch (intent.getAction()) {
                    case ApplicationConstant.ACTION_USER_CENTER_LOGIN:
                        LogUtils.d(TAG, "onUserLogin.");
                        UserInfo userInfo = mAccountManager.getCurrentAccount();
                        if (userInfo != null) {
                            String userId = userInfo.getUserId();
                            String name = userInfo.getName();
                            LogUtils.d(TAG, "userId:" + userId + " name:" + name);

                        }
                        break;
                    case ApplicationConstant.ACTION_USER_CENTER_LOGOUT:
                        LogUtils.d(TAG, "onUserLogout.");
                        break;
                    case ApplicationConstant.ACTION_USER_CENTER_USERINFO_UPDATE:
                        LogUtils.d(TAG, "onUserInfoChanged.");
                        break;
                }
            }
        };

        context.registerReceiver(receiver, filter);
    }

    //判断当前用户是否登录
    @Override
    public boolean isLogin() {
        boolean isLogin = mAccountManager.isLogin();
        LogUtils.i(TAG, "isLogin is " + isLogin);
        return isLogin;
    }

    //获取当前账号类型 "0" 普通账号 "1" 车主账号 "2" 游客
    @Override
    public String getCarOwnerType() {
        String carOwnerType = mAccountManager.isCarOwner();
        LogUtils.i(TAG, "carOwnerType is " + carOwnerType);
        return carOwnerType;
    }

    //打开账号登录弹窗
    @Override
    public void startToQrPasswordLogin() {
        LogUtils.i(TAG, "startToQrPasswordLogin");
        try {
            mAccountManager.startToQrPasswordLogin();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //打开个人中心账号管理界面
    @Override
    public void startToAccountManager() {
        LogUtils.i(TAG, "startToAccountManager");
        try {
            mAccountManager.startToAccountManager();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //打开\关闭消息中心
    @Override
    public void startOrCloseMessageCenter(String switchType) {
        LogUtils.i(TAG, "startToMessageCenter switchType is " + switchType);
        try {
            if (StringUtils.equals(switchType, "open"))
                mAccountManager.startToMessageCnter();
            if (StringUtils.equals(switchType, "close"))
                MegaForegroundUtils.backToLauncher(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //打开个人中心页面
    @Override
    public void openApplocation() {
        LogUtils.i(TAG, "startToAccountCenter");
        try {
            mAccountManager.openApplocation();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //消息中心页面是否已打开
    @Override
    public boolean isMessageCenterView() {
        boolean isMessageCenter = MegaForegroundUtils.isForegroundApp(mContext, PKG_USER_CENTER);
        LogUtils.i(TAG, "isMessageCenter is " + isMessageCenter);
        return isMessageCenter;
    }

    //账号登录页面是否已打开
    @Override
    public boolean isLoginView() {
        boolean isLoginView = MegaForegroundUtils.isForegroundApp(mContext, PKG_USER_CENTER);
        LogUtils.i(TAG, "isLoginView is " + isLoginView);
        return isLoginView;
    }

    //个人中心页面是否已打开
    @Override
    public boolean isPersonalCenterView() {
        boolean isPersonalCenterView = MegaForegroundUtils.isForegroundApp(mContext, PKG_USER_CENTER);
        LogUtils.i(TAG, "isPersonalCenterView is " + isPersonalCenterView);
        return isPersonalCenterView;
    }


    //车辆是否激活
    @Override
    public boolean isCarActivated() {
        int activateStatus = MegaDataStorageConfig.getInt("key_activate_status", 0);
        LogUtils.i(TAG, "isCarActivated activateStatus is " + activateStatus);
        return activateStatus > 0;
    }


    //登录账号弹窗
    @Override
    public void loginAccount() {
        LogUtils.i(TAG, "loginAccount");
        try {
            mAccountManager.loginAccount();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param open   此参数未使用，可传入任意值
     * @param result :
     *               0 取消退出
     *               1 确认退出
     *               -1 打开退出登录弹窗
     *               <p>
     *               ②车主账号退出登录-退出账号界面接口  loginOut(int open, int result)  result 传1
     *               ③普通账号退出登录的接口   loginOut(int open, int result)  result 传2
     */
    @Override
    public void loginOut(int open, int result) {
        LogUtils.i(TAG, "loginOut open is " + open + " ,result is " + result);
        try {
            mAccountManager.loginOut(open, result);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUserId() {
        String userId = null;
        UserInfo userInfo = mAccountManager.getCurrentAccount();
        if (userInfo != null) {
            userId = userInfo.getUserId();
        }

        return userId;
    }

    private static class Holder {
        private static final UserCenterInterfaceImpl INSTANCE = new UserCenterInterfaceImpl();
    }

}
