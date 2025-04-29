package com.voyah.ai.sdk.bean;

public class PvcResult {
    /**
     * voice_id String 音库 id
     * task_id String 音库名称
     * voice_type String 音库类型：
     * 0：个性化
     * 文档保密级别：外部公开
     * 1：变声
     * 2：个性化 2.0 合成
     * 3：个性化 2.0 极速合成
     * init_biz_no Integer 创建时队列序号（前面有多
     * 少人在排队），值不会变
     * vcn String 音库 vcn
     * model_ver String 音库模型版本
     * portal String 入口
     * app_id String 应用 id
     * user_id String 用户 ID
     * third_user_id String 第三方用户 ID
     * voice_name String 音库名称
     * sex Integer 性别：
     * 0 ：未知
     * 1 ：男
     * 2 ：女
     * train_ret Integer 训练状态：
     * -1 未开始
     * 0 训练失败
     * 1 训练成功
     * 2 训练中
     * listen_url String 试听 Url
     * updated_time Integer 更新时间 时间戳
     * created_time Integer 创建时间 时间戳
     * vaild_start_time Integer 有效期开始时间
     * vaild_end_time Integer 有效期结束时间
     * callBackUrl String 召回地址
     * image String
     */
    public String voice_id;
    public String task_id;
    public String voice_type;
    public String vcn;
    public String app_id;
    public String user_id;
    public String voice_name;
    public int sex;
    public int train_ret;
    public String listen_url;
    public long updated_time;
    public long created_time;
    public long vaild_start_time;
    public long vaild_end_time;
    public String callBackUrl;
    public String image;
}