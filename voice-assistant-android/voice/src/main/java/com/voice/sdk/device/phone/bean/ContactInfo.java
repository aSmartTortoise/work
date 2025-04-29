package com.voice.sdk.device.phone.bean;

import com.voyah.ds.common.entity.domains.call.ContactNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:lcy
 * @data:2025/3/5
 **/
public class ContactInfo {
    private int position;
    private String name;
    private int showNumberIndex = 0;
    private String matchPin = "";
    private String namePinYin = "";
    private int source_type = 0;
    private final List<ContactNumberInfo> numberInfoList = new ArrayList();

    public ContactInfo() {
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getShowNumberIndex() {
        return this.showNumberIndex;
    }

    public void setShowNumberIndex(int showNumberIndex) {
        this.showNumberIndex = showNumberIndex;
    }

    public String getMatchPin() {
        return this.matchPin;
    }

    public void setMatchPin(String matchPin) {
        this.matchPin = matchPin;
    }

    public String getNamePinYin() {
        return this.namePinYin;
    }

    public void setNamePinYin(String namePinYin) {
        this.namePinYin = namePinYin;
    }

    public int getSource_type() {
        return this.source_type;
    }

    public void setSource_type(int source_type) {
        this.source_type = source_type;
    }

    public List<ContactNumberInfo> getNumberInfoList() {
        return this.numberInfoList;
    }

    public void setNumberInfoList(List<ContactNumberInfo> numberInfoList) {
        this.numberInfoList.clear();
        this.numberInfoList.addAll(numberInfoList);
    }
}
