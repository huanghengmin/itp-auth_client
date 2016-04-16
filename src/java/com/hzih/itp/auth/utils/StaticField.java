package com.hzih.itp.auth.utils;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-6
 * Time: 上午11:25
 * To change this template use File | Settings | File Templates.
 */
public class StaticField {
    public final static String systemPath = System.getProperty("ichange.home") != null ? System.getProperty("ichange.home"):System.getenv("ichange.home");

    public static final String Platform_Type_EX_ITP = "itp-ex";//导入前置机
    public static final String Platform_Type_EX_STP = "stp-ex";//单向设备前置机
    public static final String Platform_Type_IN_STP = "stp-in";//单向设备后置机
    public static final String Platform_Type_IN_ITP = "itp-in";//导入设备

    public static final String Auth_Type_S = "server";
    public static final String Auth_type_C = "client";

    public static final String CLIENT_CONFIG = StaticField.systemPath + "/others/client.properties"; //保存客户端服务端口配置
    /**
     * http包头参数名
     */
    public static final String Command = "Command";//发送命令类型的参数名
    public static final String SendConfig = "sendConfig";//发送配置文件命令
    public static final String AlertConfig = "alertConfig";//告警命令
    public static final String SendChannelTest = "sendChannelTest";//平台通道测试

    public static final int MB = 1024 * 1024;
    public static final int GB = 1024 * 1024 * 1024;


    /**
     * 认证证书路径
     */



    public static String TRUST_KEY_STORE = systemPath + "/security/cert/TrustKey.keystore";

    public static String TRUST_KEY_PASSWORD = "123qwe";

    public static String SERVER_KEY_STORE = systemPath + "/security/cert/server/server.pfx";
    public static String SERVER_KEY_STORE_PASSWORD = "123qwe";


    public static final String TRUST_STORE = systemPath + "/security/cert/TrustKey.keystore";
    public static final String TRUST_KEY_STORE_PASSWORD = "123qwe";

    public static final String CLIENT_KEY_STORE = systemPath + "/security/cert/client/client.pfx";
    public static final String CLIENT_KEY_STORE_PASSWORD = "123qwe";


    /**
     * command
     */
    public static final String CONNECT = "connect";//客户端认证
    public static final String KEEPALIVE = "keepalive"; //客户端发送
    public static final String DISCONNECT = "disconnect"; //取消客户端认证
    public static final String SERVER = "servers"; //获取客户端所有服务权限
    public static final String PASSWORD = "password";  //获取服务密码
    public static final String DIR= "dir";  //获取服务路径
//    public static final String RULE="rule";  //建立iptable规则
}
