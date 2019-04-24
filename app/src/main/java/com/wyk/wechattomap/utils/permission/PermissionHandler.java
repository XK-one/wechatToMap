package com.wyk.wechattomap.utils.permission;

/**
 * Created by mike on 2017/3/13.
 * 权限回调接口
 */

public abstract class PermissionHandler {

    private boolean force=false;//是否为强制的权限，如果是的话，需退出当前Activity
    private String permissionName="权限";//设置权限的名称

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    /**
     * 权限通过
     */
    public abstract void onGranted();

    /**
     * 权限拒绝
     *
     *  @return 如果要覆盖原有提示则返回true
     */
    public boolean onDenied() {
        return false;
    }

    /**
     * 不再询问
     *
     * @return 如果要覆盖原有提示则返回true
     */
    public boolean onNeverAsk() {
        return false;
    }

}
