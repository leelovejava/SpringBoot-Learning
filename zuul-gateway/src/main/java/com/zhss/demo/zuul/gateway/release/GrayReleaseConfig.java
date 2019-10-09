package com.zhss.demo.zuul.gateway.release;

/**
 * 灰度发布配置对象
 *
 * @author leelovejava
 */
public class GrayReleaseConfig {
    /**
     * 主键
     */
    private int id;
    private String serviceId;
    private String path;
    /**
     * 是否启用灰度发布 1启用 0停用
     */
    private int enableGrayRelease;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getEnableGrayRelease() {
        return enableGrayRelease;
    }

    public void setEnableGrayRelease(int enableGrayRelease) {
        this.enableGrayRelease = enableGrayRelease;
    }

}
