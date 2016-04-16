package com.hzih.itp.auth.servlet;

import com.hzih.itp.auth.domain.ClientUser;
import com.hzih.itp.auth.domain.ServerUser;
import com.hzih.itp.auth.service.ClientUserService;
import com.hzih.itp.auth.service.LogService;
import com.hzih.itp.auth.utils.JSONUtils;
import com.hzih.itp.auth.utils.SpringContextUtil;
import com.hzih.itp.auth.utils.StaticField;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-6
 * Time: 下午5:01
 * To change this template use File | Settings | File Templates.
 */
public class RZClient extends Thread {
    static BufferedReader in;
    static PrintWriter out;
    static Socket s;

    /*
    *构造函数,获得socket连接,初始化in和out对象
    */

    public RZClient(Socket socket)
    {
        try
        {
            s=socket;
            in=new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));
            out=new PrintWriter(s.getOutputStream(),true);

            start();   //开新线程执行run方法

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
            String json = null;
            json = check(msg);
            System.out.println(json);
            out.println(json);
            in.close();
            out.close();
            s.close();

        }catch(Exception e)
        {
            System.out.println(e);
        }
    }

    /**
     * 对客户端进行认证
     * @return
     */
    private String check(String msg){
        JSONObject json_user = JSONUtils.toJSONObject(msg);
        String command = json_user.get("command").toString();
        String remoteIp = s.getInetAddress().getHostAddress();
        String json = null;
        if(command.equals(StaticField.CONNECT) || command.equals(StaticField.KEEPALIVE)){
            String clientUser = json_user.get("clientUser").toString();
            String clientPwd = json_user.get("clientPwd").toString();
            ClientUserService clientUserService = (ClientUserService) SpringContextUtil.getBean("clientUserService");
            LogService logService = (LogService)SpringContextUtil.getBean("logService");
            if(clientUserService != null && logService != null){
                if(clientUser != null && clientUser.length() > 0 && clientPwd != null && clientPwd.length() > 0){
                    json = clientUserService.checkByNamePwd(clientUser,clientPwd);
                    if(json.indexOf("成功") > -1){
                        //认证成功
                        if(command.equals(StaticField.CONNECT)){
                            logService.newSysLog("INFO","认证模式","认证成功","在 " + remoteIp + " 以" + clientUser + " 身份认证成功");
                        }
                    }else{
                        json = "{success:false,msg:'" + json + "'}";
                        logService.newSysLog("ERROR","认证模式","黑客入侵","在 " + remoteIp + " 以" + clientUser + " 身份认证失败");
                    }

                }else{
                    System.out.println("用户名或密码为空. . .");
                    json = "{success:false,msg:'用户名或密码不能为空'}";
                    logService.newSysLog("ERROR","认证模式","黑客入侵","在 " + remoteIp + " 以" + clientUser + " 身份认证失败");
                }

            }else {
                System.out.println("获取service错误. . .");
                json = "{success:false,msg:'服务端认证失败'}";

            }
        }else if(command.equals(StaticField.SERVER)){
            String clientId = json_user.get("clientId").toString();
            if(clientId != null && clientId.length() > 0){
                ClientUser client = Service.connectClients.get(Integer.parseInt(clientId));
                client.setDisconnectTime(new Date());
                if(client != null && client.getServers() != null){
                    Set<ServerUser> servers =  client.getServers();
                    json = "{success:true,count:" + servers.size() + ",rows:[";
                    for(ServerUser serverUser:servers){
                        json += "{serverId:" + serverUser.getId() +
                                ",serverName:'" + checkValue(serverUser.getServerName()) + "'" +
                                ",serverType:" + serverUser.getServerType()  +
                                ",ipPort:'" + checkValue(serverUser.getIpPort()) +  "'" +
                                ",serverUser:'" + checkValue(serverUser.getServerUser()) + "'" +
                                ",serverPwd:'" + checkValue(serverUser.getServerPwd()) + "'" +
                                ",permission:'" + serverUser.getPermission() + "'" +
                                ",dir:'" + checkValue(serverUser.getDir()) + "'},";
                    }
                    json += "]}";
                }
            }else {
                json = "{success:false,msg:'请重新认证!'}";
            }
        }else if(command.equals(StaticField.DISCONNECT)){
            String clientId = json_user.get("clientId").toString();
            if(clientId != null && clientId.length() > 0){
                ClientUser client = Service.connectClients.get(Integer.parseInt(clientId));
                client.setDisconnectTime(new Date());
                ClientUserService clientUserService = (ClientUserService) SpringContextUtil.getBean("clientUserService");
                clientUserService.modify(client);
                Service.connectClients.remove(client.getId());
                json = "{success:true,msg:'取消认证成功!'}";
            }
        }
        return json;

    }


    private String checkValue(String json) {
        if(json == null){
            return "";
        }else {
            return json;
        }
    }

    private String checkDate(Date date){
        if(date == null){
            return "";
        }
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(date);
    }
}
