package com.hzih.itp.auth.dao;

import cn.collin.commons.dao.BaseDao;
import cn.collin.commons.domain.PageResult;
import com.hzih.itp.auth.domain.ServerUser;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-4
 * Time: 下午4:04
 * To change this template use File | Settings | File Templates.
 */
public interface ServerUserDao extends BaseDao {
    PageResult findByPage(String serverName,String serverType,String serverUser,String dir,String permission,int pageIndex,int limit);

    PageResult findByServerName(String serverName,int pageIndex,int limit);

    List<ServerUser> findByPorperty(String propertyName,String propertyValue,int option);

    List<ServerUser> findAllServer();
}
