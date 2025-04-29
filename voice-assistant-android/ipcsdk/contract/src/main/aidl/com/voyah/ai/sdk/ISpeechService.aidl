package com.voyah.ai.sdk;

import com.voyah.ai.sdk.IVAReadyCallback;
import com.voyah.ai.sdk.IVAStateCallback;
import com.voyah.ai.sdk.IVAResultCallback;
import com.voyah.ai.sdk.IHintResultCallback;
import com.voyah.ai.sdk.ITtsCallback;
import com.voyah.ai.sdk.IVprCallback;
import com.voyah.ai.sdk.IPvcCallback;
import com.voyah.ai.sdk.ILongAsrCallback;
import com.voyah.ai.sdk.IRmsDbCallback;
import com.voyah.ai.sdk.IAgentCallback;
import com.voyah.ai.sdk.IImageRecognizeCallback;
import com.voyah.ai.sdk.IAsrListener;
import com.voyah.ai.sdk.SceneIntent;

interface ISpeechService {
    /**
    * 注册语音就绪回调
    */
    void registerReadyCallback(String packageName, IVAReadyCallback callback);

    /**
    * 反注册语音就绪回调
    */
    void unregisterReadyCallback(String packageName, IVAReadyCallback callback);

    /**
    * 语音是否已就绪
    */
    boolean isRemoteReady();

    /**
    * 注册语音状态回调
    * @param pkgname 包名
    * @param cb
    */
    void registerStateCallback(String pkgname, IVAStateCallback cb);

    /**
    * 反注册语音状态回调
    * @param pkgname 包名
    * @param cb
    */
    void unregisterStateCallback(String pkgname, IVAStateCallback cb);

    /**
    * 注册语音结果回调
    * @param pkgname 包名
    * @param cb
    */
    void registerResultCallback(String pkgname, IVAResultCallback cb);

    /**
    * 反注册语音结果回调
    * @param pkgname 包名
    * @param cb
    */
    void unregisterResultCallback(String pkgname, IVAResultCallback cb);

    /**
    * TTS播报
    * @param pkgname 包名
    */
    oneway void speak(in String pkgname, in String text, in ITtsCallback callback, in String speaker, in boolean highPriority);

    /**
     * 停止正在进行的播报
     * @param pkgname 包名
     */
     void stopCurTts(String pkgname);

    /**
    * 停止所有播报
    * @param pkgname 包名
    */
    oneway void shutUp(String pkgname);

    /**
     * 开启交互
     * @param pkgname 包名
     * @param direction 方位
     */
    oneway void startDialogue(String pkgname, int direction);

    /**
     * 停止交互
     * @param pkgname 包名
     */
    oneway void stopDialogue(String pkgname);

    /**
    * 获取唤醒方位
    */
    int getWakeupDirection();

    /**
     * 获取语音生命状态
     */
    String getLifeState();

    /**
     * 获取hints提示语
     */
    void getHintResult(String pkgname, int num, in IHintResultCallback callback);

    /**
     * asrEvent埋点
     * @param pkg 包名
     * @param skill
     * @param scene
     * @param intent
     * @param tts
     */
    void trackAsrEvent(String pkg, String skill, String scene, String intent, String tts);

    /**
    * 订阅/取消订阅的技能列表
    *
    * @param pkg 包名
    * @param domainList 技能列表
    */
    oneway void subscribeDomains(String pkg, in List<String> domainList);

    /**
     * 自定义快捷指令
     * @param pkg 包名
     * @param jsonStr 指令集
     */
    oneway void setShortCommands(String pkg, in String jsonStr);

    /**
    * 可见文本变化回调
    **/
    void onViewContentChange(String pkgname, String json);

    /**
    * 重新注册已有的用户声纹
    **/
    String startRegisterVprWithId(String pkgname, String vprId, int direction, IVprCallback vprCallback);

    /**
     * 开始注册用户声纹
     * @param pkg 包名
     * @param direction 方位
     * @param vprCallback 回调
    */
    String startRegisterVpr(String pkgname, int direction, IVprCallback vprCallback);

    /**
     * 结束注册用户声纹
     * @param pkg 包名
     * @param vprId 声纹id
     * @param errCode 错误码
     */
    void stopRegisterVpr(String pkgname, String vprId, int errCode);
    /**
     * 删除声纹
     * @param pkg 包名
     * @param id   删除声纹id
     */
    int deleteUserVpr(String pkgname, String id);

    /**
     * 开始录制声纹
     * @param pkg 包名
     * @param vprId 声纹id
     * @param text 录制文本
     */
    void startRecordingVpr(String pkgname, String vprId, String text);

    /**
     * 结束录制声纹
     * @param pkg 包名
     * @param vprId 声纹id
     * @param text 录制文本
     * @param errCode 错误码
     */
     void stopRecordingVpr(String pkgname, String vprId, String text, int errCode);

    /**
     * 获取可注册的声纹最大数量
     */
    int getMaxSupportedVprNum();

    /**
     * 获取用户声纹信息
     * @param vprId 声纹id
     */
     String getUserVprInfo(String vprId);

    /**
     * 保存用户声纹
     */
     int saveUserVprInfo(String pkgName, String json);

    /**
     * 获取已注册的所有声纹信息
     */
     String getRegisteredVprList();

    /**
      * 设置语音开关
      * @param pkgname 包名
      * @param switchName 开关名字，参考DhSwitch类定义
      * @param enable true 开启；false 关闭
     */
     void enableSwitch(String pkgname, String switchName, boolean enable);

