package com.mondogrua.httpsexample;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/*
 * http://www.pixelstech.net/article/1445603357-A-HTTPS-client-and-HTTPS-server-demo-in-Java
 *
 */
/* Use the following commands
keytool -genkey -keyalg RSA -alias client-cert -keystore client_keystore.jks -storepass cli_ks_pwd -keypass cli_ks_pwd -validity 360 -keysize 2048 -dname "cn=Marco Testa, o=MondoGrua, c=IT"
keytool -export -alias client-cert -storepass cli_ks_pwd -file client.cer -keystore client_keystore.jks
keytool -import -v -trustcacerts -alias client-cert -file client.cer -keystore server_truststore.jks -keypass serv_key_pwd -storepass serv_ts_pwd -noprompt
*/

public class HttpsURLConnectionClientExample {

    private static final String ADDRESS = "127.0.0.1";
    private static final String PORT = "8000";
    private static final String KEY_STORE_TYPE = "jks";
    private static final String KEY_STORE_NAME = "client_keystore.jks";
    private static final String KEY_STORE_PWD = "cli_ks_pwd";
    private static final String TRUST_STORE_NAME = "client_truststore.jks";
    private static final String TRUST_STORE_PWD = "cli_ts_pwd";

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
        System.setProperty("javax.net.ssl.trustStore", TRUST_STORE_NAME);
        System.setProperty("javax.net.ssl.trustStoreType", KEY_STORE_TYPE);
        System.setProperty("javax.net.ssl.trustStorePassword", TRUST_STORE_PWD);
        System.setProperty("javax.net.ssl.keyStore", KEY_STORE_NAME);
        System.setProperty("javax.net.ssl.keyStorePassword", KEY_STORE_PWD);

        try {
            URL url = new URL("https://" + ADDRESS + ":" + PORT + "/test");
            HttpsURLConnection client = (HttpsURLConnection) url
                    .openConnection();

            System.out.println("Response Code : " + client.getResponseCode());

            InputStream inputstream = client.getInputStream();
            InputStreamReader inputstreamreader = new InputStreamReader(
                    inputstream);
            BufferedReader bufferedreader = new BufferedReader(
                    inputstreamreader);

            String string = null;
            while ((string = bufferedreader.readLine()) != null) {
                System.out.println("Response content : " + string);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
