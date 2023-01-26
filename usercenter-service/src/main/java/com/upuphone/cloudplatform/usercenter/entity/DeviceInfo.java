package com.upuphone.cloudplatform.usercenter.entity;

import lombok.Data;

@Data
public class DeviceInfo {
    private String model;

    private String deviceId;

    private String deviceType;

    private String deviceName;

    private String appId;

    public DeviceInfo(String model, String deviceId, String deviceType, String deviceName, String appId) {
        this.model = model;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.deviceName = deviceName;
        this.appId = appId;
    }

    public DeviceInfo() {
    }
}
