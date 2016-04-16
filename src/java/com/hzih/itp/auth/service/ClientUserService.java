package com.hzih.itp.auth.service;

import com.hzih.itp.auth.domain.ClientUser;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-4
 * Time: 上午11:04
 * To change this template use File | Settings | File Templates.
 */
public interface ClientUserService {

    public String add(ClientUser clientUser);

    public String modify(ClientUser clientUser);

    public String remove(int userId);

    public String findByPage(String userName,int pageIndex,int limit);

    public String findServers(int userId);

    public ClientUser findById(int id);

    public String checkByNamePwd(String userName,String userPwd);
}
