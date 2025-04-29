package com.voyah.h37z;

import java.util.List;

public class CommonTipBean {

    public String image;
    public List<String> tips;
    public String title;

    public CommonTipBean() {
    }

    public CommonTipBean(String image, List<String> tips, String title) {
        this.image = image;
        this.tips = tips;
        this.title = title;
    }

    @Override
    public String toString() {
        return "CommonTipBean{" +
                "image='" + image + '\'' +
                ", tips=" + tips +
                ", title='" + title + '\'' +
                '}';
    }
}
