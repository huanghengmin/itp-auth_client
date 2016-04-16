package com.hzih.itp.auth.action.client;

import com.hzih.itp.auth.action.ActionBase;
import com.hzih.itp.auth.servlet.KeepAliveService;
import com.hzih.itp.auth.utils.JSONUtils;
import com.hzih.itp.auth.utils.StaticField;
import com.opensymphony.xwork2.ActionSupport;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.mina.filter.ssl.SslContextFactory;
import org.apache.struts2.ServletActionContext;
import sun.misc.BASE64Encoder;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-6
 * Time: 下午1:41
 * To change this template use File | Settings | File Templates.
 */
public class ClientAction extends ActionSupport {
    private static Logger logger = Logger.getLogger(ClientAction.class);

    public static int clientId = -1;
    public static boolean connect = false;



    public static Properties getProperties() {
        Properties properties = null;
        try {
            //读取properties文件,获得properties对象
            InputStream in = new BufferedInputStream(ClientAction.class.getResourceAsStream("/config.properties"));
            properties = new Properties();
            properties.load(in);
        } catch (Exception ex) {
            return null;
        }
        return properties;
    }

    private String checkValue(String json ){
        if(json == null){
            return "";
        }else {
            return json;
        }
    }

    public String getClient() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = null;
        Properties properties = null;
        try {
            InputStream in = new FileInputStream(new File(StaticField.CLIENT_CONFIG));
            properties = new Properties();
            properties.load(in);
            if(properties!=null){
                String clientUser = properties.getProperty("clientUser");
                String clientPwd = properties.getProperty("clientPwd");
                String serverIp = properties.getProperty("serverIp");
                String serverPort = properties.getProperty("serverPort");
                String clientIp = properties.getProperty("clientIp");
                String clientPort = properties.getProperty("clientPort");
                if(clientUser != null && clientPwd != null){
                    json = "{success:true,count:1,rows:[{serverIp:'" +checkValue(serverIp)+ "',serverPort:'" + checkValue(serverPort)+
                            "',clientUser:'" + checkValue(clientUser) + "',clientPwd:'" + checkValue(clientPwd) +
                            "',clientIp:'" + checkValue(clientIp) + "',clientPort:'" + checkValue(clientPort) +
                            "',connect:" +connect+ "}]}";
                }
            }
            in.close();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("查询客户端配置信息失败",e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }

    public String setClient() throws IOException {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String clientUser = request.getParameter("clientUser");
        String clientPwd = request.getParameter("clientPwd");
        String serverIp = request.getParameter("serverIp");
        String serverPort = request.getParameter("serverPort");
        String clientIp = request.getParameter("clientIp");
        String clientPort = request.getParameter("clientPort");
        String json = "{success:false}";
        Properties properties = null;
        try {
            if(clientUser != null && clientUser.length() > 0 && clientPwd != null && clientPwd.length() > 0){
                //读取properties文件,获得properties对象
                InputStream in = new FileInputStream(new File(StaticField.CLIENT_CONFIG));
                properties = new Properties();
                properties.load(in);
                properties.setProperty("clientUser",clientUser);
                properties.setProperty("clientPwd",clientPwd);
                properties.setProperty("serverIp",serverIp);
                properties.setProperty("serverPort",serverPort);
                properties.setProperty("clientIp",clientIp);
                properties.setProperty("clientPort",clientPort);
                OutputStream out = new FileOutputStream(StaticField.CLIENT_CONFIG);
                properties.store(out,"update time " + new Date());
                out.close();
                in.close();
                json = "{success:true}";
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("设置客户端配置信息失败",e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }

    //认证
    public String connect() throws IOException {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = null;
        Properties properties = null;
        try {
            InputStream in = new FileInputStream(new File(StaticField.CLIENT_CONFIG));
            properties = new Properties();
            properties.load(in);
            if(properties!=null){
                String clientUser = properties.getProperty("clientUser");
                String clientPwd = properties.getProperty("clientPwd");
                String serverIp = properties.getProperty("serverIp");
                String serverPort = properties.getProperty("serverPort");
                String clientIp = properties.getProperty("clientIp");
                String clientPort = properties.getProperty("clientPort");

                if(clientUser != null && clientPwd != null && serverIp != null && serverPort != null && clientIp != null && clientPort != null){
                    json = checkPermission(serverIp,serverPort,clientUser,clientPwd,clientIp,clientPort);
                }else{
                    json = "{success:false,msg:'存在非法配置项,请修改配置项,并保存!'}";
                }
            }
            in.close();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("认证失败",e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }

    /**
     * 三台设备之间自动认证
     */
    public  String authconnect(){
        Properties properties = null;
        String json = null;
        try {
            InputStream in = new FileInputStream(new File(StaticField.CLIENT_CONFIG));
            properties = new Properties();
            properties.load(in);
            if(properties!=null){
                String clientUser = properties.getProperty("clientUser");
                String clientPwd = properties.getProperty("clientPwd");
                String serverIp = properties.getProperty("serverIp");
                String serverPort = properties.getProperty("serverPort");
                String clientIp = properties.getProperty("clientIp");
                String clientPort = properties.getProperty("clientPort");

                if(clientUser != null && clientPwd != null && serverIp != null && serverPort != null && clientIp != null && clientPort != null){
                    json = checkPermission(serverIp,serverPort,clientUser,clientPwd,clientIp,clientPort);
                }else{
                    json = "{success:false,msg:'存在非法配置项,请修改配置项,并保存!'}";
                }
            }
            in.close();
        }catch (Exception e){
            logger.error("认证失败",e);
        }
        return json;
    }


    //取消认证
    public String disconnect() throws IOException {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = null;
        Properties properties = null;
        try {
            InputStream in = new FileInputStream(new File(StaticField.CLIENT_CONFIG));
            properties = new Properties();
            properties.load(in);
            if(properties!=null){
                String serverIp = properties.getProperty("serverIp");
                String serverPort = properties.getProperty("serverPort");
                String clientIp = properties.getProperty("clientIp");
                String clientPort = properties.getProperty("clientPort");
                if( serverIp != null && serverPort != null && clientIp != null && clientPort != null){
                    json = client_disconnect(serverIp,serverPort,clientIp,clientPort);
                }else{
                    json = "{success:false,msg:'存在非法配置项,请修改配置项,并保存!'}";
                }
            }
            in.close();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("取消认证失败",e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        clientId = -1;
        connect = false;
        return null;
    }

    //获取所有服务
    public String selectServers() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = null;
        Properties properties = null;
        try {
            if(clientId != -1){
                InputStream in = new FileInputStream(new File(StaticField.CLIENT_CONFIG));
                properties = new Properties();
                properties.load(in);
                if(properties!=null){
                    String clientUser = properties.getProperty("clientUser");
                    String clientPwd = properties.getProperty("clientPwd");
                    String serverIp = properties.getProperty("serverIp");
                    String serverPort = properties.getProperty("serverPort");
                    String clientIp = properties.getProperty("clientIp");
                    String clientPort = properties.getProperty("clientPort");
                    if(clientUser != null && clientPwd != null && serverIp != null && serverPort != null){
                        json = getServers(serverIp,serverPort,clientIp,clientPort);
                    }else{
                        json = "{success:false,msg:'存在非法配置项,请修改配置项,并保存!'}";
                    }
                }
                in.close();
            }else {
                json = "{success:true,count:0,rows:[]}";
            }

        }catch (Exception e){
            e.printStackTrace();
            logger.error("获得所有服务失败",e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }

    /**
     * 获取服务密码
     * @return
     * @throws Exception
     */
    public String getPwd() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = null;
        Properties properties = null;
        try {
            if(clientId != -1){
                String serverId = request.getParameter("serverId");
                InputStream in = new FileInputStream(new File(StaticField.CLIENT_CONFIG));
                properties = new Properties();
                properties.load(in);
                if(properties!=null){
                    String clientUser = properties.getProperty("clientUser");
                    String clientPwd = request.getParameter("pwd");  //由前台输入
                    String serverIp = properties.getProperty("serverIp");
                    String serverPort = properties.getProperty("serverPort");
                    String clientIp = properties.getProperty("clientIp");
                    String clientPort = properties.getProperty("clientPort");
                    if(clientUser != null && clientPwd != null && serverIp != null && serverPort != null && clientIp != null && clientPort != null){
                        json = getServerPwd(serverId,clientUser,clientPwd,serverIp,serverPort,clientIp,clientPort);
                    }else{
                        json = "{success:false,msg:'存在非法配置项,请修改配置项,并保存!'}";
                    }
                }
                in.close();
            }else {
                json = "{success:true,count:0,rows:[]}";
            }

        }catch (Exception e){
            e.printStackTrace();
            logger.error("获得服务密码失败",e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }


    /**
     * 获取服务权限路径
     * @return
     * @throws Exception
     */
    public String getUrl() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = null;
        Properties properties = null;
        try {
            if(clientId != -1){
                String serverName = request.getParameter("serverName");
                InputStream in = new FileInputStream(new File(StaticField.CLIENT_CONFIG));
                properties = new Properties();
                properties.load(in);
                if(properties!=null){
                    String clientUser = properties.getProperty("clientUser");
                    String clientPwd = properties.getProperty("clientPwd");
                    String serverIp = properties.getProperty("serverIp");
                    String serverPort = properties.getProperty("serverPort");
                    String clientIp = properties.getProperty("clientIp");
                    String clientPort = properties.getProperty("clientPort");
                    if(clientUser != null && clientPwd != null && serverIp != null && serverPort != null){
                        json = getServerDir(serverName,clientUser,clientPwd,serverIp,serverPort,clientIp,clientPort);
                        json = "{success:true,msg:'" +json + "'}";
                    }else{
                        json = "{success:false,msg:'存在非法配置项,请修改配置项,并保存!'}";
                    }
                }
                in.close();
            }else {
                json = "{success:true,count:0,rows:[]}";
            }

        }catch (Exception e){
            e.printStackTrace();
            logger.error("获得服务权限路径失败",e);
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }


    /**
     * 建立通道:设置权限中的 local_root,设置iptables规则
     * @return
     * @throws Exception
     */
    public String makeRule() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = null;
        Properties properties = null;
        try {
            if(clientId != -1){
                String serverName = request.getParameter("serverName");
                InputStream in = new FileInputStream(new File(StaticField.CLIENT_CONFIG));
                properties = new Properties();
                properties.load(in);
                if(properties!=null){
                    String clientUser = properties.getProperty("clientUser");
                    String clientPwd = properties.getProperty("clientPwd");
                    String serverIp = properties.getProperty("serverIp");
                    String serverPort = properties.getProperty("serverPort");
                    String clientIp = properties.getProperty("clientIp");
                    String clientPort = properties.getProperty("clientPort");
                    if(clientUser != null && clientPwd != null && serverIp != null && serverPort != null && clientIp != null && clientPort != null){
                        json = getServerDir(serverName,clientUser,clientPwd,serverIp,serverPort,clientIp,clientPort);
                        json = "{success:true,msg:'" +json + "'}";
                    }else{
                        json = "{success:false,msg:'存在非法配置项,请修改配置项,并保存!'}";
                    }
                }
                in.close();
            }else {
                json = "{success:true,count:0,rows:[]}";
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }


    public String checkState() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = null;
        if(clientId == -1){
            json = "{success:true,msg:'false'}";
        }else{
            json = "{success:true,msg:'true'}";
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }





    private Socket getSocket(String remoteIp,int remotePort,String localIp,int localPort) {
        int x= StaticField.CLIENT_KEY_STORE.lastIndexOf(".") ;
        String s= StaticField.CLIENT_KEY_STORE.substring(x+1) ;

        SSLContext key = null;

        SslContextFactory sslcontextFactory = new SslContextFactory();
        sslcontextFactory.setProtocol("TLS");
        KeyStore ks = null;
        Socket socket = null;
        try{
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
            InetSocketAddress inetSocketAddress = new InetSocketAddress(localIp,localPort);

            InetAddress inetAddress = inetSocketAddress.getAddress();
            socket = factory.createSocket(remoteIp, remotePort, inetAddress,localPort );
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (CertificateException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnknownHostException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (KeyStoreException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return socket;
    }


    private String checkPermission(String serverIp,String serverPort,String clientUser,String clientPwd,String clientIp,String clientPort) {
        String msg = null,json = null;
        try
        {


//            System.setProperty("javax.net.ssl.trustStore", StaticField.CLIENT_KEY_STORE);

            Socket socket = getSocket(serverIp,Integer.parseInt(serverPort),clientIp,Integer.parseInt(clientPort));
            PrintWriter out=new PrintWriter(socket.getOutputStream(),true);

            String userName = new String(new BASE64Encoder().encode(clientUser.getBytes()));

            out.println("{\"command\":\"" + StaticField.CONNECT + "\",\"clientUser\":\"" + userName + "\",\"clientPwd\":\"" + clientPwd + "\"}");
            out.flush();
            BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream(),"GBK"));
            msg=in.readLine();
            System.out.println(msg);
            json = getMsg(StaticField.CONNECT,msg);
            in.close();
            out.close();
            socket.close();
        }catch(Exception e){
            System.out.println(e);
            logger.error("用户名密码认证失败",e);
            json = "{success:false,msg:'无法连接到服务器'}";
        }
        return json;
    }

    private String client_disconnect(String serverIp,String serverPort,String clientIp,String clientPort) throws Exception{
        String msg = null,json = null;
        try
        {

//            System.setProperty("javax.net.ssl.trustStore", StaticField.CLIENT_KEY_STORE);

            Socket s = getSocket(serverIp,Integer.parseInt(serverPort),clientIp,Integer.parseInt(clientPort));

            PrintWriter out=new PrintWriter(s.getOutputStream(),true);

            out.println("{\"command\":\"" + StaticField.DISCONNECT + "\",\"clientId\":" + clientId + "}");
            out.flush();
            BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream(),"GBK"));
            msg=in.readLine();
            System.out.println(msg);
            json = msg;
            in.close();
            out.close();
            s.close();
            clientId = -1;
            connect = false;
        }catch(Exception e){
            System.out.println(e);
            logger.error("取消认证失败",e);
            json = "{success:false,msg:'无法连接到服务器'}";
        }
        return json;
    }

    private String getServers(String serverIp,String serverPort,String clientIp,String clientPort) throws Exception{
        String msg = null,json = null;
        try
        {

//            System.setProperty("javax.net.ssl.trustStore", StaticField.CLIENT_KEY_STORE);

            Socket s = getSocket(serverIp,Integer.parseInt(serverPort),clientIp,Integer.parseInt(clientPort));

            PrintWriter out=new PrintWriter(s.getOutputStream(),true);

            out.println("{\"command\":\"" + StaticField.SERVER + "\",\"clientId\":" + clientId + "}");
            out.flush();
            BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream(),"GBK"));
            msg=in.readLine();
            System.out.println(msg);
            json = getMsg(StaticField.SERVER,msg);
            in.close();
            out.close();
            s.close();
        }catch(Exception e){
//            System.out.println(e);
            logger.error("获取服务失败",e.getCause());
            json = "{success:false,msg:'无法连接到服务器'}";
        }
        return json;

    }

    private String getServerPwd(String serverId,String clientUser,String clientPwd,String serverIp,String serverPort,String clientIp,String clientPort){
        String msg = null,json = null;
        try
        {

//            System.setProperty("javax.net.ssl.trustStore", StaticField.CLIENT_KEY_STORE);

            Socket s = getSocket(serverIp,Integer.parseInt(serverPort),clientIp,Integer.parseInt(clientPort));

            PrintWriter out=new PrintWriter(s.getOutputStream(),true);
            String userName = new String(new BASE64Encoder().encode(clientUser.getBytes()));

            out.println("{\"command\":\"" + StaticField.PASSWORD + "\",\"clientId\":" + clientId + ",\"serverId\":" + Integer.parseInt(serverId) +",\"clientUser\":\"" + userName + "\",\"clientPwd\":\"" + clientPwd  + "\"}");
            out.flush();
            BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream(),"GBK"));
            msg=in.readLine();
            System.out.println(msg);
            json = msg;
            in.close();
            out.close();
            s.close();
        }catch(Exception e){
            System.out.println(e);
            logger.error("获取服务密码失败",e);
            json = "{success:false,msg:'无法连接到服务器'}";
        }
        return json;
    }


    private String getServerDir(String serverName,String clientUser,String clientPwd,String serverIp,String serverPort,String clientIp,String clientPort){
        String msg = null,json = null;
        try
        {

//            System.setProperty("javax.net.ssl.trustStore", StaticField.CLIENT_KEY_STORE);

            Socket s = getSocket(serverIp,Integer.parseInt(serverPort),clientIp,Integer.parseInt(clientPort));

            PrintWriter out=new PrintWriter(s.getOutputStream(),true);
            String userName = new String(new BASE64Encoder().encode(clientUser.getBytes()));

            out.println("{\"command\":\"" + StaticField.DIR + "\",\"serverUser\":\"" + serverName +"\",\"clientUser\":\"" + userName + "\",\"clientPwd\":\"" + clientPwd  + "\"}");
            out.flush();
            BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream(),"GBK"));
            msg=in.readLine();
            System.out.println(msg);
            json = msg;
            in.close();
            out.close();
            s.close();
        }catch(Exception e){
            System.out.println(e);
            logger.error("获取服务路径失败",e);
            json = "{success:false,msg:'无法连接到服务器'}";
        }
        return json;
    }


    /*private String getRule(String serverId,String serverName,String clientUser,String clientPwd,String serverIp,String serverPort){
        String msg = null,json = null;
        try
        {

            System.setProperty("javax.net.ssl.trustStore", StaticField.CLIENT_KEY_STORE);

            Socket s = getSocket(serverIp,Integer.parseInt(serverPort));

            PrintWriter out=new PrintWriter(s.getOutputStream(),true);

            out.println("{\"command\":\"" + StaticField.RULE + "\"," +
                    "\"serverId\":" + serverId +"," +
                    "\"serverUser\":\"" + serverName +"\"," +
                    "\"clientUser\":\"" + clientUser + "\"," +
                    "\"clientPwd\":\"" + clientPwd  + "\"}");
            out.flush();
            BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream(),"GBK"));
            msg=in.readLine();
            System.out.println(msg);
            json = msg;
            in.close();
            out.close();
            s.close();
        }catch(Exception e){
            System.out.println(e);
            json = "{success:false,msg:'无法连接到服务器'}";
        }
        return json;
    }*/

    private String checkPermission_old(String serverIp,String serverPort,String clientUser,String clientPwd) {
        String msg = null,json = null;
        try
        {

//            System.setProperty("javax.net.ssl.trustStore", StaticField.CLIENT_KEY_STORE);
//            System.setProperty("javax.net.debug", "ssl,handshake");
            SSLSocketFactory factory=(SSLSocketFactory)SSLSocketFactory.getDefault();

            Socket s=factory.createSocket(serverIp,Integer.parseInt(serverPort));

            PrintWriter out=new PrintWriter(s.getOutputStream(),true);
            String userName = new String(new BASE64Encoder().encode(clientUser.getBytes()));

            out.println("{\"command\":\"" + StaticField.CONNECT + "\",\"clientUser\":\"" + userName + "\",\"clientPwd\"=\"" + clientPwd + "\"}");
            out.flush();
            BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream(),"GBK"));
            msg=in.readLine();
            System.out.println(msg);
            json = getMsg(StaticField.CONNECT,msg);
            in.close();
            out.close();
            s.close();
        }catch(Exception e){
            System.out.println(e);
            json = "{success:false,msg:'无法连接到服务器'}";
        }
        return json;
    }

    private String client_disconnect_old(String serverIp,String serverPort) throws Exception{
        String msg = null,json = null;
        try
        {

//            System.setProperty("javax.net.ssl.trustStore", StaticField.CLIENT_KEY_STORE);
//            System.setProperty("javax.net.debug", "ssl,handshake");
            SSLSocketFactory factory=(SSLSocketFactory)SSLSocketFactory.getDefault();

            Socket s=factory.createSocket(serverIp,Integer.parseInt(serverPort));

            PrintWriter out=new PrintWriter(s.getOutputStream(),true);

            out.println("{\"command\":\"" + StaticField.DISCONNECT + "\",\"clientId\":" + clientId + "}");
            out.flush();
            BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream(),"GBK"));
            msg=in.readLine();
            System.out.println(msg);
            json = msg;
            in.close();
            out.close();
            s.close();
            clientId = -1;
            connect = false;
        }catch(Exception e){
            System.out.println(e);
            json = "{success:false,msg:'无法连接到服务器'}";
        }
        return json;
    }

