package com.pinit.pinitmobileapp;

public class SettingsItem {

    private String pinType;
    private int pinImgId;


    public SettingsItem(String pinType, int pinImgId) {
        this.pinType = pinType;
        this.pinImgId = pinImgId;
    }

    public String getPinType() {
        return pinType;
    }

    public int getPinImgId() {
        return pinImgId;
    }

    public void setPinType(String pinType) {
        this.pinType = pinType;
    }

    public void setPinImgId(int pinImgId) {
        this.pinImgId = pinImgId;
    }
}
