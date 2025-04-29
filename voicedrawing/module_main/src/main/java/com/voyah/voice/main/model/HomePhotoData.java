package com.voyah.voice.main.model;

import com.voyah.voice.main.R;

import java.util.ArrayList;
import java.util.List;

public class HomePhotoData {
    private List<String> descList;
    private List<String> styleList;

    private List<String> urlBigList;

    private List<String> urlSmallList;

    public List<String> getDescList() {
        return descList;
    }

    public void setDescList(List<String> descList) {
        this.descList = descList;
    }

    public List<String> getStyleList() {
        return styleList;
    }

    public void setStyleList(List<String> styleList) {
        this.styleList = styleList;
    }

    public List<String> getUrlBigList() {
        return urlBigList;
    }

    public void setUrlBigList(List<String> urlBigList) {
        this.urlBigList = urlBigList;
    }

    public List<String> getUrlSmallList() {
        return urlSmallList;
    }

    public void setUrlSmallList(List<String> urlSmallList) {
        this.urlSmallList = urlSmallList;
    }
}
