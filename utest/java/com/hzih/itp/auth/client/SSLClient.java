package com.hzih.itp.auth.client;

import com.hzih.itp.auth.utils.StaticField;

import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-6
 * Time: 下午3:08
 * To change this template use File | Settings | File Templates.
 */
public class SSLClient
{
    static int port=6666;
    public static void main(String args[])
    {
        try
        {
            SSLSocketFactory factory=(SSLSocketFactory)SSLSocketFactory.getDefault();

            Socket s=factory.createSocket("localhost",port);

            PrintWriter out=new PrintWriter(s.getOutputStream(),true);

            out.println("{\"command\":\"" + StaticField.CONNECT + "\",\"clientUser\":\"ztt\",\"clientPwd\"=\"123qwe!@#\"}");
//            out.println("安全的说你好");
            out.flush();
            byte[] buf = new byte[1024];
            Thread.sleep(1000);
            BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream(),"GBK"));
            String msg=in.readLine();
            System.out.println(msg);
            in.close();
            out.close();
            s.close();
        }catch(Exception e)
        {
            System.out.println(e);
        }



        /*X509TrustManager x509m = new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        };
        try{
            SSLContext context = SSLContext.getInstance("SSL");
            // 初始化
            context.init(null,
                    new TrustManager[] { x509m },
                    new SecureRandom());
            SSLSocketFactory factory = context.getSocketFactory();
            SSLSocket s = (SSLSocket) factory.createSocket("192.168.1.128", 6666);
            System.out.println("ok");

            OutputStream output = s.getOutputStream();
            InputStream input = s.getInputStream();

            output.write("alert".getBytes());
            System.out.println("sent: alert");
            output.flush();

            byte[] buf = new byte[1024];
            int len = input.read(buf);
            System.out.println("received:" + new String(buf, 0, len));
            if(output != null){
                output.close();
            }
            if(input != null){
                input.close();
            }
            if(s != null){
                s.close();
            }

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (KeyManagementException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }*/


    }
}
