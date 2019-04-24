package com.wyk.wechattomap.entity;

import com.baidu.mapapi.model.LatLng;

/**
 *  @author wyk
 *  记录位置及坐标的实体类
 */
public class LocationGpsBean {

    public String blackName;                //key;
    public String address;                  //city
    public String uId;                      //以uid作为标识      ,//uid
    public LatLng pt;;                      //坐标

    public String getBlackName() {
        return blackName;
    }

    public void setBlackName(String blackName) {
        this.blackName = blackName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public LatLng getPt() {
        return pt;
    }
    public void setPt(LatLng pt) {
        this.pt = pt;
    }


    @Override
    public String toString() {
        return "LocationGpsBean{" +
                "blackName='" + blackName + '\'' +
                ", address='" + address + '\'' +
                ", uId='" + uId + '\'' +
                ", pt=" + pt +
                '}';
    }
}
