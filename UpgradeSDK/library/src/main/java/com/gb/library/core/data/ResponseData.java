package com.gb.library.core.data;

import java.util.List;

public class ResponseData {

    /** 响应结果 */
    private int code;

    /** 失败或者无更新为空，非空则需升级 */
    private List<UpgradeItem> data;

    /** 结果描述 */
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<UpgradeItem> getData() {
        return data;
    }

    public void setData(List<UpgradeItem> data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
