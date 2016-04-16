package com.hzih.itp.auth.servlet;

import com.hzih.itp.auth.action.client.ClientAction;
import com.hzih.itp.auth.service.LogService;
import com.hzih.itp.auth.utils.SpringContextUtil;
import com.hzih.itp.auth.utils.StaticField;
import org.apache.log4j.Logger;
import org.apache.mina.filter.ssl.SslContextFactory;
import sun.misc.BASE64Encoder;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-7
 * Time: 下午2:53
 * To change this template use File | Settings | File Templates.
 */
public class KeepAliveService implements Runnable {
    private static Logger logger = Logger.getLogger(ClientAction.class);

    private String serverIp;
    private String serverPort;
    private String clientUser;
    private String clientPwd;
    private String clientIp;
    private String clientPort;

    public void init(){
        Properties properties = null;
        try{
            InputStream in = new FileInputStream(new File(StaticField.CLIENT_CONFIG));
            properties = new Properties();
            properties.load(in);
            if(properties!=null){
                clientUser = properties.getProperty("clientUser");
                clientPwd = properties.getProperty("clientPwd");
                serverIp = properties.getProperty("serverIp");
                serverPort = properties.getProperty("serverPort");
                clientIp = properties.getProperty("clientIp");
                clientPort = properties.getProperty("clientPort");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void run() {
        while (ClientAction.connect){
            try{
                work();
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                Thread.sleep(5*60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    private void work() {
        try
        {
//            System.setProperty("javax.net.ssl.trustStore", StaticField.CLIENT_KEY_STORE);

            Socket s = getSocket(serverIp, Integer.parseInt(serverPort));

            PrintWriter out=new PrintWriter(s.getOutputStream(),true);
            String userName = new String(new BASE64Encoder().encode(clientUser.getBytes()));

            out.println("{\"command\":\"" + StaticField.KEEPALIVE + "\",\"clientUser\":\"" + userName + "\",\"clientPwd\"=\"" + clientPwd + "\"}");
            out.flush();
            BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream(),"GBK"));
            String msg=in.readLine();
            logger.info("msg:" + msg);
            if(msg.indexOf("失败") > -1){
                ClientAction.connect = false;
            }
            in.close();
            out.close();
            s.close();
        }catch(Exception e){
            System.out.println(e);
            LogService logService = (LogService) SpringContextUtil.getBean("logService");
            logService.newSysLog("ERROR", "认证模式", "认证超时", clientUser + "发送脉冲失败");
        }
    }

    private Socket getSocket(String remoteIp,int remotePort) throws Exception {
        int x=StaticField.CLIENT_KEY_STORE.lastIndexOf(".") ;
        String s=StaticField.CLIENT_KEY_STORE.substring(x+1) ;

        SSLContext key = null;

        SslContextFactory sslcontextFactory = new SslContextFactory();
        sslcontextFactory.setProtocol("TLS");
        KeyStore ks = null;
        if(s.equals("p12")||s.equals("pfx")){
            ks = KeyStore.getInstance("pkcs12");
            ks.load(new FileInputStream(StaticField.CLIENT_KEY_STORE), StaticField.CLIENT_KEY_STORE_PASSWORD.toCharArray());
        }
        if (s.equals("jks")||s.equals("keystore")){
            ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(StaticField.CLIENT_KEY_STORE), StaticField.CLIENT_KEY_STORE_PASSWORD.toCharArray());
        }


        KeyStore jks = KeyStore.getInstance("JKS");
        jks.load(new FileInputStream(StaticField.TRUST_STORE), StaticField.TRUST_KEY_STORE_PASSWORD.toCharArray());


        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, StaticField.CLIENT_KEY_STORE_PASSWORD.toCharArray());

        TrustManagerFactory tmFact = TrustManagerFactory.getInstance("SunX509");
        tmFact.init(jks);

        sslcontextFactory.setTrustManagerFactory(tmFact);
        sslcontextFactory.setTrustManagerFactoryKeyStore(jks);         //可信证书
        sslcontextFactory.setKeyManagerFactoryKeyStore(ks);              //匹配证书
        sslcontextFactory.setKeyManagerFactoryKeyStorePassword(StaticField.CLIENT_KEY_STORE_PASSWORD);
        key = sslcontextFactory.newInstance();

        SocketFactory factory = key.getSocketFactory();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(clientIp,Integer.parseInt(clientPort));

        InetAddress inetAddress = inetSocketAddress.getAddress();
        Socket socket = factory.createSocket(remoteIp, remotePort, inetAddress,Integer.parseInt(clientPort));
        return socket;
    }


}
