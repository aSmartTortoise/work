package com.voyah.voice.main.model;

public class PhotoItem {

    private String resStr;

    private String smallResStr;

    private int resId = 0;

    private int smallResId = 0;
    private final String style;
    private final String desc;

    public PhotoItem(String resStr, String smallResStr, String desc, String style) {
        this.resStr = resStr;
        this.smallResStr = smallResStr;
        this.desc = desc.replace("”", "").replace("“", "");
        this.style = style;
    }

    public PhotoItem(int resId, int smallResId, String desc, String style) {
        this.resId = resId;
        this.smallResId = smallResId;
        this.desc = desc.replace("”", "").replace("“", "");
        this.style = style;
    }


    public Object getRes() {
        if (resStr != null && !resStr.isEmpty()) {
            return resStr;
        }
        return resId;
    }


    public Object getSmallRes() {
        if (smallResStr != null && smallResStr.length() > 0) {
            return smallResStr;
        }
        return smallResId;
    }

    public String getDesc() {
        return desc;
    }

    public String getStyle() {
        return style;
    }
}
