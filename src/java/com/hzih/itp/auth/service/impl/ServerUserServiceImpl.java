package com.hzih.itp.auth.service.impl;

import cn.collin.commons.domain.PageResult;
import com.hzih.itp.auth.dao.ClientUserDao;
import com.hzih.itp.auth.dao.ServerUserDao;
import com.hzih.itp.auth.domain.ClientUser;
import com.hzih.itp.auth.domain.ServerUser;
import com.hzih.itp.auth.service.ServerUserService;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-4
 * Time: 下午4:18
 * To change this template use File | Settings | File Templates.
 */
public class ServerUserServiceImpl implements ServerUserService {

    private ServerUserDao serverUserDao;

    private ClientUserDao clientUserDao;

    public ClientUserDao getClientUserDao() {
        return clientUserDao;
    }

    public void setClientUserDao(ClientUserDao clientUserDao) {
        this.clientUserDao = clientUserDao;
    }

    public ServerUserDao getServerUserDao() {
        return serverUserDao;
    }

    public void setServerUserDao(ServerUserDao serverUserDao) {
        this.serverUserDao = serverUserDao;
    }

    @Override
    public String findByPage(String serverName, String serverType, String serverUser, String dir, String permission, int pageIndex, int limit) {
        String json = null;
        PageResult ps = null;
        ps = serverUserDao.findByPage(serverName, serverType, serverUser, dir, permission, pageIndex, limit);
        if(ps != null){
            json = "{success:true,count:" + ps.getAllResultsAmount() + ",rows:[";
            List<ServerUser> serverUsers = ps.getResults();
            if(serverUsers != null && serverUsers.size() > 0){
                for(ServerUser server:serverUsers){
                    json += "{id:" + server.getId() +
                            ",serverName:'" + checkValue(server.getServerName()) + "'" +
                            ",serverType:" + server.getServerType()  +
                            ",ipPort:'" + checkValue(server.getIpPort()) +  "'" +
                            ",createTime:'" + checkDate(server.getCreateTime()) + "'},";
                }
            }
            json += "]}";
        }
        return json;
    }

    @Override
    public String findByServerName(String serverName, int pageIndex, int limit) {
        String json = null;
        PageResult ps = null;
        ps = serverUserDao.findByServerName(serverName, pageIndex, limit);
        if(ps != null){
            json = "{success:true,count:" + ps.getAllResultsAmount() + ",rows:[";
            List<ServerUser> serverUsers = ps.getResults();
            if(serverUsers != null && serverUsers.size() > 0){
                for(ServerUser server:serverUsers){
                    json += "{serverId:" + server.getId() +
                            ",serverName:'" + checkValue(server.getServerName()) + "'" +
                            ",serverUser:'" + checkValue(server.getServerUser())  + "'" +
                            ",permission:" + server.getPermission()  +
                            ",dir:'" + checkValue(server.getDir()) + "'" +
                            ",createTime:'" + checkDate(server.getCreateTime()) + "'},";
                }
            }
            json += "]}";
        }
        return json;
    }

    @Override
    public String findByServerUser(int id) {
        ServerUser serverUser = null;
        String json = null;
        serverUser = findById(id);
        if(serverUser != null){
            Set<ClientUser> clients = serverUser.getClients();
            if(clients != null){
                json = "{success:true,count:" + clients.size() + ",rows:[";
                if(clients.size() > 0){
                    for(ClientUser client:clients){
                        json += "{id1:" + id +
                                ",serverName:'" + checkValue(serverUser.getServerName()) + "'" +
                                ",clientId:" + client.getId() +
                                ",userName:'" + checkValue(client.getUserName()) + "'" +
                                ",ip:'" + checkValue(client.getIp()) + "'" +
                                ",mac:'" + checkValue(client.getMac()) + "'" +
                                ",connectTime:'" + checkDate(client.getConnectTime()) + "'},";
                    }
                }
                json += "]}";
            }
        }
        return json;
    }

    @Override
    public String findClientName(int id) {
        ServerUser serverUser = null;
        String json = null;
        serverUser = findById(id);
        List<ClientUser> allClients = clientUserDao.findAll();
        if(serverUser != null){
            Set<ClientUser> clients = serverUser.getClients();
            Map<Integer,ClientUser> existClient = new HashMap<Integer, ClientUser>();
            if(clients != null && clients.size() > 0){
                for(ClientUser client:clients){
                    if(client != null){
                        existClient.put(client.getId(),client);
                    }
                }
            }
            if(allClients != null && allClients.size() > 0){
                json = "{success:true,rows:[";
                int count = 0;
                for(ClientUser client:allClients){
                    if(!existClient.containsKey(client.getId())){
                        count ++;
                        json += "{value:" + client.getId() + ",key:'" + client.getUserName() + "'},";
                    }
                }
                json += "],count:" + count + "}";
            }
        }
        return json;
    }

    @Override
    public String findServerName() {
        List<ServerUser> serverUsers = null;
        String json = null;
        serverUsers = serverUserDao.findAllServer();
        if(serverUsers != null){
            json = "{success:true,count:" + serverUsers.size() + ",rows:[";
            for(ServerUser serverUser:serverUsers){
                json += "{serverId:" + serverUser.getId() + ",serverName:'" + checkValue(serverUser.getServerName()) + "'},";
            }
            json += "]}";
        }
        return json;
    }

    @Override
    public ServerUser findById(int id) {
        return (ServerUser)serverUserDao.getById(id);
    }

    /**
     * 验证该服务名是否可以使用,true表示可以使用,false表示已存在该服务名
     * @param serverName
     * @return
     */
    @Override
    public boolean checkServerName(String serverName) {
        List<ServerUser> serverUsers = null;
        if(serverName != null && serverName.length() > 0){
            serverUsers = serverUserDao.findByPorperty("serverName",serverName,0);
        }
        if(serverUsers == null || serverUsers.size() == 0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean checkServerUser(String serverName, String serverUser) {
        List<ServerUser> serverUsers = null;
        if(serverName != null && serverName.length() > 0){
            serverUsers = serverUserDao.findByPorperty("serverName",serverName,0);
            if(serverUsers!= null && serverUsers.size() > 0){
                for(int i =0;i<serverUsers.size() ; i++){
                    String user = serverUsers.get(i).getServerUser();
                    if(user != null && user.equals(serverUser) ){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean checkIpport(String ipport) {
        List<ServerUser> serverUsers = null;
        if(ipport != null && ipport.length() > 0){
            serverUsers = serverUserDao.findByPorperty("ipPort", ipport, 0);
        }
        if(serverUsers == null || serverUsers.size() == 0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public String add(ServerUser serverUser) {
        String json = "新增失败!";
        try{
            serverUserDao.create(serverUser);
            json = "新增成功!";
        }catch (Exception e){
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public String modify(ServerUser serverUser) {
        String json = "修改失败!";
        try{
            serverUserDao.update(serverUser);
            json = "修改成功!";
        }catch (Exception e){
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public String remove(int id) {
        String json = "删除失败!";
        try{
            serverUserDao.delete(id);
            json = "删除成功!";
        }catch (Exception e){
            e.printStackTrace();
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
