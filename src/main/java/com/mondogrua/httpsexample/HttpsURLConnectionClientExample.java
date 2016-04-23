package com.mondogrua.httpsexample;

import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/*
 * http://www.pixelstech.net/article/1445603357-A-HTTPS-client-and-HTTPS-server-demo-in-Java
 */

public class HttpsURLConnectionClientExample {

    private static final String ADDRESS = "127.0.0.1";
    private static final String PORT = "8000";
    private static final String TRUST_STORE_TYPE = "jks";
    private static final String TRUST_STORE_NAME = "testkey.jks";

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

        try {
            URL url = new URL("https://" + ADDRESS + ":" + PORT);
            HttpsURLConnection client = (HttpsURLConnection) url
                    .openConnection();

            System.out.println("RETURN : " + client.getResponseCode());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
