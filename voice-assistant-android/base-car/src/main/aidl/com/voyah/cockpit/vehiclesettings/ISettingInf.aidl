// ISettingInf.aidl
package com.voyah.cockpit.vehiclesettings;

// 车辆设置对外统一接口

interface ISettingInf {
    /**
     * @params action 指令，比如打开页面、定位功能、打开弹窗等。格式：com.voyah.vehicle.action.XXXX
     */
    void exec(String action);

    /**
     * @params action 指令
     * @return 当前程序是否处于指令执行的状态
     */
    boolean isCurrentState(String action);

    /**
    * action：指令
      指令指定的状态查询
      比上面一个方法返回更多的状态
    */
    String getCurrentState(String action);
}