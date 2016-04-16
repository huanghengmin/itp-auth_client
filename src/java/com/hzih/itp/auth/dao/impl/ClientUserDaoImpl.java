package com.hzih.itp.auth.dao.impl;

import cn.collin.commons.dao.MyDaoSupport;
import cn.collin.commons.domain.PageResult;
import com.hzih.itp.auth.dao.ClientUserDao;
import com.hzih.itp.auth.domain.ClientUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-4
 * Time: 上午10:56
 * To change this template use File | Settings | File Templates.
 */
public class ClientUserDaoImpl extends MyDaoSupport implements ClientUserDao {

    @Override
    public void setEntityClass() {
        this.entityClass = ClientUser.class;
    }

    @Override
    public PageResult findByPage(String userName,int pageIndex,int limit) throws Exception {
        String hql = " from ClientUser where 1=1 ";
        List params = new ArrayList();
        if(userName != null && userName.length() > 0){
            hql += " and userName like ?";
            params.add("%" + userName + "%");
        }
        String countHql = "select count(*) " + hql;

        PageResult ps = this.findByPage(hql,countHql,params.toArray(),pageIndex,limit);
        return ps;

    }

    @Override
    public ClientUser findById(int id) {
        String hql = " from ClientUser where id= " + id;
        List<ClientUser> clientUsers = getHibernateTemplate().find(hql);
        if(clientUsers != null){
            return clientUsers.get(0);
        }else {
            return null;
        }
    }

    /**
     *
     * @param propertyName   列名
     * @param propertyValue   列值
     * @param option         模糊查询或等值查询(option=0:等值查询,option=1:模糊查询)
     * @return
     */
    @Override
    public ClientUser findByProperty(String propertyName, String propertyValue, int option) {
        String hql = " from ClientUser";
        List<ClientUser> clientUsers = null;
        if(propertyName != null && propertyName.length() > 0){
            if(option == 0){
                hql += " where " + propertyName + " = '" + propertyValue + "'";
            }else {
                hql += " where " + propertyName + " like '%" + propertyValue + "%'";
            }
            clientUsers = getHibernateTemplate().find(hql);
        }
        if(clientUsers != null){
            return clientUsers.get(0);
        }else {
            return null;
        }
    }
}
