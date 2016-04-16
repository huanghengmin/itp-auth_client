package com.hzih.itp.auth.servlet;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-6
 * Time: 下午2:38
 * To change this template use File | Settings | File Templates.
 */
public class RZPortListenService implements Runnable{

    private boolean isRunning = false;
    private static int port ;    //认证服务将要监听的端口号
    static SSLServerSocket server;

    public RZPortListenService() {
    }

    /*
    *@param port 监听的端口号
    *@return 返回一个SSLServerSocket对象
    */

    private static SSLServerSocket getServerSocket(int thePort)
    {
        SSLServerSocket s=null;
        try
        {
            String key=System.getProperty("itpauth.home") + "/security/SSLKey";   //要使用的证书名

            char keyStorePass[]="123qwe".toCharArray();   //证书密码

            char keyPassword[]="123qwe".toCharArray();   //证书别称所使用的主要密码

            KeyStore ks=KeyStore.getInstance("JKS");   //创建JKS密钥库

            ks.load(new FileInputStream(key),keyStorePass);

            //创建管理JKS密钥库的X.509密钥管理器
            KeyManagerFactory kmf=KeyManagerFactory.getInstance("SunX509");

            kmf.init(ks,keyPassword);

            SSLContext sslContext=SSLContext.getInstance("SSLv3");

            sslContext.init(kmf.getKeyManagers(),null,null);

            //根据上面配置的SSL上下文来产生SSLServerSocketFactory,与通常的产生方法不同
            SSLServerSocketFactory factory=sslContext.getServerSocketFactory();

            s=(SSLServerSocket)factory.createServerSocket(thePort);

        }catch(Exception e)
        {
            System.out.println(e);
        }
        return(s);
    }

    public void init(int port){
        try{
            server=getServerSocket(port);
            System.out.println("在"+port+"端口等待连接...");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        isRunning = true;
        while (isRunning){
            try{
                SSLSocket socket=(SSLSocket)server.accept();

                //将得到的socket交给CreateThread对象处理,主线程继续监听
                new RZClient(socket);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }
}
