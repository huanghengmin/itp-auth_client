package com.hzih.itp.auth.service;

public interface LogService {


    public void newSysLog(String level, String auditModule, String auditAction, String auditinfo);

}
