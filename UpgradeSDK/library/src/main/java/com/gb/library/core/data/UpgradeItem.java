package com.gb.library.core.data;

public class UpgradeItem {

    /** 类型 0-apk 1-web 2-mini */
    private int type;
    /** 当前最新版本 */
    private long latestVersion;
    /** 升级包大小 单位：Byte */
    private long size;
    /** 最新版本下载地址 */
    private String downloadUrl;
    /** 签名类型 默认:md5 */
    private String signType;
    /** 签名值 */
    private String signature;
    /** 文件类型 */
    private String mimeType;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(long latestVersion) {
        this.latestVersion = latestVersion;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
