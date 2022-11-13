package com.gb.library.core.data;

public class RequestData {

    private String pname; // 包名
    private long vc_apk;  // apk当前版本
    private long vc_web;  // 离线web包当前版本
    private long vc_mini; // 小程序包当前版本
    private String country; // 国家代码

    public RequestData(String pname, long vc_apk, long vc_web, long vc_mini, String country) {
        this.pname = pname;
        this.vc_apk = vc_apk;
        this.vc_web = vc_web;
        this.vc_mini = vc_mini;
        this.country = country;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public long getVc_apk() {
        return vc_apk;
    }

    public void setVc_apk(long vc_apk) {
        this.vc_apk = vc_apk;
    }

    public long getVc_web() {
        return vc_web;
    }

    public void setVc_web(long vc_web) {
        this.vc_web = vc_web;
    }

    public long getVc_mini() {
        return vc_mini;
    }

    public void setVc_mini(long vc_mini) {
        this.vc_mini = vc_mini;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
