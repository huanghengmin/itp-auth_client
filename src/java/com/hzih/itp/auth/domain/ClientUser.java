package com.hzih.itp.auth.domain;

import java.util.Date;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ztt
 * Date: 14-5-4
 * Time: 上午10:41
 * 客户端认证账号,密码
 */
public class ClientUser {

    private int id;
    private String userName;
    private String userPwd;
    private String ip;
    private String mac;
    private Date createTime;
    private Date connectTime;
    private Date disconnectTime;

    private Set<ServerUser> servers;

    public ClientUser() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(Date connectTime) {
        this.connectTime = connectTime;
    }

    public Set<ServerUser> getServers() {
        return servers;
    }

    public void setServers(Set<ServerUser> servers) {
        this.servers = servers;
    }

    public Date getDisconnectTime() {
        return disconnectTime;
    }

    public void setDisconnectTime(Date disconnectTime) {
        this.disconnectTime = disconnectTime;
    }
}
