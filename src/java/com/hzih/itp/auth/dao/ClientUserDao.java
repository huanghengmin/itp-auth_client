package com.hzih.itp.auth.dao;

import cn.collin.commons.dao.BaseDao;
import cn.collin.commons.domain.PageResult;
import com.hzih.itp.auth.domain.ClientUser;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-4
 * Time: 上午10:52
 * To change this template use File | Settings | File Templates.
 */
public interface ClientUserDao extends BaseDao {

    PageResult findByPage(String userName,int pageIndex,int limit) throws Exception;

    ClientUser findById(int id);

    ClientUser findByProperty(String propertyName,String propertyValue,int option);
}
