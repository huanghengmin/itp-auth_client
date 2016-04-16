package com.hzih.itp.auth.service.impl;

import com.hzih.itp.auth.dao.SysLogDao;
import com.hzih.itp.auth.domain.SysLog;
import com.hzih.itp.auth.service.LogService;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-8
 * Time: 上午9:29
 * To change this template use File | Settings | File Templates.
 */
public class LogServiceImpl implements LogService {

    private SysLogDao sysLogDao;

    public SysLogDao getSysLogDao() {
        return sysLogDao;
    }

    public void setSysLogDao(SysLogDao sysLogDao) {
        this.sysLogDao = sysLogDao;
    }

    @Override
    public void newSysLog(String level, String auditModule, String auditAction, String auditinfo) {

        SysLog sysLog = new SysLog();
        sysLog.setLogTime(new Date());
        sysLog.setLevel(level);
        sysLog.setAuditModule(auditModule);
        sysLog.setAuditAction(auditAction);
        sysLog.setAuditInfo(auditinfo);
        try{
            sysLogDao.create(sysLog);
        } catch (Exception e) {
//            LogLayout.error(logger,"itp","新增系统日志",e);
            e.printStackTrace();
        }
    }
}
