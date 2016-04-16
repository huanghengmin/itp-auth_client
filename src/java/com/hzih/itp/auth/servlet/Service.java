package com.hzih.itp.auth.servlet;

import com.hzih.itp.auth.domain.ClientUser;
import com.hzih.itp.auth.utils.StaticField;
import com.inetec.common.util.OSInfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-6
 * Time: 上午11:23
 * To change this template use File | Settings | File Templates.
 */
public class Service extends HttpServlet {

    private static boolean isRunPortListen = false;
    private static boolean isRunRZPortListen = false;
    private static PortListenService portListenService = new PortListenService();
    private static RZPortListenService rzPortListenService = new RZPortListenService();

    public static Map<Integer,ClientUser> connectClients ;

    public Service() {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();

        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>ITP Service Page</title>");
        writer.println("</head>");
        writer.println("<body bgcolor=white>");
        writer.println("<table border=\"0\">");
        writer.println("<tr>");
        writer.println("<td>");
        writer.println("<h1>ITP Service  Status Page</h1>");
        writer.println("<P>Service is running.<P><BR>");
        writer.println("</td>");
        writer.println("</tr>");
        writer.println("</table>");
        writer.println("</body>");
        writer.println("</html>");
    }


    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String command = request.getParameter(StaticField.Command);

        /*if(StaticField.SendConfig.equals(command)) {
            Service.configService.query.offer(command);
        } else if(StaticField.AlertConfig.equals(command)) {

        } else if(StaticField.SendChannelTest.equals(command)) {

        }*/

        byte[] data = (command + " is ok!").getBytes();
        response.setContentLength(data.length);
        response.getOutputStream().write(data);

        response.flushBuffer();
        response.setStatus(HttpServletResponse.SC_OK);
    }

    public void init() {
        //初始化已连接的客户端
        connectClients = new HashMap<Integer, ClientUser>();
        String platformType = getPlatformType();         //主机名字
        String platformName = null;
        if(StaticField.Platform_Type_EX_ITP.equals(platformType)) {
            platformName = StaticField.Auth_Type_S;
            System.out.println("启动 服务器 认证端口监听 开始. . .");
            runRZPortListen();
            System.out.println("启动 服务器 认证端口监听 成功. . .");

            System.out.println("启动 服务端 监听端口 开始. . .");
            runPortListen();
            System.out.println("启动 服务端 监听端口 成功. . .");
        } else if(StaticField.Platform_Type_EX_STP.equals(platformType)) {
            platformName = StaticField.Auth_Type_S;
            System.out.println("启动 服务器 认证端口监听 开始. . .");
            runRZPortListen();
            System.out.println("启动 服务器 认证端口监听 成功. . .");

            System.out.println("启动 服务端 监听端口 开始. . .");
            runPortListen();
            System.out.println("启动 服务端 监听端口 成功. . .");
        } else if(StaticField.Platform_Type_IN_STP.equals(platformType)) {
            platformName = StaticField.Auth_Type_S;
            System.out.println("启动 服务器 认证端口监听 开始. . .");
            runRZPortListen();
            System.out.println("启动 服务器 认证端口监听 成功. . .");

            System.out.println("启动 服务端 监听端口 开始. . .");
            runPortListen();
            System.out.println("启动 服务端 监听端口 成功. . .");
        } else if(StaticField.Platform_Type_IN_ITP.equals(platformType)) {
            platformName = StaticField.Auth_Type_S;
            System.out.println("启动 服务器 认证端口监听 开始. . .");
            runRZPortListen();
            System.out.println("启动 服务器 认证端口监听 成功. . .");

            System.out.println("启动 服务端 监听端口 开始. . .");
            runPortListen();
            System.out.println("启动 服务端 监听端口 成功. . .");
        } else{
            platformName = StaticField.Auth_type_C;

        }

    }

    private String getPlatformType() {
        OSInfo os = OSInfo.getOSInfo();
        if(os.isLinux()) {
            return os.getHostName();
        }
        return "client";
    }

    private void runPortListen(){
        if(Service.isRunPortListen){
            return;
        }else {
            portListenService.init();
            Thread thread = new Thread(portListenService);
            thread.start();
            Service.isRunPortListen = true;
        }
    }

    private void runRZPortListen(){
        if(Service.isRunRZPortListen){
            return;
        }else {
            rzPortListenService.init(6666);
            Thread thread = new Thread(rzPortListenService);
            thread.start();
            Service.isRunRZPortListen = true;
        }
    }

}
