package com.hzih.itp.auth.service;

import com.hzih.itp.auth.domain.ServerUser;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-4
 * Time: 下午4:17
 * To change this template use File | Settings | File Templates.
 */
public interface ServerUserService {

    String findByPage(String serverName, String serverType, String serverUser, String dir, String permission, int pageIndex, int limit );

    String findByServerName(String serverName,int pageIndex,int limit);

    String findByServerUser(int id);

    String findClientName(int id);

    String findServerName();

    ServerUser findById(int id);

    boolean checkServerName(String serverName);

    boolean checkServerUser(String serverName,String serverUser);

    boolean checkIpport(String ipport);

    String add(ServerUser serverUser);

    String modify(ServerUser serverUser);

    String remove(int id);

}
