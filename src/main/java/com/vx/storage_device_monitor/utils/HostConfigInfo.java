package com.vx.storage_device_monitor.utils;

/**
 * 主机配置信息Class,与Json配置文件字段一一映射
 */
public class HostConfigInfo {
    public HostConfigInfo(){}
    public HostConfigInfo(String _ip, String _username, String _password){
        ip = _ip;
        username = _username;
        password = _password;
    }
    //IP
    public String ip;
    //用户名
    public String username;
    //密码
    public String password;
}
