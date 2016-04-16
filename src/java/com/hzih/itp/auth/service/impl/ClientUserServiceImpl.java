package com.hzih.itp.auth.service.impl;

import cn.collin.commons.domain.PageResult;
import com.hzih.itp.auth.dao.ClientUserDao;
import com.hzih.itp.auth.domain.ClientUser;
import com.hzih.itp.auth.domain.ServerUser;
import com.hzih.itp.auth.service.ClientUserService;
import com.hzih.itp.auth.servlet.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-4
 * Time: 上午11:07
 * To change this template use File | Settings | File Templates.
 */
public class ClientUserServiceImpl implements ClientUserService {

    private ClientUserDao clientUserDao;

    public ClientUserDao getClientUserDao() {
        return clientUserDao;
    }

    public void setClientUserDao(ClientUserDao clientUserDao) {
        this.clientUserDao = clientUserDao;
    }

    @Override
    public String add(ClientUser clientUser) {
        String json = "新增失败!";
        try{
            clientUserDao.create(clientUser);
            json = "新增成功!";
        }catch (Exception e){
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public String modify(ClientUser clientUser) {
        String json = "修改失败!";
        try{
            clientUserDao.update(clientUser);
            json = "修改成功!";
        }catch (Exception e){
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public String remove(int userId) {
        String json = "删除失败!";
        try{
            clientUserDao.delete(userId);
            json = "删除成功!";
        }catch (Exception e){
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public String findByPage(String userName, int pageIndex, int limit) {
        PageResult ps = null;
        String json = null;
        try{
            ps = clientUserDao.findByPage(userName, pageIndex, limit);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if(ps != null){
            json = "{success:true,count:" + ps.getAllResultsAmount() + ",rows:[";
            List<ClientUser> clientUserList = ps.getResults();
            if(clientUserList != null && clientUserList.size() > 0){
                for(ClientUser clientUser:clientUserList){
                    json += "{id:" + clientUser.getId() +
                            ",userName:'" + checkValue(clientUser.getUserName()) + "'" +
                            ",ip:'" + checkValue(clientUser.getIp()) + "'" +
                            ",mac:'" + checkValue(clientUser.getMac()) + "'" +
                            ",createTime:'" + checkTime(clientUser.getCreateTime())+ "'" +
                            ",isconnect:" + (Service.connectClients.containsKey(clientUser.getId())?true:false) +
                            ",connectTime:'" + checkTime(clientUser.getConnectTime())+ "'},";
                }
            }
            json += "]}";
        }
        return json;
    }

    @Override
    public String findServers(int userId) {
        String json = null;
        int count = 0;
        ClientUser clientUser = null;
        clientUser = findById(userId);
        if(clientUser != null){
            Set<ServerUser> servers = clientUser.getServers();
            if(servers != null){
                json = "{success:true,rows:[";
                for(ServerUser serverUser:servers){
                    if(serverUser != null){
                        json += "{serverId:" + serverUser.getId() +
                                ",serverName:'" + checkValue(serverUser.getServerName()) + "'" +
                                ",serverType:" + serverUser.getServerType() +
                                ",ipPort:'" + checkValue(serverUser.getIpPort()) + "'" +
                                ",serverUser:'" + checkValue(serverUser.getServerUser()) + "'" +
                                ",serverPwd:'" + checkValue(serverUser.getServerPwd()) + "'" +
                                ",permission:" + serverUser.getPermission() +
                                ",dir:'" + checkValue(serverUser.getDir()) + "'},";
                        count ++;
                    }
                }
                json += "],count:" + count + "}";
            }
        }
        return json;
    }

    @Override
    public ClientUser findById(int id) {
        return clientUserDao.findById(id);
    }

    @Override
    public String checkByNamePwd(String userName, String userPwd) {
        ClientUser clientUser = null;
        String json = "认证失败,用户名不存在";
        clientUser = clientUserDao.findByProperty("userName",userName,0);
        if(clientUser != null){
            if(userPwd != null && userPwd.equals(clientUser.getUserPwd())){
                if(!Service.connectClients.containsKey(clientUser.getId())){
                    clientUser.setConnectTime(new Date());
                    clientUser.setDisconnectTime(new Date()); //断开连接时间=最后连接的时间
                    modify(clientUser);
                    Service.connectClients.put(clientUser.getId(),clientUser);
                }else{       //发送脉冲认证
                    clientUser = Service.connectClients.get(clientUser.getId());
                    clientUser.setDisconnectTime(new Date()); //断开连接时间=最后连接的时间
                    modify(clientUser);

                }
                //返回认证成功,和客户端的ID用于再次认证
                json = "{success:true,msg:'认证成功!',clientId:" + clientUser.getId()+ "}";
            }else{
                json = "密码错误!认证失败!";
            }
        }
        return json;
    }

    private String checkValue(String json){
        if(json == null){
            return "";
        }else {
            return json;
        }
    }
    public String checkTime(Date date){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(date == null){
            return "";
        }else {
            return sf.format(date);
        }

    }
}
