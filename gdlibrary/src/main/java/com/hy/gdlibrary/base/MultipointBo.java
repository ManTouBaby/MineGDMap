package com.hy.gdlibrary.base;

import com.amap.api.maps.model.LatLng;

import java.io.Serializable;

/**
 * Author: MtHappy
 * Date: 2020/7/22 15:30
 * Description: 多点实体
 */
public class MultipointBo implements Serializable {
    private double latitude;
    private double longitude;
    private Object tagObject;
    private int locationIcon;
    private String dataJson;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Object getTagObject() {
        return tagObject;
    }

    public void setTagObject(Object tagObject) {
        this.tagObject = tagObject;
    }

    public int getLocationIcon() {
        return locationIcon;
    }

    public void setLocationIcon(int locationIcon) {
        this.locationIcon = locationIcon;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }
}
