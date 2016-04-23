package com.mondogrua.httpsexample;

import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/*
 * http://www.pixelstech.net/article/1445603357-A-HTTPS-client-and-HTTPS-server-demo-in-Java
 *
 * keytool -genkey -keyalg RSA -alias client-key -keystore client_keystore.jks -storepass qwerty -keypass qwerty
 * keytool -export -alias client-key -storepass qwerty -file client.cer -keystore client_keystore.jks
 * keytool -import -v -trustcacerts -alias client-key -file client.cer -keystore server_keystore.jks -keypass qwerty -storepass password

 */

public class HttpsURLConnectionClientExample {

    private static final String ADDRESS = "127.0.0.1";
    private static final String PORT = "8000";
    private static final String TRUST_STORE_TYPE = "jks";
    private static final String TRUST_STORE_NAME = "client_truststore.jks";
    private static final String TRUST_STORE_PWD = "qwerty";
    private static final String KEY_STORE_PWD = "qwerty";
    private static final String KEY_STORE_NAME = "client_keystore.jks";

    // Disable the hostname verification for demo purpose
    static {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });
    }

    public static void main(String[] args) {
        // Initialize configuration
        System.setProperty("javax.net.ssl.trustStore", TRUST_STORE_NAME);
        System.setProperty("javax.net.ssl.trustStoreType", TRUST_STORE_TYPE);
        System.setProperty("javax.net.ssl.trustStorePassword", TRUST_STORE_PWD);

        System.setProperty("javax.net.ssl.keyStore", KEY_STORE_NAME);
        System.setProperty("javax.net.ssl.keyStorePassword", KEY_STORE_PWD);

        try {
            URL url = new URL("https://" + ADDRESS + ":" + PORT + "/test");
            HttpsURLConnection client = (HttpsURLConnection) url
                    .openConnection();

            System.out.println("RETURN : " + client.getResponseCode());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
