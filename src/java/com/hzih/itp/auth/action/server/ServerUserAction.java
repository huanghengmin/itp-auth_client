package com.hzih.itp.auth.action.server;

import com.hzih.itp.auth.action.ActionBase;
import com.hzih.itp.auth.domain.ClientUser;
import com.hzih.itp.auth.domain.ServerUser;
import com.hzih.itp.auth.service.ClientUserService;
import com.hzih.itp.auth.service.ServerUserService;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-4
 * Time: 下午4:27
 * To change this template use File | Settings | File Templates.
 */
public class ServerUserAction extends ActionSupport {
    private ServerUserService serverUserService;
    private ClientUserService clientUserService;
    private int start;
    private int limit;
    private ServerUser serverUser;

    public ServerUserService getServerUserService() {
        return serverUserService;
    }

    public void setServerUserService(ServerUserService serverUserService) {
        this.serverUserService = serverUserService;
    }


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

    public ServerUser getServerUser() {
        return serverUser;
    }

    public void setServerUser(ServerUser serverUser) {
        this.serverUser = serverUser;
    }

    /**
     * 获取所有的服务
     * @return
     * @throws Exception
     */
    public String select() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = null;
        try{
            String serverName = request.getParameter("serverName");
            String serverType = request.getParameter("serverType");
            String serverUser = request.getParameter("serverUser");
            String dir = request.getParameter("dir");
            String permission = request.getParameter("permission");
            json = serverUserService.findByPage(serverName,serverType,serverUser,dir,permission,start/limit+1,limit);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }

    /**
     * 获取服务下所有的账号
     * @return
     * @throws Exception
     */
    public String selectUsers() throws Exception{

        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'查询失败!'}";
        try{
            String serverName = request.getParameter("serverName");
            if(serverName != null && serverName.length() > 0){
                json = serverUserService.findByServerName(serverName,start/limit+1,limit);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }

    /**
     * 获取服务下账号对应的认证账号
     * @return
     * @throws Exception
     */
    public String selectClients() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'查询失败!'}";
        try{
            String id = request.getParameter("id");
            if(id != null && id.length() > 0){
                json = serverUserService.findByServerUser(Integer.parseInt(id));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }

    /**
     * 获取能够添加的认证账号
     * @return
     * @throws Exception
     */
    public String selectClientName() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'查询失败!'}";
        try{
            String id = request.getParameter("id");
            if(id != null && id.length() > 0){
                json = serverUserService.findClientName(Integer.parseInt(id));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;

    }

    /**
     * 获取所有的服务
     * @return
     * @throws Exception
     */
    public String selectServerName() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'查询失败!'}";
        try{
            json = serverUserService.findServerName();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }

