package com.mondogrua.httpsexample;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;

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
 * keytool -genkey -keyalg RSA -alias selfsigned -keystore server_keystore.jks -storepass password -validity 360 -keysize 2048
 * keytool -export -alias selfsigned -storepass password -file server.cer -keystore server_keystore.jks
 * keytool -import -file server.cer -alias firstCA -keystore client_truststore.jks -keypass password -storepass qwerty
*/

@SuppressWarnings("restriction")
public class HttpsServerExample {

    private static final String KEY_STORE_TYPE = "JKS";
    private static final String KEY_STORE_NAME = "server_keystore.jks";
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
            // setup the socket address
            InetSocketAddress address = new InetSocketAddress(PORT);

            // initialise the HTTPS server
            HttpsServer httpsServer = HttpsServer.create(address, 0);
            SSLContext sslContext = SSLContext.getInstance("TLS");

            // initialise the keystore
            char[] password = "password".toCharArray();
            KeyStore ks = KeyStore.getInstance(KEY_STORE_TYPE);
            FileInputStream fis = new FileInputStream(KEY_STORE_NAME);
            ks.load(fis, password);

            // setup the key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);

            // setup the trust manager factory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                    "SunX509");
            tmf.init(ks);

            // setup the HTTPS context and parameters
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {

                @Override
                public void configure(HttpsParameters params) {
                    try {
                        SSLContext c = SSLContext.getDefault();

                        // get the default parameters
                        SSLParameters sslparams = c.getDefaultSSLParameters();

                        // initialise the SSL context
                        SSLEngine engine = c.createSSLEngine();
                        sslparams.setNeedClientAuth(true);
                        sslparams.setCipherSuites(engine
                                .getEnabledCipherSuites());
                        sslparams.setProtocols(engine.getEnabledProtocols());

                        params.setSSLParameters(sslparams);
                    } catch (Exception ex) {
                        System.out.println("Failed to create HTTPS port");
                    }
                }
            });
            httpsServer.createContext("/test", new MyHandler());
            httpsServer.setExecutor(null); // creates a default executor
            httpsServer.start();

        } catch (Exception exception) {
            System.out.println("Failed to create HTTPS server on port " + 8000
                    + " of localhost");
            exception.printStackTrace();

        }
    }

}
