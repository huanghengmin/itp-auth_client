package com.hzih.itp.auth.dao.impl;

import cn.collin.commons.dao.MyDaoSupport;
import com.hzih.itp.auth.dao.SysLogDao;
import com.hzih.itp.auth.domain.SysLog;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-8
 * Time: 上午9:34
 * To change this template use File | Settings | File Templates.
 */
public class SysLogDaoImpl extends MyDaoSupport implements SysLogDao {
    @Override
    public void setEntityClass() {
        this.entityClass = SysLog.class;
    }
}
