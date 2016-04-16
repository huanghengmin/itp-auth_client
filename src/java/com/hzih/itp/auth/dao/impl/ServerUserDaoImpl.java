package com.hzih.itp.auth.dao.impl;

import cn.collin.commons.dao.MyDaoSupport;
import cn.collin.commons.domain.PageResult;
import com.hzih.itp.auth.dao.ServerUserDao;
import com.hzih.itp.auth.domain.ServerUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-4
 * Time: 下午4:07
 * To change this template use File | Settings | File Templates.
 */
public class ServerUserDaoImpl extends MyDaoSupport implements ServerUserDao {
    @Override
    public void setEntityClass() {
        this.entityClass = ServerUser.class;
    }

    @Override
    public PageResult findByPage(String serverName, String serverType, String serverUser, String dir, String permission, int pageIndex, int limit) {
        String hql = " from ServerUser where 1=1 ";
        List params = new ArrayList();
        if(serverName != null && serverName.length() > 0){
            hql += " and serverName like ?";
            params.add("%" + serverName + "%");
        }
        if(serverType != null && serverType.length() > 0){
            hql += " and serverType = ?";
            params.add(Integer.parseInt(serverType));
        }
        if(serverUser != null && serverUser.length() > 0){
            hql += " and serverUser like ?";
            params.add("%" + serverUser + "%");
        }
        if(dir != null && dir.length() > 0){
            hql += " and dir like ?";
            params.add("%" + dir + "%");
        }
        if(permission != null && permission.length() > 0){
            hql += " and permission = ?";
            params.add(Integer.parseInt(permission));
        }
        if(serverName == null || serverName.length() <= 0){
            hql += " group by serverName";
        }
        String countHql = "select count(*) " + hql;

        PageResult ps = this.findByPage(hql,countHql,params.toArray(),pageIndex,limit);
        return ps;
    }

    @Override
    public PageResult findByServerName(String serverName, int pageIndex, int limit) {
        String hql = " from ServerUser where 1=1";
        List params = new ArrayList();
        if(serverName != null && serverName.length() > 0){
            hql += " and serverName = ?";
            params.add(serverName);
            hql += " and serverUser is not null";
        }
        String countHql = "select count(*) " + hql;

        PageResult ps = this.findByPage(hql,countHql,params.toArray(),pageIndex,limit);
        return ps;
    }

    /**
     *
     * @param propertyName      列名
     * @param propertyValue     列值
     * @param option           操作 :是用=(0) 还是like(1)
     * @return
     */
    @Override
    public List<ServerUser> findByPorperty(String propertyName, String propertyValue,int option) {
        String hql = " from ServerUser where 1=1";
        if(propertyName != null && propertyName.length() > 0){
            if(option == 0){
                hql += " and " + propertyName + " = '" + propertyValue + "'";
            }else {
                hql += " and " + propertyName + " like '%" + propertyValue + "%'";
            }
        }
        List<ServerUser> serverUsers = getHibernateTemplate().find(hql);
        return serverUsers;
    }

    @Override
    public List<ServerUser> findAllServer() {
        String hql = " from ServerUser where 1=1 group by serverName";
        List<ServerUser> serverUsers = getHibernateTemplate().find(hql);
        return serverUsers;

    }
}
