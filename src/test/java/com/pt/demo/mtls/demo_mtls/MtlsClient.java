package com.pt.demo.mtls.demo_mtls;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyStore;

public class MtlsClient {

    public static void main(String[] args) throws Exception {
        // Load client keystore
        /*KeyStore clientKeyStore = KeyStore.getInstance("JKS");
        try (FileInputStream keyStoreStream = new FileInputStream("certs/client-keystore.jks")) {
            clientKeyStore.load(keyStoreStream, "clientpass".toCharArray());
        }*/

        // Load client truststore
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (FileInputStream trustStoreStream = new FileInputStream("certs/client-truststore.jks")) {
            trustStore.load(trustStoreStream, "trustpass".toCharArray());
        }

        // Initialize KeyManagerFactory
        /*KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(clientKeyStore, "clientpass".toCharArray());*/

        // Initialize TrustManagerFactory
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        // Create SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        //sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

        // Create HttpClient with SSLContext
        HttpClient client = HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();

        // Create and send request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://localhost:8443/api/demo"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response: " + response.body());
    }
}