package com.hzih.itp.auth.action.server;

import com.hzih.itp.auth.action.ActionBase;
import com.hzih.itp.auth.domain.ClientUser;
import com.hzih.itp.auth.service.ClientUserService;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-4
 * Time: 上午11:18
 * To change this template use File | Settings | File Templates.
 */
public class ClientUserAction extends ActionSupport {
    private ClientUserService clientUserService;
    private int start;
    private int limit;
    private ClientUser clientUser;

    public ClientUserService getClientUserService() {
        return clientUserService;
    }

    public void setClientUserService(ClientUserService clientUserService) {
        this.clientUserService = clientUserService;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public ClientUser getClientUser() {
        return clientUser;
    }

    public void setClientUser(ClientUser clientUser) {
        this.clientUser = clientUser;
    }

    public String select() throws Exception{
        HttpServletRequest request= ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = null;
        try{
            String userName = request.getParameter("userName");
            json = clientUserService.findByPage(userName,start/limit+1,limit);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }

    public String selectServers() throws Exception{
        HttpServletRequest request= ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:false,msg:'查询失败'}";
        try{
            String clientId = request.getParameter("clientId");
            if(clientId != null && clientId.length() > 0){
                json = clientUserService.findServers(Integer.parseInt(clientId));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }

    public String insert() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = null;
        try{
            clientUser.setCreateTime(new Date());
            json = clientUserService.add(clientUser);
            if(json != null){
                json = "{success:true,msg:'" + json + "'}";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        clientUser = null;
        return null;
    }

    public String update() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = null;
        try{
            String id = request.getParameter("id");
            if(id != null && id.length() > 0 && clientUser != null){
                ClientUser client = clientUserService.findById(Integer.parseInt(id));
                if(clientUser.getUserName() != null && client.getUserName().length() > 0){
                    client.setUserName(clientUser.getUserName());
                }
                if(clientUser.getUserPwd() != null && clientUser.getUserPwd().length() > 0){
                    client.setUserPwd(clientUser.getUserPwd());
                }
                if(clientUser.getIp() != null && clientUser.getIp().length() > 0){
                    client.setIp(clientUser.getIp());
                }
                if(clientUser.getMac() != null && clientUser.getMac().length() > 0){
                    client.setMac(clientUser.getMac());
                }
                json = clientUserService.modify(client);
                if(json != null){
                    json = "{success:true,msg:'" + json + "'}";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        clientUser = null;
        return null;
    }

    public String delete() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = null;
        try{
            String id = request.getParameter("id");
            if(id != null && id.length() > 0){
                json = clientUserService.remove(Integer.parseInt(id));
                if(json != null){
                    json ="{success:true,msg:'" + json + "'}";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }

    public String checkPwd() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = null;
        boolean flag = false;
        try{
            String id = request.getParameter("id");
            String pwd = request.getParameter("password");
            if(id != null && id.length() > 0){
                clientUser = null;
                clientUser = clientUserService.findById(Integer.parseInt(id));
                if(clientUser != null){
                    if(pwd != null && pwd.equals(clientUser.getUserPwd())){
                        flag = true;
                    }
                }
            }
            if(flag){
                json = "{success:true,msg:'" + flag + "'}";
            }else{
                json = "{success:true,msg:'密码错误!'}";
            }
        }catch (Exception e){
            e.printStackTrace();
            json = "{success:false,msg:'客户端不存在!'}";
        }

        if(json != null){
            actionBase.actionEnd(response,json);
        }
        clientUser = null;
        return null;
    }
}
