package com.xunteng.ujb.entity;

public class VersionInfo {

    private int versionCode;
    private String url;
    private String version;
    private String remarks;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "VersionInfo{" +
                "versionCode=" + versionCode +
                ", url='" + url + '\'' +
                ", version='" + version + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
