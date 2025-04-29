package com.voyah.ai.basecar.buriedpoint.bean;

public class MegaBean {
    public static class Key {
        public static final String appid = "appid";
        public static final String eid = "eid";
        public static final String ts = "ts";
        public static final String appvn = "appvn";
        public static final String edes = "edes";
        //        public static final String userid = "userid";
        public static final String others = "others";
    }

    //
    private String appid = "88";
    //
    private String eid;
    //时间戳
    private long ts;
    //
    private String appvn;
    //
    private String edes;
    //
//    private String userid;
    //埋点的json数据
    private String others;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getAppvn() {
        return appvn;
    }

    public void setAppvn(String appvn) {
        this.appvn = appvn;
    }

    public String getEdes() {
        return edes;
    }

    public void setEdes(String edes) {
        this.edes = edes;
    }

//    public String getUserid() {
//        return userid;
//    }
//
//    public void setUserid(String userid) {
//        this.userid = userid;
//    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    @Override
    public String toString() {
        return "MegaBean{" +
                "appid='" + appid + '\'' +
                ", eid='" + eid + '\'' +
                ", ts=" + ts +
                ", appvn='" + appvn + '\'' +
                ", edes='" + edes + '\'' +
                ", others='" + others + '\'' +
                '}';
    }
}
