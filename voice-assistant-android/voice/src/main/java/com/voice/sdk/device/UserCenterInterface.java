package com.voice.sdk.device;

/**
 * author : jie wang
 * date : 2025/3/5 20:33
 * description :
 */
public interface UserCenterInterface extends DomainInterface {

    boolean isLogin();

    String getCarOwnerType();

    void startToQrPasswordLogin();

    void startToAccountManager();

    void startOrCloseMessageCenter(String switchType);

    void openApplocation();

    boolean isMessageCenterView();

    boolean isLoginView();

    boolean isPersonalCenterView();

    boolean isCarActivated();

    void loginAccount();

    void loginOut(int open, int result);

    String getUserId();

}
