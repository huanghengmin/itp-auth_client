package com.hzih.itp.auth;

import org.apache.mina.filter.ssl.SslContextFactory;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyStore;

public class SSLClient {
    
    private static String remote_SSLServer = "192.168.1.128";
    private static int remote_SSLPort = 6665;
    

    private static String TRUST_STORE = "D:\\cert\\TrustKey.keystore";
    private static String TRUST_KEY_STORE_PASSWORD = "123qwe";

    private static String CLIENT_KEY_STORE = "D:\\cert\\fJuly1_666612345678945612.pfx";
    private static String CLIENT_KEY_STORE_PASSWORD = "123qwe";

    public static void main(String[] args) throws Exception {
//        System.setProperty("javax.net.ssl.trustStore", TRUST_STORE);
//        System.setProperty("javax.net.debug", "ssl,handshake");
        SSLClient client = new SSLClient();
        Socket s = client.clientWithCert();
        PrintWriter writer = new PrintWriter(s.getOutputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        writer.println("hello\n");
        writer.write("fdasfdsafdsaaaaaaaafdsafdsfdsfdsfds\n");
        writer.write("fdasfdsafdsfsdfsadfds");
        writer.flush();
        System.out.println(reader.readLine());
        s.close();
    }

    private Socket clientWithCert() throws Exception {
        int x=CLIENT_KEY_STORE.lastIndexOf(".") ;
        String s=CLIENT_KEY_STORE.substring(x+1,CLIENT_KEY_STORE.length()) ;

        SSLContext key = null;

        SslContextFactory sslcontextFactory = new SslContextFactory();
        sslcontextFactory.setProtocol("TLS");
        KeyStore ks = null;
        if(s.equals("p12")||s.equals("pfx")){
            ks = KeyStore.getInstance("pkcs12");
            ks.load(new FileInputStream(CLIENT_KEY_STORE), CLIENT_KEY_STORE_PASSWORD.toCharArray());
        }
        if (s.equals("jks")||s.equals("keystore")){
            ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(CLIENT_KEY_STORE), CLIENT_KEY_STORE_PASSWORD.toCharArray());
        }


        KeyStore jks = KeyStore.getInstance("JKS");
        jks.load(new FileInputStream(TRUST_STORE), TRUST_KEY_STORE_PASSWORD.toCharArray());


        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, CLIENT_KEY_STORE_PASSWORD.toCharArray());

        TrustManagerFactory tmFact = TrustManagerFactory.getInstance("SunX509");
        tmFact.init(jks);

        sslcontextFactory.setTrustManagerFactory(tmFact);
        sslcontextFactory.setTrustManagerFactoryKeyStore(jks);         //可信证书
        sslcontextFactory.setKeyManagerFactoryKeyStore(ks);              //匹配证书
        sslcontextFactory.setKeyManagerFactoryKeyStorePassword(CLIENT_KEY_STORE_PASSWORD);
        key = sslcontextFactory.newInstance();

        SocketFactory factory = key.getSocketFactory();
        Socket socket = factory.createSocket(remote_SSLServer, remote_SSLPort);
        return socket;
    }
}