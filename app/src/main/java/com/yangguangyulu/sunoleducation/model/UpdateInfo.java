package com.yangguangyulu.sunoleducation.model;

import java.io.Serializable;

/**
 * UpdateInfo
 */
@SuppressWarnings("unused")
public class UpdateInfo implements Serializable {
    private int id;
    private String versionName = "";            //当前版本名称
    private int mandatoryUpdate;                //是否强制更新  1是   2否
    private String packetUrl;                   //Android下载路径
    private String versionNumber = "";          //版本号 1.0.0
    private String updateDescription;           //更新描述

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getUpdateDescription() {
        return updateDescription;
    }

    public void setUpdateDescription(String updateDescription) {
        this.updateDescription = updateDescription;
    }

    public String getAndroidUrl() {
        return packetUrl;
    }

    public void setAndroidUrl(String androidUrl) {
        this.packetUrl = androidUrl;
    }

    public String getVersionNumber() {
        return versionNumber.replace("V", "").replace("v", "");
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getMandatoryUpdate() {
        return mandatoryUpdate == 1;
    }

    public void setMandatoryUpdate(int mandatoryUpdate) {
        this.mandatoryUpdate = mandatoryUpdate;
    }
}
