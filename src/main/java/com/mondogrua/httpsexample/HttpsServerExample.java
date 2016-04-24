package com.mondogrua.httpsexample;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

/*
 * http://stackoverflow.com/questions/2308479/simple-java-https-server
 *
 */
/* Use the following commands
keytool -genkey -keyalg RSA -alias server-cert -keystore server_keystore.jks -storepass serv_ks_pwd -keypass serv_key_pwd -validity 360 -keysize 2048 -dname "cn=Marco Testa, o=MondoGrua, c=IT"
keytool -export -alias server-cert -storepass serv_ks_pwd -file server.cer -keystore server_keystore.jks
keytool -import -file server.cer -alias server-cert -keystore client_truststore.jks -keypass cli_key_pwd -storepass cli_ts_pwd -noprompt
*/

@SuppressWarnings("restriction")
public class HttpsServerExample {

    private static final boolean NEED_CLIENT_AUTH = true;
    private static final String KEY_STORE_TYPE = "JKS";
    private static final String KEY_STORE_NAME = "server_keystore.jks";
    private static final char[] KEY_STORE_PWD = "serv_ks_pwd".toCharArray();
    private static final char[] KEY_PWD = "serv_key_pwd".toCharArray();
    private static final String TRUST_STORE_NAME = "server_truststore.jks";
    private static final char[] TRUST_STORE_PWD = "serv_ts_pwd".toCharArray();
    private static final int PORT = 8000;

    public static class MyHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "This is the response";
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        try {
            InetSocketAddress address = new InetSocketAddress(PORT);

            KeyManagerFactory keyMgrFactory = createKeyManagerFactory();
            TrustManagerFactory trustMgrFactory = createTrustManagerFactory();
            SSLContext sslContext = createSSLContext(keyMgrFactory,
                    trustMgrFactory);
            HttpsConfigurator httpsConfigurator = createHttpsConfigurator(
                    sslContext);

            HttpsServer httpsServer = HttpsServer.create(address, 0);
            httpsServer.setHttpsConfigurator(httpsConfigurator);
            httpsServer.createContext("/test", new MyHandler());
            httpsServer.setExecutor(null); // creates a default executor
            httpsServer.start();

        } catch (Exception exception) {
            System.out.println("Failed to create HTTPS server on port " + 8000
                    + " of localhost");
            exception.printStackTrace();

        }
    }

    private static SSLContext createSSLContext(
            KeyManagerFactory keyManagerFactory,
            TrustManagerFactory trustManagerFactory)
                    throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory
                .getTrustManagers(), null);
        return sslContext;
    }

    private static TrustManagerFactory createTrustManagerFactory()
            throws KeyStoreException, IOException, NoSuchAlgorithmException,
            CertificateException, FileNotFoundException {
        KeyStore trustStore = KeyStore.getInstance(KEY_STORE_TYPE);
        trustStore.load(new FileInputStream(TRUST_STORE_NAME), TRUST_STORE_PWD);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance("SunX509");
        trustManagerFactory.init(trustStore);
        return trustManagerFactory;
    }

    private static KeyManagerFactory createKeyManagerFactory()
            throws KeyStoreException, IOException, NoSuchAlgorithmException,
            CertificateException, FileNotFoundException,
            UnrecoverableKeyException {
        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
        keyStore.load(new FileInputStream(KEY_STORE_NAME), KEY_STORE_PWD);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                "SunX509");
        keyManagerFactory.init(keyStore, KEY_PWD);
        return keyManagerFactory;
    }

    private static HttpsConfigurator createHttpsConfigurator(
            SSLContext sslContext) {
        return new HttpsConfigurator(sslContext) {

            @Override
            public void configure(HttpsParameters params) {
                try {
                    SSLContext context = SSLContext.getDefault();
                    SSLParameters sslparams = context.getDefaultSSLParameters();
                    SSLEngine engine = context.createSSLEngine();
                    sslparams.setCipherSuites(engine.getEnabledCipherSuites());
                    sslparams.setProtocols(engine.getEnabledProtocols());
                    sslparams.setNeedClientAuth(NEED_CLIENT_AUTH);

                    params.setSSLParameters(sslparams);
                } catch (Exception ex) {
                    System.out.println("Failed to create HTTPS port");
                }
            }
        };
    }

}
