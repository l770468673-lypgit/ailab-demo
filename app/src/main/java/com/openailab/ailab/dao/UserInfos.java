package com.openailab.ailab.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class UserInfos {
    //    @Id(autoincrement = true)
    //    private long _id;

    @Generated
    private String _userid;
    @Generated
    private String name;
    @Generated
    private String phone;
    @Generated
    private String Sex;
    @Generated
    private String time;
    @Generated
    private String heat;
    @Generated(hash = 1947056991)
    public UserInfos(String _userid, String name, String phone, String Sex,
            String time, String heat) {
        this._userid = _userid;
        this.name = name;
        this.phone = phone;
        this.Sex = Sex;
        this.time = time;
        this.heat = heat;
    }
    @Generated(hash = 2018014889)
    public UserInfos() {
    }
    public String get_userid() {
        return this._userid;
    }
    public void set_userid(String _userid) {
        this._userid = _userid;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhone() {
        return this.phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getSex() {
        return this.Sex;
    }
    public void setSex(String Sex) {
        this.Sex = Sex;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getHeat() {
        return this.heat;
    }
    public void setHeat(String heat) {
        this.heat = heat;
    }
  

}