    /**
     * 获取语音开关当前值
     * @param switchName 开关名字，参考DhSwitch类定义
     * @return
     */
     boolean isEnableSwitch(String switchName);

    /**
     * 切换方言
     * @param pkgname 包名
     * @param dialect 方言
     */
    void changeDialect(String pkgname, in String dialect);

    /**
     * 获取当前方言
     */
    String getCurrentDialect();

    /**
    * 获取声音复刻列表
    */
    void getPvcList(String pkgname, IPvcCallback callback);

    /**
     * 设置呢称
     * @param pkgname 包名
     * @param nickname 呢称
     */
    void setNickname(String pkgname, String nickname);

    /**
     * 恢复呢称
     * @param pkgname 包名
     */
    void restoreNickname(String pkgname);

    /**
     * 获取呢称
     */
    String getNickname();

    /**
     * 设置自定义唤醒欢迎语
     *
     * @param direction 方位
     * @param greet 自定义唤醒欢迎语
     */
    void setDiyGreeting(String pkgname, int direction, String greet);

    /**
     * 获取自定义唤醒欢迎语
     *
     * @param direction
     * @return
     */
    String getDiyGreeting(int direction);

    /**
     * 设置拾音模式
     * @param pkgname 包名
     * @param mode 模式
     */
    void setPickupMode(String pkg, int mode);

    /**
     * 获取拾音模式
     */
    int getPickupMode();

    /**
     * 设置连续对话延时聆听时长
     * @param pkgname 包名
     * @param time 时长
     */
    void setContinuousWaitTime(String pkg, int time);

    /**
     * 获取连续对话延时聆听时长
     */
    int getContinuousWaitTime();

    /**
     * 音区设定
     * @param pkgname 包名
     * @param mask 音区设置码
     */
    void setUserVoiceMicMask(String pkgname, int mask);

    /**
     * 获取当前音区
     */
    int getUserVoiceMicMask();

    /**
     * 开启长识别
     * @param pkgname 包名
     * @param screenType 屏幕类型
     * @param callback 回调
     */
    void startLongAsr(String pkgname, int screenType, ILongAsrCallback callback);

    /**
     * 结束长识别
     */
    void stopLongAsr(String pkgname, int screenType);

    /**
     * 是否正在进行长识别
     *
     * @return
     */
     boolean isLongAsrGoing(int screenType);

     /**
      * 获取音乐媒体偏好
      *
      * @return
      */
      int getMusicPreference();

     /**
      * 设置音乐媒体偏好
      *
      * @param preference 智能推荐，音乐1，音乐2
      * @return
      */
      int setMusicPreference(String pkgname, int preference);

     /**
      * 获取视频媒体偏好
      *
      * @return
      */
      int getVideoPreference();

     /**
      * 设置视频媒体偏好
      *
      * @param preference 智能选择，腾讯视频，爱奇艺
      * @return
      */
      int setVideoPreference(String pkgname, int preference);

      /**
       * 上传个性化热词
       * @param json
       **/
      void uploadPersonalEntity(String pkgname, String json);

     /**
      * 设置音量大小回调
      */
     void setRmsDbListener(String pkgname, in IRmsDbCallback callback);

     int registerAgentX(String pkgname, in IAgentCallback callback);

     void registerAsrListener(IAsrListener asrListener);

     void unregisterAsrListener(IAsrListener asrListener);

     /**
      * 是否正在显示空调/座椅/ALL-APP等特殊view
      */
     boolean isShowingTopCoverView(String callPkg);

     /**
      * 获取当前语音vpa所在的displayId
      *
      * @return -1:无效值
      */
      int getCurVpaDisplayId();

      /**
      * 主动交互：触发场景意图
      * @param intent 场景意图
      */
      int triggerSceneIntent(String pkgname, in SceneIntent intent);

      /**
       * 设置特殊view正在显示
       */
      void setTopCoverViewShowing(String pkgname, int displayId, boolean isShow);

       /**
        * 是否正在显示空调/座椅/ALL-APP等特殊view
        */
       boolean isShowingTopCoverViewWithDisplayId(String callPkg, int displayId);

       /**
        * 根据屏幕类型获取displayId
        */
       int getDisplayIdForScreenType(int screenType);

      /**
       * 获取分屏时左侧宽度
       */
      int getLeftSplitScreenWidth();

      /**
       * 设置新闻消息推送配置时间
       * @param pkgname 包名
       * @param time 自定义时间
       */
      void setNewsPushConfigTime(String pkg, String time);

      /**
       * 获取新闻消息推送配置时间
       */
      String getNewsPushConfigTime();

      /**
       * 获取大模型选择偏好
       *
       * @return
       */
       int getAiModelPreference();

      /**
       * 设置大模型选择偏好
       *
       * @param preference 逍遥座舱大模型，DeepSeek
       * @return
       */
       int setAiModelPreference(String pkgname, int preference);

       /**
       * 开始图像识别
       * @param pkgname 包名
       * @param screenType 屏幕类型
       */
       String startImageRecognize(String pkgname, int screenType);

       /**
       * 注册图像识别回调
       * @param pkgname 包名
       * @param cb
       */
       void registerImageRecognizeCallback(String pkgname, IImageRecognizeCallback cb);

       /**
       * 反注册图像识别回调
       * @param pkgname 包名
       * @param cb
       */
       void unregisterImageRecognizeCallback(String pkgname, IImageRecognizeCallback cb);

       /**
       * 获取图像识别状态
       */
       int getImageRecognizeState();
}