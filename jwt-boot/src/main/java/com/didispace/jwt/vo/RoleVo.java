package com.didispace.jwt.vo;

/**
 * 角色
 */
public class RoleVo implements java.io.Serializable {
    private String id;
    private String name;

    public RoleVo() {
    }

    public RoleVo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
