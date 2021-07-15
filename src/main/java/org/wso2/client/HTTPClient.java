package org.wso2.client;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HTTPClient {

    private static HTTPClient instance = null;

    private HTTPClient() throws NoSuchAlgorithmException, KeyManagementException {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = (hostname, session) -> true;

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    public static HTTPClient getInstance() throws KeyManagementException, NoSuchAlgorithmException {
        if (instance == null)
            instance = new HTTPClient();

        return instance;
    }

    public JSONObject sendGet(String urlString, Map<String, String> headers) throws IOException {
        URL url = new URL (urlString);
        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        for (String key : headers.keySet()) {
            con.setRequestProperty(key, headers.get(key));
        }
        con.setDoOutput(true);
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return new JSONObject(response.toString());
        }
    }

    public JSONObject sendPost(String urlString, String body, Map<String, String> headers, boolean isJsonBody)
            throws IOException {
        URL url = new URL (urlString);
        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept", "application/json");
        for (String key : headers.keySet()) {
            con.setRequestProperty(key, headers.get(key));
        }
        con.setDoOutput(true);
        if (isJsonBody) {
            con.setRequestProperty("Content-Type", "application/json");
        } else {
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        }
        if (body != null) {
            byte[] postData = body.getBytes(StandardCharsets.UTF_8);
            con.setRequestProperty("Content-Length", Integer.toString(postData.length));

            try(OutputStream os = con.getOutputStream()) {
                os.write(postData, 0, postData.length);
            }
        }

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return new JSONObject(response.toString());
        }
    }

    public JSONObject sendPut(String urlString, String apiDefinition, Map<String, String> headers)
            throws IOException {
        MultipartUtility multipart = new MultipartUtility(urlString, "utf-8", headers);

        multipart.addFormField("apiDefinition", apiDefinition);

        String response = multipart.finish();
        return new JSONObject(response);
    }

}
