package com.hzih.itp.auth.server;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyStore;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-6
 * Time: 下午3:08
 * To change this template use File | Settings | File Templates.
 */
public class SSLServer
{
    static int port=6666;  //系统将要监听的端口号,82.6.6是偶以前女朋友的生日^_^
    static SSLServerSocket server;

    /*
    *构造函数
    */

    public SSLServer()
    {

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

            char keyStorePass[]="123qwe".toCharArray();  //证书密码

            char keyPassword[]="123qwe".toCharArray();  //证书别称所使用的主要密码

            KeyStore ks=KeyStore.getInstance("JKS");  //创建JKS密钥库

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


    public static void main(String args[])
    {
        try
        {
            server=getServerSocket(port);
            System.out.println("在"+port+"端口等待连接...");

            while(true)
            {
                SSLSocket socket=(SSLSocket)server.accept();

                //将得到的socket交给CreateThread对象处理,主线程继续监听
                new CreateThread(socket);

            }
        }catch(Exception e)
        {
            System.out.println("main方法错误80:"+e);
        }
    }
}

/*
*内部类,获得主线程的socket连接,生成子线程来处理
*/

class CreateThread extends Thread
{
    static BufferedReader in;
    static PrintWriter out;
    static Socket s;

    /*
    *构造函数,获得socket连接,初始化in和out对象
    */

    public CreateThread(Socket socket)
    {
        try
        {
            s=socket;
            in=new BufferedReader(new InputStreamReader(s.getInputStream(),"utf-8"));

            out=new PrintWriter(s.getOutputStream(),true);

            start();  //开新线程执行run方法

        }catch(Exception e)
        {
            System.out.println(e);
        }

    }

    /*
    *线程方法,处理socket传递过来的数据
    */

    public void run()
    {
        try
        {
            String msg=in.readLine();
            System.out.println(msg);
            out.println("连接成功!");
            in.close();
            out.close();
            s.close();

        }catch(Exception e)
        {
            System.out.println(e);
        }
    }
}