    private String getServers_old(String serverIp,String serverPort) throws Exception{
        String msg = null,json = null;
        try
        {

//            System.setProperty("javax.net.ssl.trustStore", StaticField.CLIENT_KEY_STORE);
//            System.setProperty("javax.net.debug", "ssl,handshake");
            SSLSocketFactory factory=(SSLSocketFactory)SSLSocketFactory.getDefault();

            Socket s=factory.createSocket(serverIp,Integer.parseInt(serverPort));

            PrintWriter out=new PrintWriter(s.getOutputStream(),true);

            out.println("{\"command\":\"" + StaticField.SERVER + "\",\"clientId\":" + clientId + "}");
            out.flush();
            BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream(),"GBK"));
            msg=in.readLine();
            System.out.println(msg);
            json = getMsg(StaticField.SERVER,msg);
            in.close();
            out.close();
            s.close();
        }catch(Exception e){
            System.out.println(e);
            json = "{success:false,msg:'无法连接到服务器'}";
        }
        return json;

    }

    private String getMsg(String command,String msg){
        String json = null;
        if(msg != null){
            JSONObject json_msg = JSONUtils.toJSONObject(msg);
            if(command.equals(StaticField.CONNECT)){
                String success = null,result = null;
                if(json_msg.containsKey("success")){
                    success = json_msg.get("success").toString();
                }
                if(json_msg.containsKey("msg")){
                    result = json_msg.get("msg").toString();
                }
                if(success.indexOf("true") > -1 && json_msg.containsKey("clientId")){
                    ClientAction.clientId = Integer.parseInt(json_msg.get("clientId").toString());
                    ClientAction.connect = true;
                    //发送心跳线程
                    KeepAliveService keepAliveService = new KeepAliveService();
                    keepAliveService.init();
                    new Thread(keepAliveService).start();
                }
                json = "{success:" + success + ",msg:'" + result + "'}";
            }else if(command.equals(StaticField.SERVER)){
                String success = null;
                if(json_msg.containsKey("success")){
                    success = json_msg.get("success").toString();
                }
                if(success.indexOf("true") > -1){
                    json = msg;
                }
            }
        }
        return json;
    }


    /**
     * 获取当前设备的所有IP
     * @return
     * @throws Exception
     */
    public String getIps() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:false,totoal:0,rows:[]}";
        json = getIpFromNetwork();
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;


    }

    private String getIpFromNetwork(){
        // 根据网卡取本机配置的IP
        Enumeration<NetworkInterface> netInterfaces = null;
        String json = "{success:true,rows:[";
        int count = 0;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    String ip = ips.nextElement().getHostAddress();
                    if(ip.indexOf("127.0.0.1") > -1){
                        continue;
                    }
                    json += "{ip:'" + ip + "'},";
                    count ++;
                }
            }
            json += "],count:" + count + "}";
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取本机IP失败",e);
        }
        return json;
    }


}