    /**
     * 获取服务下的所有账号
     * @return
     * @throws Exception
     */
    public String selectServerUser() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'查询失败!'}";
        try{
            String serverName = request.getParameter("serverName");
            if(serverName != null && serverName.length() > 0){
                json = serverUserService.findServerName();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;

    }

    public String checkServerName() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'验证失败!'}";
        try{
            String serverName = request.getParameter("serverName");
            boolean flag = serverUserService.checkServerName(serverName);
            if(flag){
                json = "{success:true,msg:'true'}";
            }else {
                json = "{success:true,msg:'该服务名已存在'}";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;
    }

    public String checkServerUser() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'验证失败!'}";
        try{
            String serverUser = request.getParameter("userName");
            String serverName = request.getParameter("serverName");
            String id = request.getParameter("id");
            if((serverName == null || serverName.length() <= 0) &&(id != null && id.length() > 0)){
                ServerUser server = serverUserService.findById(Integer.parseInt(id));
                serverName = server.getServerName();
            }
            boolean flag = true;
            if(serverName != null){
                 flag= serverUserService.checkServerUser(serverName,serverUser);
            }
            if(flag){
                json = "{success:true,msg:'true'}";
            }else {
                json = "{success:true,msg:'该账号已存在'}";
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
        String json = "{success:true,msg:'新增失败!'}";
        try{
            serverUser.setCreateTime(new Date());
            json = serverUserService.add(serverUser);
            if(json != null){
                json = "{success:true,msg:'" + json + "'}";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        serverUser = null;
        return null;
    }

    public String insertUser() throws  Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'新增失败!'}";
        try{
            String serverId = request.getParameter("id");
            if(serverId != null && serverId.length() > 0){
                ServerUser server = serverUserService.findById(Integer.parseInt(serverId));
                serverUser.setServerName(server.getServerName());
                serverUser.setServerType(server.getServerType());
                serverUser.setIpPort(server.getIpPort());
                serverUser.setCreateTime(new Date());
                json = serverUserService.add(serverUser);
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
        serverUser = null;
        return null;
    }

    public String insertClient() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'新增失败!'}";
        try{
            String serverId = request.getParameter("id");
            String clientId = request.getParameter("clientId");
            if(serverId != null && serverId.length() > 0 && clientId != null && clientId.length() > 0){
                ServerUser server = serverUserService.findById(Integer.parseInt(serverId));
                ClientUser client = clientUserService.findById(Integer.parseInt(clientId));
                Set<ClientUser> clientUsers = server.getClients();
                boolean flag = true;
                for(ClientUser clientUser:clientUsers){
                    if(clientUser.getId() == client.getId()){
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    server.getClients().add(client);
                    json = serverUserService.modify(server);
                    if(json != null){
                        json = "{success:true,msg:'" + json + "'}";
                    }
                }else {
                    json = "{success:true,msg:'记录已存在!'}";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        serverUser = null;
        return null;
    }

    public String update() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'修改失败!'}";
        try{
            String id = request.getParameter("id");
            if(id != null && id.length() > 0 && serverUser != null){
                serverUser.setId(Integer.parseInt(id));
                ServerUser server = serverUserService.findById(Integer.parseInt(id));
                if(serverUser.getServerName() != null && serverUser.getServerName().length() > 0){
                    server.setServerUser(serverUser.getServerUser());
                }
                server.setServerType(serverUser.getServerType());
                if(serverUser.getIpPort() != null && serverUser.getIpPort().length() > 0){
                    server.setIpPort(serverUser.getIpPort());
                }
                json = serverUserService.modify(server);
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
        serverUser = null;
        return null;
    }

    public String updateUser() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'修改失败!'}";
        try{
            String id = request.getParameter("id");
            if(id != null && id.length() > 0 && serverUser != null){
                ServerUser server = serverUserService.findById(Integer.parseInt(id));
                if(serverUser.getServerUser() != null && serverUser.getServerUser().length() > 0){
                    server.setServerUser(serverUser.getServerUser());
                }
                if(serverUser.getServerPwd() != null && serverUser.getServerPwd().length() > 0 && !(serverUser.getServerPwd().indexOf("请输入") > -1)){
                    server.setServerPwd(serverUser.getServerPwd());
                }
                server.setPermission(serverUser.getPermission());
                if(serverUser.getDir() != null && serverUser.getDir().length() > 0){
                    server.setDir(serverUser.getDir());
                }
                json = serverUserService.modify(server);
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
        serverUser = null;
        return null;
    }

    public String delete() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'删除失败!'}";
        try{
            String id = request.getParameter("id");
            if(id != null && id.length() > 0){
                json = serverUserService.remove(Integer.parseInt(id));
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

    public String deleteClient() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'删除失败!'}";
        try{
            String serverId = request.getParameter("id");
            String clientId = request.getParameter("clientId");
            if(serverId != null && serverId.length() > 0 && clientId != null && clientId.length() > 0){
                ServerUser server = serverUserService.findById(Integer.parseInt(serverId));
                ClientUser client = new ClientUser();
                client.setId(Integer.parseInt(clientId));
                Set<ClientUser> clientUsers = server.getClients();
                boolean flag = true;
                for(ClientUser clientUser:clientUsers){
                    if(clientUser.getId() == client.getId()){
                        flag = true;
                        client = clientUser;
                        break;
                    }
                }
                if(flag){
                    server.getClients().remove(client);
                    json = serverUserService.modify(server);
                }else{
                    json = "{success:true,msg:'记录不存在!'}";
                }
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
        serverUser = null;
        return null;
    }

    public String checkIpport() throws Exception{

        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ActionBase actionBase = new ActionBase();
        String json = "{success:true,msg:'验证失败!'}";
        try{
            String ipport = request.getParameter("ipport");
            boolean flag = serverUserService.checkIpport(ipport);
            if(flag){
                json = "{success:true,msg:'true'}";
            }else {
                json = "{success:true,msg:'该IP端口已被占用'}";
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
        String json = "{success:true,msg:'验证失败!'}";
        boolean flag = true;
        try{
            String password = request.getParameter("password");
            String id = request.getParameter("id");
            if(id != null && id.length() > 0 && password != null && password.length() > 0){
                ServerUser server = serverUserService.findById(Integer.parseInt(id));
                if(server != null && server.getServerPwd().equals(password)){
                    flag = true;
                }
            }
            if(flag){
                json = "{success:true,msg:'true'}";
            }else {
                json = "{success:true,msg:'密码错误!'}";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(json != null){
            actionBase.actionEnd(response,json);
        }
        return null;

    }
}
